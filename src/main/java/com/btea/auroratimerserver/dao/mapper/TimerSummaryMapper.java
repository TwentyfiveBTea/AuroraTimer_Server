package com.btea.auroratimerserver.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.btea.auroratimerserver.dao.entity.TimerSummaryDO;
import com.btea.auroratimerserver.vo.CheckInRankingOtherVO;
import com.btea.auroratimerserver.vo.CheckInRankingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 03:04
 * @Description: 时间统计数据库访问层接口
 */
@Mapper
public interface TimerSummaryMapper extends BaseMapper<TimerSummaryDO> {

    /**
     * 获取排行榜（JOIN users 和 timer_summary 表）
     *
     * @return 排行榜列表
     */
    @Select("""
        SELECT
            u.name,
            u.grade,
            u.position,
            u.direction,
            u.avatar,
            ts.week_seconds AS weekTime,
            ts.total_seconds AS totalTime
        FROM users u
        INNER JOIN timer_summary ts ON u.user_id = ts.user_id
        WHERE u.status = 1
        ORDER BY ts.week_seconds DESC
        """)
    @Results({
        @Result(column = "name", property = "name"),
        @Result(column = "grade", property = "grade"),
        @Result(column = "position", property = "position"),
        @Result(column = "direction", property = "direction"),
        @Result(column = "avatar", property = "avatar"),
        @Result(column = "weekTime", property = "weekTime"),
        @Result(column = "totalTime", property = "totalTime")
    })
    List<CheckInRankingVO> selectLeaderboard();

    /**
     * 获取排行榜统计数据（平均周时长和达标率）
     *
     * @return 排行榜统计数据
     */
    @Select("""
            SELECT
                AVG(ts.week_seconds) AS avgOnlineDuration,
                COUNT(CASE WHEN ts.week_seconds > ts.weekly_target_duration THEN 1 END) * 1.0
                / NULLIF(COUNT(*), 0) AS weeklyGoalProgress
            FROM timer_summary ts
            INNER JOIN users u ON ts.user_id = u.user_id
            WHERE u.status = 1
            """)
    @Results({
        @Result(column = "avgOnlineDuration", property = "avgOnlineDuration"),
        @Result(column = "weeklyGoalProgress", property = "weeklyGoalProgress")
    })
    CheckInRankingOtherVO selectLeaderboardOther();

    /**
     * 获取排行榜（按时间范围查询）
     *
     * @param startTime 统计开始时间
     * @param endTime   统计结束时间
     * @return 排行榜列表
     */
    @Select("""
        SELECT
            u.name,
            u.grade,
            u.position,
            u.direction,
            u.avatar,
            COALESCE(SUM(tr.duration), 0) AS weekTime,
            ts.total_seconds AS totalTime
        FROM users u
        INNER JOIN timer_records tr ON u.user_id = tr.user_id
            AND tr.is_active = 1
            AND tr.end_time >= #{startTime}
            AND tr.end_time <= #{endTime}
        LEFT JOIN timer_summary ts ON u.user_id = ts.user_id
        WHERE u.status = 1
        GROUP BY u.user_id, u.name, u.grade, u.position, u.direction, u.avatar, ts.total_seconds
        ORDER BY weekTime DESC
        """)
    @Results({
        @Result(column = "name", property = "name"),
        @Result(column = "grade", property = "grade"),
        @Result(column = "position", property = "position"),
        @Result(column = "direction", property = "direction"),
        @Result(column = "avatar", property = "avatar"),
        @Result(column = "weekTime", property = "weekTime"),
        @Result(column = "totalTime", property = "totalTime")
    })
    List<CheckInRankingVO> selectLeaderboardByWeek(
        @Param("startTime") Date startTime,
        @Param("endTime") Date endTime
    );

    /**
     * 获取排行榜统计数据（按时间范围查询）
     *
     * @param startTime 统计开始时间
     * @param endTime   统计结束时间
     * @return 排行榜统计数据
     */
    @Select("""
        SELECT
            AVG(weekStats.weekSeconds) AS avgOnlineDuration,
            COUNT(CASE WHEN weekStats.weekSeconds > weekStats.targetDuration THEN 1 END) * 1.0
            / NULLIF(COUNT(*), 0) AS weeklyGoalProgress
        FROM (
            SELECT
                u.user_id,
                COALESCE(SUM(tr.duration), 0) AS weekSeconds,
                ts.weekly_target_duration AS targetDuration
            FROM users u
            LEFT JOIN timer_summary ts ON u.user_id = ts.user_id
            LEFT JOIN timer_records tr ON u.user_id = tr.user_id
                AND tr.is_active = 1
                AND tr.end_time >= #{startTime}
                AND tr.end_time <= #{endTime}
            WHERE u.status = 1
            GROUP BY u.user_id, ts.weekly_target_duration
        ) AS weekStats
        """)
    @Results({
        @Result(column = "avgOnlineDuration", property = "avgOnlineDuration"),
        @Result(column = "weeklyGoalProgress", property = "weeklyGoalProgress")
    })
    CheckInRankingOtherVO selectLeaderboardOtherByWeek(
        @Param("startTime") Date startTime,
        @Param("endTime") Date endTime
    );
}
