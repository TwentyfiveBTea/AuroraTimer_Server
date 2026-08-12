package com.btea.auroratimerserver.req;

import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/12 19:12
 * @Description: 导出计时数据请求
 */
@Data
public class ExcelDataReq {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

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
