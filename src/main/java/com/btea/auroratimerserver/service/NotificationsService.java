package com.btea.auroratimerserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.btea.auroratimerserver.dao.entity.NotificationsDO;
import com.btea.auroratimerserver.req.CreateNotificationsReq;
import com.btea.auroratimerserver.vo.NotificationVO;

import java.util.List;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/10 19:13
 * @Description: 通知接口层
 */
public interface NotificationsService extends IService<NotificationsDO> {

    /**
     * 创建通知
     *
     * @param createNotificationsReq 创建通知请求参数
     */
    void createNotifications(CreateNotificationsReq createNotificationsReq);

    /**
     * 获取所有通知
     *
     * @return 所有通知
     */
    List<NotificationVO> getAllNotifications();

    /**
     * 根据字段查询通知
     *
     * @param field 字段
     * @return 通知
     */
    List<NotificationVO> queryNotificationsByField(String field);
}
