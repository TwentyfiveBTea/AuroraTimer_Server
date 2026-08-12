package com.btea.auroratimerserver.req;

import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/13 19:01
 * @Description: 修改周目标时长请求
 */
@Data
public class EditWeeklyTargetDurationReq {

    /**
     * 学号
     */
    private String userId;

    /**
     * 新周目标时长
     */
    private Integer newWeeklyTargetDuration;
}
