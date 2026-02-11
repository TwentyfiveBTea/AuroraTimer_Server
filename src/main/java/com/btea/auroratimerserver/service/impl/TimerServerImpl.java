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
import com.btea.auroratimerserver.req.TimeAddReq;
import com.btea.auroratimerserver.service.TimerServer;
import com.btea.auroratimerserver.vo.TimeAddVO;
import com.btea.auroratimerserver.vo.TimerStatusVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;

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

            log.info("用户 {} 首次打卡: +{}秒", userId, addedSeconds);
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
        stringRedisTemplate.opsForSet().add(RedisCacheConstant.ONLINE_USERS_KEY, userId);
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
        stringRedisTemplate.opsForSet().remove(RedisCacheConstant.ONLINE_USERS_KEY, userId);
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
        stringRedisTemplate.opsForValue().set(
                RedisCacheConstant.TIMER_STATUS_KEY + userId,
                "1",
                RedisCacheConstant.ONLINE_EXPIRE_SECONDS,
                java.util.concurrent.TimeUnit.SECONDS
        );
        stringRedisTemplate.opsForSet().add(RedisCacheConstant.ONLINE_USERS_KEY, userId);
        return true;
    }

    /**
     * 获取当前正在计时的用户人数
     */
    @Override
    public Integer getTimingUsersCount() {
        Set<String> onlineUsers = stringRedisTemplate.opsForSet().members(RedisCacheConstant.ONLINE_USERS_KEY);
        return onlineUsers != null ? onlineUsers.size() : 0;
    }
}
