package com.btea.auroratimerserver.controller;

import com.btea.auroratimerserver.common.convention.result.Result;
import com.btea.auroratimerserver.common.convention.result.Results;
import com.btea.auroratimerserver.req.CreateNotificationsReq;
import com.btea.auroratimerserver.service.NotificationsService;
import com.btea.auroratimerserver.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/10 19:12
 * @Description: 通知控制器
 */
@RestController
@RequiredArgsConstructor
public class NotificationsController {

    private final NotificationsService notificationsService;

    /**
     * 创建通知
     *
     * @param requestParam 创建通知请求参数
     * @return 创建结果
     */
    @PostMapping("/admin/notifications")
    public Result<Void> createNotifications(@RequestBody CreateNotificationsReq requestParam) {
        notificationsService.createNotifications(requestParam);
        return Results.success();
    }

    /**
     * 获取所有通知
     *
     * @return 所有通知
     */
    @GetMapping("/notifications")
    public Result<List<NotificationVO>> getAllNotifications() {
        return Results.success(notificationsService.getAllNotifications());
    }

    /**
     * 根据字段查询通知
     *
     * @param field 字段
     * @return 通知
     */
    @GetMapping("/notifications/{field}")
    public Result<List<NotificationVO>> queryNotificationByField(@PathVariable("field") String field) {
        return Results.success(notificationsService.queryNotificationsByField(field));
    }
}
