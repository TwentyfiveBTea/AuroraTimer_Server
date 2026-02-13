package com.btea.auroratimerserver.req;

import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/13 19:10
 * @Description: 选择周目标时长请求
 */
@Data
public class SelectWeeklyTargetDurationReq {

    /**
     * 姓名
     */
    private String name;

    /**
     * 年级
     */
    private String grade;

    /**
     * 方向
     */
    private String direction;

    /**
     * 职位
     */
    private String position;
}
