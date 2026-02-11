package com.btea.auroratimerserver.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 20:49
 * @Description: 排行榜视图
 */
@Data
@Builder
public class CheckInRankingVO {

    /**
     * 姓名
     */
    private String name;

    /**
     * 年级
     */
    private String grade;

    /**
     * 职位
     */
    private String position;

    /**
     * 方向
     */
    private String direction;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 本周时间
     */
    private Integer weekTime;

    /**
     * 总时间
     */
    private Integer totalTime;
}
