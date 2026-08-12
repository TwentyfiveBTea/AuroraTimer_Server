package com.btea.auroratimerserver.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/10 21:53
 * @Description: 用户信息
 */
@Data
@Builder
public class UserInfoVO {

    /**
     * 学号
     */
    private String userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 方向
     */
    private String direction;

    /**
     * 职位
     */
    private String position;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 头像
     */
    private String avatar;
}
