package com.btea.auroratimerserver.req;

import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/10 19:19
 * @Description: 发送通知请求参数
 */
@Data
public class CreateNotificationsReq {

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

}
