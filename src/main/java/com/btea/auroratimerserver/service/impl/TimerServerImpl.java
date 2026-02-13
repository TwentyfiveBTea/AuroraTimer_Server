package com.btea.auroratimerserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.btea.auroratimerserver.common.constant.RedisCacheConstant;
import com.btea.auroratimerserver.dao.entity.TimerRecordsDO;
import com.btea.auroratimerserver.dao.entity.TimerSummaryDO;
import com.btea.auroratimerserver.dao.mapper.TimerRecordsMapper;
import com.btea.auroratimerserver.dao.mapper.TimerSummaryMapper;
import com.btea.auroratimerserver.req.EditWeeklyTargetDurationReq;
import com.btea.auroratimerserver.req.ExcelDataReq;
import com.btea.auroratimerserver.req.SelectWeeklyTargetDurationReq;
import com.btea.auroratimerserver.req.TimeAddReq;
import com.btea.auroratimerserver.service.TimerServer;
import com.btea.auroratimerserver.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.TimeUnit;;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:10
 * @Description: 计时器接口层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimerServerImpl extends ServiceImpl<TimerRecordsMapper, TimerRecordsDO> implements TimerServer {

    private final TimerRecordsMapper timerRecordsMapper;
    private final TimerSummaryMapper timerSummaryMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 离线超时阈值（秒）- 15分钟
     */
    private static final long OFFLINE_THRESHOLD_SECONDS = 900L;

    /**
     * 获取计时器目标时长
     *
     * @param userId 用户 ID
     * @return 目标时长
     */
    @Override
    public Integer getTimerTarget(String userId) {
        LambdaQueryWrapper<TimerSummaryDO> queryWrapper = Wrappers.lambdaQuery(TimerSummaryDO.class)
                .eq(TimerSummaryDO::getUserId, userId);
        TimerSummaryDO summary = timerSummaryMapper.selectOne(queryWrapper);
        return summary != null ? summary.getWeeklyTargetDuration() : 0;
    }

    /**
     * 同步工时到服务器
     * <p>
     * 核心逻辑：
     * 1. 首次打卡：创建新记录，返回本次秒数
     * 2. 正常补时（间隔 < 900秒）：补时 = 实际经过的秒数
     * 3. 重新上线（间隔 >= 900秒）：只加固定的 60 秒
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TimeAddVO addTime(TimeAddReq requestParam) {
        String userId = requestParam.getUserId();
        Integer seconds = requestParam.getSeconds();
        long now = System.currentTimeMillis();

        log.debug("同步工时: userId={}, seconds={}", userId, seconds);

        // 刷新用户在线状态（每个用户独立的 key，延长60秒过期）
        stringRedisTemplate.opsForValue().set(
                RedisCacheConstant.USER_ONLINE_KEY + userId,
                "1",
                RedisCacheConstant.ONLINE_EXPIRE_SECONDS,
                TimeUnit.SECONDS
        );

        // 查询是否有进行中的记录
        LambdaQueryWrapper<TimerRecordsDO> queryWrapper = Wrappers.lambdaQuery(TimerRecordsDO.class)
                .eq(TimerRecordsDO::getUserId, userId)
                .eq(TimerRecordsDO::getIsActive, 1)
                .isNull(TimerRecordsDO::getEndTime)
                .orderByDesc(TimerRecordsDO::getStartTime)
                .last("LIMIT 1");
        TimerRecordsDO record = timerRecordsMapper.selectOne(queryWrapper);

        int addedSeconds;

        if (record == null) {
            // 首次打卡
            record = TimerRecordsDO.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .startTime(new Date(now - seconds * 1000L))
                    .endTime(new Date(now))
                    .duration(seconds)
                    .isActive(1)
                    .build();
            timerRecordsMapper.insert(record);
            addedSeconds = seconds;
        } else {
            // 已有记录，计算时间差
            long recordTime = record.getEndTime().getTime();
            long intervalTime = now - recordTime;
            long intervalSeconds = intervalTime / 1000;

            if (intervalSeconds < OFFLINE_THRESHOLD_SECONDS) {
                // 正常补时
                addedSeconds = (int) intervalSeconds;
                log.info("用户 {} 正常补时: +{}秒 (间隔{}秒)", userId, addedSeconds, intervalSeconds);
            } else {
                // 重新上线
                addedSeconds = seconds;
                log.info("用户 {} 重新上线: +{}秒 (间隔{}秒)", userId, addedSeconds, intervalSeconds);
            }

            // 更新记录
            record.setEndTime(new Date(now));
            record.setDuration(record.getDuration() + addedSeconds);
            timerRecordsMapper.updateById(record);
        }

        // 更新汇总表
        LambdaUpdateWrapper<TimerSummaryDO> updateWrapper = Wrappers.lambdaUpdate(TimerSummaryDO.class)
                .eq(TimerSummaryDO::getUserId, userId)
                .setSql("total_seconds = total_seconds + " + addedSeconds)
                .setSql("week_seconds = week_seconds + " + addedSeconds);
        timerSummaryMapper.update(null, updateWrapper);

        // 计算本周总时长
        Integer serverWeekTime = getWeekTime(userId);

        return TimeAddVO.builder()
                .addedSeconds(addedSeconds)
                .serverWeekTime(serverWeekTime)
                .build();
    }

    /**
     * 计算本周总时长
     */
    private Integer getWeekTime(String userId) {
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDateTime startOfWeek = LocalDateTime.of(weekStart, LocalTime.MIN);
        LocalDateTime endOfWeek = LocalDateTime.of(now.plusDays(6), LocalTime.MAX);

        LambdaQueryWrapper<TimerRecordsDO> weekQuery = Wrappers.lambdaQuery(TimerRecordsDO.class)
                .eq(TimerRecordsDO::getUserId, userId)
                .eq(TimerRecordsDO::getIsActive, 1)
                .ge(TimerRecordsDO::getEndTime, startOfWeek)
                .le(TimerRecordsDO::getEndTime, endOfWeek);

        List<TimerRecordsDO> weekRecords = timerRecordsMapper.selectList(weekQuery);

        return weekRecords.stream()
                .mapToInt(TimerRecordsDO::getDuration)
                .sum();
    }

    /**
     * 开始计时
     */
    @Override
    public void startTiming(String userId) {
        // 设置用户在线状态（每个用户独立的 key，60秒过期）
        stringRedisTemplate.opsForValue().set(
                RedisCacheConstant.USER_ONLINE_KEY + userId,
                "1",
                RedisCacheConstant.ONLINE_EXPIRE_SECONDS,
                java.util.concurrent.TimeUnit.SECONDS
        );
        stringRedisTemplate.opsForValue().set(
                RedisCacheConstant.TIMER_STATUS_KEY + userId,
                "1",
                RedisCacheConstant.ONLINE_EXPIRE_SECONDS,
                java.util.concurrent.TimeUnit.SECONDS
        );
        log.info("用户开始计时: userId={}", userId);
    }

    /**
     * 停止计时
     */
    @Override
    public void stopTiming(String userId) {
        // 删除用户在线状态
        stringRedisTemplate.delete(RedisCacheConstant.USER_ONLINE_KEY + userId);
        stringRedisTemplate.delete(RedisCacheConstant.TIMER_STATUS_KEY + userId);

        // 标记进行中的记录为完成
        LambdaUpdateWrapper<TimerRecordsDO> updateWrapper = Wrappers.lambdaUpdate(TimerRecordsDO.class)
                .eq(TimerRecordsDO::getUserId, userId)
                .eq(TimerRecordsDO::getIsActive, 1)
                .isNull(TimerRecordsDO::getEndTime)
                .set(TimerRecordsDO::getIsActive, 0);
        timerRecordsMapper.update(null, updateWrapper);

        log.info("用户停止计时: userId={}", userId);
    }

    /**
     * 获取用户计时状态
     */
    @Override
    public TimerStatusVO getTimerStatus(String userId) {
        String statusKey = RedisCacheConstant.TIMER_STATUS_KEY + userId;
        Boolean isTiming = stringRedisTemplate.hasKey(statusKey);

        LambdaQueryWrapper<TimerSummaryDO> summaryQuery = Wrappers.lambdaQuery(TimerSummaryDO.class)
                .eq(TimerSummaryDO::getUserId, userId);
        TimerSummaryDO summary = timerSummaryMapper.selectOne(summaryQuery);

        String status = Boolean.TRUE.equals(isTiming) ? "RUNNING" : "STOPPED";

        return TimerStatusVO.builder()
                .isTiming(Boolean.TRUE.equals(isTiming))
                .status(status)
                .weekTotalSeconds(summary != null ? summary.getWeekSeconds() : 0)
                .totalSeconds(summary != null ? summary.getTotalSeconds() : 0)
                .remainingSeconds(summary != null && summary.getWeeklyTargetDuration() != null && summary.getWeekSeconds() != null && summary.getWeeklyTargetDuration() > 0
                        ? Math.max(0, summary.getWeeklyTargetDuration() - summary.getWeekSeconds()) : null)
                .build();
    }

    /**
     * 用户心跳检测
     */
    @Override
    public Boolean heartbeat(String userId) {
        // 设置用户在线状态（每个用户独立的 key，60秒过期）
        stringRedisTemplate.opsForValue().set(
                RedisCacheConstant.USER_ONLINE_KEY + userId,
                "1",
                RedisCacheConstant.ONLINE_EXPIRE_SECONDS,
                java.util.concurrent.TimeUnit.SECONDS
        );
        return true;
    }

    /**
     * 获取当前正在计时的用户人数
     */
    @Override
    public Integer getTimingUsersCount() {
        Set<String> keys = stringRedisTemplate.keys(RedisCacheConstant.USER_ONLINE_KEY + "*");
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        return keys.size();
    }

    /**
     * 获取排行榜
     *
     * @param weekOffset 周偏移量：0=本周, -1=上周, -2=上上周, -3=上上上周, -4=上上上上周
     */
    @Override
    public List<CheckInRankingVO> getLeaderboard(int weekOffset) {
        Date[] timeRange = getWeekTimeRange(weekOffset);
        return timerSummaryMapper.selectLeaderboardByWeek(timeRange[0], timeRange[1]);
    }

    /**
     * 获取排行榜的其他数据
     *
     * @param weekOffset 周偏移量：0=本周, -1=上周, -2=上上周, -3=上上上周, -4=上上上上周
     */
    @Override
    public CheckInRankingOtherVO getLeaderboardOther(int weekOffset) {
        Date[] timeRange = getWeekTimeRange(weekOffset);
        CheckInRankingOtherVO result = timerSummaryMapper.selectLeaderboardOtherByWeek(timeRange[0], timeRange[1]);
        if (result == null) {
            return CheckInRankingOtherVO.builder()
                    .avgOnlineDuration(0)
                    .weeklyGoalProgress(0.0)
                    .build();
        }
        return result;
    }

    /**
     * 获取处刑榜
     */
    @Override
    public List<PunishmentVO> getPunishment() {
        Date[] timeRange = getWeekTimeRange(-1);
        return timerSummaryMapper.selectPunishment(timeRange[0], timeRange[1]);
    }

    /**
     * 获取 Excel 数据
     */
    @Override
    public List<ExcelData> getExcelData(ExcelDataReq excelDataReq) {
        Date startTime = parseDate(excelDataReq.getStartTime());
        Date endTime = parseDate(excelDataReq.getEndTime());
        String grade = StringUtils.hasText(excelDataReq.getGrade()) ? excelDataReq.getGrade() : null;
        String direction = StringUtils.hasText(excelDataReq.getDirection()) ? excelDataReq.getDirection() : null;
        String position = StringUtils.hasText(excelDataReq.getPosition()) ? excelDataReq.getPosition() : null;

        return timerSummaryMapper.selectExcelData(startTime, endTime, grade, direction, position);
    }

    /**
     * 解析日期字符串为 Date 对象
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            // 尝试解析日期时间格式
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            // 如果失败，尝试解析日期格式
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }

    /**
     * 获取周目标时长
     */
    @Override
    public List<WeeklyTargetDurationVO> getWeeklyTargetDuration(SelectWeeklyTargetDurationReq selectWeeklyTargetDurationReq) {
        // 获取查询条件
        String name = StringUtils.hasText(selectWeeklyTargetDurationReq.getName())
                ? selectWeeklyTargetDurationReq.getName() : null;
        String grade = StringUtils.hasText(selectWeeklyTargetDurationReq.getGrade())
                ? selectWeeklyTargetDurationReq.getGrade() : null;
        String direction = StringUtils.hasText(selectWeeklyTargetDurationReq.getDirection())
                ? selectWeeklyTargetDurationReq.getDirection() : null;
        String position = StringUtils.hasText(selectWeeklyTargetDurationReq.getPosition())
                ? selectWeeklyTargetDurationReq.getPosition() : null;

        return timerSummaryMapper.selectWeeklyTargetDuration(name, grade, direction, position);
    }

    /**
     * 修改周目标时长
     */
    @Override
    public void editWeeklyTargetDuration(List<EditWeeklyTargetDurationReq> editWeeklyTargetDurationReqList) {
        for (EditWeeklyTargetDurationReq list : editWeeklyTargetDurationReqList) {
            LambdaUpdateWrapper<TimerSummaryDO> updateWrapper = Wrappers.lambdaUpdate(TimerSummaryDO.class)
                    .eq(TimerSummaryDO::getUserId, list.getUserId())
                    .set(TimerSummaryDO::getWeeklyTargetDuration, list.getNewWeeklyTargetDuration());
            timerSummaryMapper.update(updateWrapper);
        }
    }

    /**
     * 周偏移量转时间范围
     *
     * @param weekOffset 0=本周, -1=上周, -2=上上周...
     * @return [startTime, endTime]
     */
    private Date[] getWeekTimeRange(int weekOffset) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDate targetMonday = monday.plusWeeks(weekOffset);
        LocalDate targetSunday = targetMonday.plusDays(6);

        LocalDateTime startOfWeek = targetMonday.atStartOfDay();
        LocalDateTime endOfWeek = targetSunday.atTime(23, 59, 59);

        return new Date[]{
                Date.from(startOfWeek.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(endOfWeek.atZone(ZoneId.systemDefault()).toInstant())
        };
    }
}
