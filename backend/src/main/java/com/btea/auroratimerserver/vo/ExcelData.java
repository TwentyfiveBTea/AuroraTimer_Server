package com.btea.auroratimerserver.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/12 19:07
 * @Description: 导出计时数据
 */
@Data
@Builder
public class ExcelData {

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

    /**
     * 打卡时间
     */
    private Integer signInTime;

    /**
     * 达标状态
     */
    private String status;
}
