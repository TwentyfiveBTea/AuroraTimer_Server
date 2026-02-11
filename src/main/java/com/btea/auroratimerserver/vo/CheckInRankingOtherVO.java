package com.btea.auroratimerserver.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 21:21
 * @Description: 打卡排行其他数据视图
 */
@Data
@Builder
public class CheckInRankingOtherVO {

    /**
     * 平均周时长（秒）
     */
    private Integer avgOnlineDuration;

    /**
     * 平均达标率
     */
    private Double weeklyGoalProgress;
}
