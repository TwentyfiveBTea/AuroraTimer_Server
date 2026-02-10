package com.btea.auroratimerserver.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/10 19:33
 * @Description: 全部通知视图
 */
@Data
public class NotificationVO {

    /**
     * 通知类型
     */
    private String type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 会议地点（可选）
     */
    private String meetingLocation;

    /**
     * 会议时间（可选）
     */
    private String meetingTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
