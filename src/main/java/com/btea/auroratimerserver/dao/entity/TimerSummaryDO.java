package com.btea.auroratimerserver.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("timer_summary")
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
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
