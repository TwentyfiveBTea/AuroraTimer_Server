package com.btea.auroratimerserver.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/12 18:17
 * @Description: 处刑榜视图
 */
@Data
@Builder
public class PunishmentVO {

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 方向
     */
    private String direction;

    /**
     * 上周打卡时间
     */
    private Integer lastWeekSignInTime;
}
