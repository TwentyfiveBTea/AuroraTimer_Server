package com.btea.auroratimerserver.controller;

import com.btea.auroratimerserver.service.TimerRecordServer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:18
 * @Description: 计时控制器
 */
@RestController
@RequiredArgsConstructor
public class TimerController {

    private final TimerRecordServer timerRecordServer;
}
