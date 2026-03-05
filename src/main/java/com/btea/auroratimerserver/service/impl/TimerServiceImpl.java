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
import com.btea.auroratimerserver.service.TimerService;
import com.btea.auroratimerserver.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:10
 * @Description: 计时器接口层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimerServiceImpl extends ServiceImpl<TimerRecordsMapper, TimerRecordsDO> implements TimerService {

    private final TimerRecordsMapper timerRecordsMapper;
    private final TimerSummaryMapper timerSummaryMapper;
    private final StringRedisTemplate stringRedisTemplate;

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
     * <p>
     * 优化：使用 Redis 缓存进行中的记录信息（endTime|duration）和周累计时间，
     * 减少数据库查询次数，从 3-4 次降低到最多 2 次
     */
    @Override
    public TimeAddVO addTime(TimeAddReq requestParam) {
        String userId = requestParam.getUserId();
        Integer seconds = requestParam.getSeconds();
        long now = System.currentTimeMillis();

        log.debug("同步工时: userId={}, seconds={}", userId, seconds);

        // 1. 刷新用户在线状态
        stringRedisTemplate.opsForValue().set(
                RedisCacheConstant.USER_ONLINE_KEY + userId,
                "1",
                RedisCacheConstant.ONLINE_EXPIRE_SECONDS,
                TimeUnit.SECONDS
        );

        // 2. 从 Redis 获取进行中的记录信息
        String ongoingKey = RedisCacheConstant.ONGOING_RECORD_KEY + userId;
        String cachedRecord = stringRedisTemplate.opsForValue().get(ongoingKey);

        Long cachedEndTime = null;
        Integer cachedDuration = null;
        String recordId = null;

        if (cachedRecord != null && cachedRecord.contains("|")) {
            // Redis 有缓存，解析缓存数据 (格式: recordId|endTime|duration)
            String[] parts = cachedRecord.split("\\|");
            if (parts.length == 3) {
                recordId = parts[0];
                cachedEndTime = Long.parseLong(parts[1]);
                cachedDuration = Integer.parseInt(parts[2]);
            }
        }

        int addedSeconds;

        if (cachedEndTime == null) {
            // Redis 没有缓存，查询数据库
            LambdaQueryWrapper<TimerRecordsDO> queryWrapper = Wrappers.lambdaQuery(TimerRecordsDO.class)
                    .eq(TimerRecordsDO::getUserId, userId)
                    .eq(TimerRecordsDO::getIsActive, 1)
                    .isNull(TimerRecordsDO::getEndTime)
                    .orderByDesc(TimerRecordsDO::getStartTime)
                    .last("LIMIT 1");
            TimerRecordsDO record = timerRecordsMapper.selectOne(queryWrapper);

            if (record != null) {
                recordId = record.getId();
                cachedEndTime = record.getEndTime().getTime();
                cachedDuration = record.getDuration();
                // 写入 Redis 缓存，1小时过期
                stringRedisTemplate.opsForValue().set(ongoingKey,
                        recordId + "|" + cachedEndTime + "|" + cachedDuration,
                        RedisCacheConstant.CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
            }
        }

        if (cachedEndTime == null) {
            // 首次打卡
            recordId = UUID.randomUUID().toString();
            TimerRecordsDO record = TimerRecordsDO.builder()
                    .id(recordId)
                    .userId(userId)
                    .startTime(new Date(now - seconds * 1000L))
                    .endTime(new Date(now))
                    .duration(seconds)
                    .isActive(1)
                    .build();
            timerRecordsMapper.insert(record);

            // 更新 Redis 缓存
            stringRedisTemplate.opsForValue().set(ongoingKey,
                    recordId + "|" + now + "|" + seconds,
                    RedisCacheConstant.CACHE_EXPIRE_HOURS, TimeUnit.HOURS);

            addedSeconds = seconds;
        } else {
            // 已有记录，直接累加前端传来的秒数
            addedSeconds = seconds;

            // 更新数据库记录
            TimerRecordsDO record = new TimerRecordsDO();
            record.setId(recordId);
            record.setEndTime(new Date(now));
            record.setDuration(cachedDuration + addedSeconds);
            timerRecordsMapper.updateById(record);

            // 更新 Redis 缓存
            stringRedisTemplate.opsForValue().set(ongoingKey,
                    recordId + "|" + now + "|" + (cachedDuration + addedSeconds),
                    RedisCacheConstant.CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }

        // 3. 更新汇总表
        LambdaUpdateWrapper<TimerSummaryDO> updateWrapper = Wrappers.lambdaUpdate(TimerSummaryDO.class)
                .eq(TimerSummaryDO::getUserId, userId)
                .setSql("total_seconds = total_seconds + " + addedSeconds)
                .setSql("week_seconds = week_seconds + " + addedSeconds);
        timerSummaryMapper.update(null, updateWrapper);

        // 4. 从 Redis 缓存获取本周总时长（避免再查数据库）
        String weekSecondsKey = RedisCacheConstant.WEEK_SECONDS_KEY + userId;
        String cachedWeekSeconds = stringRedisTemplate.opsForValue().get(weekSecondsKey);

        int serverWeekTime;
        if (cachedWeekSeconds != null) {
            // 缓存存在，直接累加
            serverWeekTime = Integer.parseInt(cachedWeekSeconds) + addedSeconds;
        } else {
            // 缓存不存在，查询数据库一次（数据库已经是更新后的值，不需要再加 addedSeconds）
            TimerSummaryDO summary = timerSummaryMapper.selectOne(
                    Wrappers.lambdaQuery(TimerSummaryDO.class)
                            .eq(TimerSummaryDO::getUserId, userId)
            );
            serverWeekTime = summary != null ? summary.getWeekSeconds() : 0;
        }

        // 更新 Redis 缓存
        stringRedisTemplate.opsForValue().set(weekSecondsKey, String.valueOf(serverWeekTime),
                RedisCacheConstant.WEEK_CACHE_EXPIRE_DAYS, TimeUnit.DAYS);

        return TimeAddVO.builder()
                .addedSeconds(addedSeconds)
                .serverWeekTime(serverWeekTime)
                .build();
    }

    /**
     * 计算本周总时长（从 records 表查询，用于排行榜等场景）
     * 注意：addTime 已改用 summary 表直接读取，如无特殊需求不建议调用此方法
     */
    @SuppressWarnings("unused")
    private Integer getWeekTime(String userId) {
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDateTime startOfWeek = LocalDateTime.of(weekStart, LocalTime.MIN);
        LocalDateTime endOfWeek = LocalDateTime.of(now.plusDays(6), LocalTime.MAX);

        LambdaQueryWrapper<TimerRecordsDO> weekQuery = Wrappers.lambdaQuery(TimerRecordsDO.class)
                .eq(TimerRecordsDO::getUserId, userId)
                .eq(TimerRecordsDO::getIsActive, 1)
                .isNotNull(TimerRecordsDO::getEndTime)
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
        // 删除进行中的记录缓存
        stringRedisTemplate.delete(RedisCacheConstant.ONGOING_RECORD_KEY + userId);
        // 删除周累计时间缓存
        stringRedisTemplate.delete(RedisCacheConstant.WEEK_SECONDS_KEY + userId);

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
        // weekOffset = 0 表示本周，从 timer_summary 表查询
        if (weekOffset == 0) {
            return timerSummaryMapper.selectLeaderboard();
        }
        // 其他周（-1、-2、-3、-4）从 timer_records 表实时 SUM
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
        // weekOffset = 0 表示本周，从 timer_summary 表查询
        if (weekOffset == 0) {
            return timerSummaryMapper.selectLeaderboardOther();
        }
        // 其他周（-1、-2、-3、-4）从 timer_records 表实时 SUM
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
