package com.btea.auroratimerserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.btea.auroratimerserver.dao.entity.NotificationsDO;
import com.btea.auroratimerserver.dao.mapper.NotificationsMapper;
import com.btea.auroratimerserver.req.CreateNotificationsReq;
import com.btea.auroratimerserver.service.NotificationsService;
import com.btea.auroratimerserver.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/10 19:14
 * @Description: 通知接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationsServiceImpl extends ServiceImpl<NotificationsMapper, NotificationsDO> implements NotificationsService {

    private final NotificationsMapper notificationsMapper;

    /**
     * 创建通知
     *
     * @param createNotificationsReq 创建通知请求参数
     */
    @Override
    public void createNotifications(CreateNotificationsReq createNotificationsReq) {
        notificationsMapper.insert(NotificationsDO.builder()
                .id(UUID.randomUUID().toString())
                .type(createNotificationsReq.getType())
                .title(createNotificationsReq.getTitle())
                .content(createNotificationsReq.getContent())
                .meetingLocation(createNotificationsReq.getMeetingLocation())
                .meetingTime(createNotificationsReq.getMeetingTime())
                .build());
        log.info("创建通知成功");
    }

    /**
     * 获取所有通知
     *
     * @return 所有通知
     */
    @Override
    public List<NotificationVO> getAllNotifications() {
        List<NotificationsDO> notificationsDOList = notificationsMapper.selectList(null);
        return notificationsDOList.stream().map(notificationsDO -> NotificationVO.builder()
                .type(notificationsDO.getType())
                .title(notificationsDO.getTitle())
                .content(notificationsDO.getContent())
                .meetingLocation(notificationsDO.getMeetingLocation())
                .meetingTime(notificationsDO.getMeetingTime())
                .createTime(notificationsDO.getCreateTime())
                .build()).toList();
    }

    /**
     * 根据字段查询通知
     *
     * @param field 字段
     * @return 通知
     */
    @Override
    public List<NotificationVO> queryNotificationsByField(String field) {
        LambdaQueryWrapper<NotificationsDO> queryWrapper = Wrappers.lambdaQuery(NotificationsDO.class)
                .and(wrapper -> wrapper.like(NotificationsDO::getType, field)
                        .or()
                        .like(NotificationsDO::getTitle, field)
                        .or()
                        .like(NotificationsDO::getContent, field));
        List<NotificationsDO> notificationsDOList = notificationsMapper.selectList(queryWrapper);
        return notificationsDOList.stream().map(notificationsDO -> NotificationVO.builder()
                .type(notificationsDO.getType())
                .title(notificationsDO.getTitle())
                .content(notificationsDO.getContent())
                .meetingLocation(notificationsDO.getMeetingLocation())
                .meetingTime(notificationsDO.getMeetingTime())
                .createTime(notificationsDO.getCreateTime())
                .build()).toList();
    }
}
