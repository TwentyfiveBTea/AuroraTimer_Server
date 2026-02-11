package com.btea.auroratimerserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.btea.auroratimerserver.dao.entity.TimerRecordsDO;
import com.btea.auroratimerserver.dao.mapper.TimerRecordsMapper;
import com.btea.auroratimerserver.service.TimerServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:20
 * @Description: 计时器接口层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimerServerImpl extends ServiceImpl<TimerRecordsMapper, TimerRecordsDO> implements TimerServer {

    private final TimerRecordsMapper timerRecordsMapper;




}
