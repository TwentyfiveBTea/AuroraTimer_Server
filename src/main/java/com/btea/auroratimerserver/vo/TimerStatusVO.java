package com.btea.auroratimerserver.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:04
 * @Description: 计时状态响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimerStatusVO {

    /**
     * 是否正在计时
     */
    private Boolean isTiming;

    /**
     * 当前状态: RUNNING, STOPPED
     */
    private String status;

    /**
     * 本周总时长（秒）
     */
    private Integer weekTotalSeconds;

    /**
     * 历史总时长（秒）
     */
    private Integer totalSeconds;

    /**
     * 剩余时间（秒）
     */
    private Integer remainingSeconds;
}
