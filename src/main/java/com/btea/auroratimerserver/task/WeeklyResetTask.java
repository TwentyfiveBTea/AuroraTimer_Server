package com.btea.auroratimerserver.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.btea.auroratimerserver.dao.entity.TimerSummaryDO;
import com.btea.auroratimerserver.dao.mapper.TimerSummaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    /**
     * 每周一凌晨 00:00 重置所有用户的本周计时
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 0 ? * MON")
    public void resetWeeklyTime() {
        log.info("========== 开始执行每周重置任务 ==========");

        LambdaUpdateWrapper<TimerSummaryDO> updateWrapper = Wrappers.lambdaUpdate(TimerSummaryDO.class)
                .set(TimerSummaryDO::getWeekSeconds, 0);
        int updatedCount = timerSummaryMapper.update(null, updateWrapper);

        log.info("========== 每周重置任务完成，重置了 {} 位用户的本周计时 ==========", updatedCount);
    }
}
