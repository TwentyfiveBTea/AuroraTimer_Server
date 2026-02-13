package com.btea.auroratimerserver.controller;

import com.btea.auroratimerserver.common.convention.result.Result;
import com.btea.auroratimerserver.common.convention.result.Results;
import com.btea.auroratimerserver.req.EditWeeklyTargetDurationReq;
import com.btea.auroratimerserver.req.ExcelDataReq;
import com.btea.auroratimerserver.req.SelectWeeklyTargetDurationReq;
import com.btea.auroratimerserver.req.TimeAddReq;
import com.btea.auroratimerserver.service.TimerService;
import com.btea.auroratimerserver.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:18
 * @Description: 计时器控制器
 */
@RestController
@RequiredArgsConstructor
public class TimerController {

    private final TimerService timerService;

    /**
     * 获取计时器目标时长
     *
     * @param userId 用户 ID
     * @return 获取计时器目标时长
     */
    @PostMapping("/timer/target")
    public Result<Integer> getTimerTarget(@RequestParam("userId") String userId) {
        return Results.success(timerService.getTimerTarget(userId));
    }

    /**
     * 同步工时到服务器（每60秒调用一次）
     * <p>
     * 返回：
     * - addedSeconds: 本次实际增加的秒数
     * - serverWeekTime: 服务器计算的本周总时长（用于前端校准）
     */
    @PostMapping("/time/add")
    public Result<TimeAddVO> addTime(@RequestBody TimeAddReq requestParam) {
        return Results.success(timerService.addTime(requestParam));
    }

    /**
     * 开始计时
     *
     * @param userId 用户 ID
     */
    @PostMapping("/timer/start")
    public Result<Void> startTiming(@RequestParam String userId) {
        timerService.startTiming(userId);
        return Results.success();
    }

    /**
     * 停止计时
     *
     * @param userId 用户 ID
     */
    @PostMapping("/timer/stop")
    public Result<Void> stopTiming(@RequestParam String userId) {
        timerService.stopTiming(userId);
        return Results.success();
    }

    /**
     * 获取用户计时状态
     *
     * @param userId 用户 ID
     */
    @GetMapping("/timer/status")
    public Result<TimerStatusVO> getTimerStatus(@RequestParam String userId) {
        return Results.success(timerService.getTimerStatus(userId));
    }

    /**
     * 用户心跳检测
     *
     * @param userId 用户 ID
     */
    @PostMapping("/timer/heartbeat")
    public Result<Boolean> heartbeat(@RequestParam String userId) {
        return Results.success(timerService.heartbeat(userId));
    }

    /**
     * 获取当前正在计时的用户人数
     *
     * @return 在线人数
     */
    @GetMapping("/timer/timingUsers")
    public Result<Integer> getTimingUsersCount() {
        return Results.success(timerService.getTimingUsersCount());
    }

    /**
     * 获取打卡排行榜
     *
     * @param weekOffset 周偏移量：0=本周, -1=上周, -2=上上周, -3=上上上周, -4=上上上上周
     * @return 打卡排行榜
     */
    @GetMapping("/leaderboard")
    public Result<List<CheckInRankingVO>> getLeaderboard(
            @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset
    ) {
        return Results.success(timerService.getLeaderboard(weekOffset));
    }

    /**
     * 获取排行榜的其他数据
     *
     * @param weekOffset 周偏移量：0=本周, -1=上周, -2=上上周, -3=上上上周, -4=上上上上周
     * @return 打卡排行榜的其他数据
     */
    @GetMapping("/leaderboard/other")
    public Result<CheckInRankingOtherVO> getLeaderboardOther(
            @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset
    ) {
        return Results.success(timerService.getLeaderboardOther(weekOffset));
    }

    /**
     * 获取处刑榜
     *
     * @return 处刑榜
     */
    @GetMapping("/timer/punishment")
    public Result<List<PunishmentVO>> getPunishment() {
        return Results.success(timerService.getPunishment());
    }

    /**
     * 导出计时数据
     *
     * @param requestParam 导出参数
     * @return 导出的数据
     */
    @PostMapping("/admin/timer/exportTimerData")
    public Result<List<ExcelData>> exportTimerData(@RequestBody ExcelDataReq requestParam) {
        return Results.success(timerService.getExcelData(requestParam));
    }

    /**
     * 获取周目标时长
     *
     * @return 周目标时长
     */
    @PostMapping("/admin/timer/weeklyTargetDuration")
    public Result<List<WeeklyTargetDurationVO>> getWeeklyTargetDuration(@RequestBody SelectWeeklyTargetDurationReq requestParam) {
        return Results.success(timerService.getWeeklyTargetDuration(requestParam));
    }

    /**
     * 修改周目标时长
     *
     * @param requestParam 修改参数
     */
    @PostMapping("/admin/timer/editWeeklyTargetDuration")
    public Result<Void> editWeeklyTargetDuration(@RequestBody List<EditWeeklyTargetDurationReq> requestParam) {
        timerService.editWeeklyTargetDuration(requestParam);
        return Results.success();
    }
}
