package com.btea.auroratimerserver.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 02:47
 * @Description: 用户实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("users")
public class UsersDO {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 学号
     */
    private String userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 方向
     */
    private String direction;

    /**
     * 职位
     */
    private String position;

    /**
     * 活跃度（活跃：1，退休：0，被淘汰：-1）
     */
    private Integer status;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
