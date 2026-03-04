package com.btea.auroratimerserver.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.btea.auroratimerserver.common.constant.RedisCacheConstant;
import com.btea.auroratimerserver.dao.entity.TimerSummaryDO;
import com.btea.auroratimerserver.dao.mapper.TimerSummaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:21
 * @Description: 每周定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyResetTask {

    private final TimerSummaryMapper timerSummaryMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 每周一凌晨 00:00 重置所有用户的本周计时
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 0 ? * MON")
    public void resetWeeklyTime() {
        log.info("========== 开始执行每周重置任务 ==========");

        // 1. 重置数据库中的 week_seconds
        LambdaUpdateWrapper<TimerSummaryDO> updateWrapper = Wrappers.lambdaUpdate(TimerSummaryDO.class)
                .set(TimerSummaryDO::getWeekSeconds, 0);
        int updatedCount = timerSummaryMapper.update(null, updateWrapper);

        // 2. 清理 Redis 中的周累计时间缓存
        String weekSecondsPattern = RedisCacheConstant.WEEK_SECONDS_KEY + "*";
        Set<String> keys = stringRedisTemplate.keys(weekSecondsPattern);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
            log.info("清理了 {} 个 Redis 周缓存 key", keys.size());
        }

        log.info("========== 每周重置任务完成，重置了 {} 位用户的本周计时 ==========", updatedCount);
    }
}
