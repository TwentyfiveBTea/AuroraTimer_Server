package com.btea.auroratimerserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.btea.auroratimerserver.dao.entity.TimerRecordsDO;
import com.btea.auroratimerserver.req.TimeAddReq;
import com.btea.auroratimerserver.vo.TimeAddVO;
import com.btea.auroratimerserver.vo.TimerStatusVO;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:10
 * @Description: 计时器接口层
 */
public interface TimerServer extends IService<TimerRecordsDO> {

    /**
     * 获取计时器目标时长
     *
     * @param userId 用户 ID
     * @return 目标时长
     */
    Integer getTimerTarget(String userId);

    /**
     * 同步工时到服务器
     *
     * 核心逻辑：
     * 1. 首次打卡：创建新记录，返回本次秒数
     * 2. 正常补时（间隔 < 900秒）：补时 = 实际经过的秒数
     * 3. 重新上线（间隔 >= 900秒）：只加固定的 60 秒
     *
     * @param requestParam 同步请求
     * @return 同步结果
     */
    TimeAddVO addTime(TimeAddReq requestParam);

    /**
     * 开始计时
     *
     * @param userId 用户 ID
     */
    void startTiming(String userId);

    /**
     * 停止计时
     *
     * @param userId 用户 ID
     */
    void stopTiming(String userId);

    /**
     * 获取用户计时状态
     *
     * @param userId 用户 ID
     * @return 计时状态
     */
    TimerStatusVO getTimerStatus(String userId);

    /**
     * 用户心跳检测
     *
     * @param userId 用户 ID
     * @return 是否成功
     */
    Boolean heartbeat(String userId);

    /**
     * 获取当前正在计时的用户人数
     *
     * @return 在线人数
     */
    Integer getTimingUsersCount();
}
