package com.btea.auroratimerserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.btea.auroratimerserver.dao.entity.TimerRecordsDO;
import com.btea.auroratimerserver.req.EditWeeklyTargetDurationReq;
import com.btea.auroratimerserver.req.ExcelDataReq;
import com.btea.auroratimerserver.req.SelectWeeklyTargetDurationReq;
import com.btea.auroratimerserver.req.TimeAddReq;
import com.btea.auroratimerserver.vo.*;

import java.util.List;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:10
 * @Description: 计时器接口层
 */
public interface TimerService extends IService<TimerRecordsDO> {

    /**
     * 获取计时器目标时长
     *
     * @param userId 用户 ID
     * @return 目标时长
     */
    Integer getTimerTarget(String userId);

    /**
     * 同步工时到服务器
     * <p>
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

    /**
     * 获取排行榜
     *
     * @param weekOffset 周偏移量：0=本周, -1=上周, -2=上上周, -3=上上上周, -4=上上上上周
     * @return 排行榜
     */
    List<CheckInRankingVO> getLeaderboard(int weekOffset);

    /**
     * 获取排行榜的其他数据
     *
     * @param weekOffset 周偏移量：0=本周, -1=上周, -2=上上周, -3=上上上周, -4=上上上上周
     * @return 排行榜
     */
    CheckInRankingOtherVO getLeaderboardOther(int weekOffset);

    /**
     * 获取处刑榜
     *
     * @return 处刑榜
     */
    List<PunishmentVO> getPunishment();

    /**
     * 获取导出数据
     *
     * @param excelDataReq 导出数据请求
     * @return 导出数据
     */
    List<ExcelData> getExcelData(ExcelDataReq excelDataReq);

    /**
     * 获取周目标时长
     *
     * @param selectWeeklyTargetDurationReq 查询周目标时长请求
     * @return 周目标时长
     */
    List<WeeklyTargetDurationVO> getWeeklyTargetDuration(SelectWeeklyTargetDurationReq selectWeeklyTargetDurationReq);

    /**
     * 修改周目标时长
     *
     * @param editWeeklyTargetDurationReq 修改周目标时长请求
     */
    void editWeeklyTargetDuration(List<EditWeeklyTargetDurationReq> editWeeklyTargetDurationReq);
}
