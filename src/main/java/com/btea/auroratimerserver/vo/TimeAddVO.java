package com.btea.auroratimerserver.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:03
 * @Description: 同步工时响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeAddVO {

    /**
     * 本次实际增加的秒数
     */
    private Integer addedSeconds;

    /**
     * 服务器计算的本周总时长（用于前端校准）
     */
    private Integer serverWeekTime;
}
