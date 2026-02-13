package com.btea.auroratimerserver.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/13 18:54
 * @Description: 周目标时长VO
 */
@Data
@Builder
public class WeeklyTargetDurationVO {

    /**
     * 姓名
     */
    private String name;

    /**
     * 学号
     */
    private String userId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 方向
     */
    private String direction;

    /**
     * 周目标时长
     */
    private Integer weeklyTargetDuration;
}
