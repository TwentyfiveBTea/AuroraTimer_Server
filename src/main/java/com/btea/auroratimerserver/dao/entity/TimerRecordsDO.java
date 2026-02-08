package com.btea.auroratimerserver.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 02:54
 * @Description: 时间记录实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("times_records")
public class TimerRecordsDO {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 学号
     */
    private Long userId;

    /**
     * 本次时长（秒）
     */
    private Integer duration;

    /**
     * 计时开始时间
     */
    private Date startTime;

    /**
     * 计时结束时间
     */
    private Date endTime;

    /**
     * 是否有效(1有效/0无效)
     */
    private Integer isActive;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
