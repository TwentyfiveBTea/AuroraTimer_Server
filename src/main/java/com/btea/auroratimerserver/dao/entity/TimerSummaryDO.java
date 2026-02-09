package com.btea.auroratimerserver.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 02:57
 * @Description: 时间统计实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("times_summary")
public class TimerSummaryDO {

    /**
     * 主键 ID
     */
    private String id;

    /**
     * 学号
     */
    private String userId;

    /**
     * 每周目标时长（秒）
     */
    private Integer weeklyTargetDuration;

    /**
     * 当前周时长（秒）
     */
    private Integer weekSeconds;

    /**
     * 历史总时长（秒）
     */
    private Integer totalSeconds;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
