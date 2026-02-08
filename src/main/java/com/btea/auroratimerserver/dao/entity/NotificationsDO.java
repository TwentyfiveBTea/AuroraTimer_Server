package com.btea.auroratimerserver.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 02:50
 * @Description: 通知实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("notifications")
public class NotificationsDO {

    /**
     * 主键 ID
     */
    private Long id;

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
     * 会议地点
     */
    private String meetingLocation;

    /**
     * 会议时间
     */
    private Date meetingTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
