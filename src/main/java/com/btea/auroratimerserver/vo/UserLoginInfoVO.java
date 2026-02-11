package com.btea.auroratimerserver.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:14
 * @Description: 用户登录信息
 */
@Data
@Builder
public class UserLoginInfoVO {

    /**
     * 姓名
     */
    private String name;

    /**
     * 学号
     */
    private String userId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * token
     */
    private String token;
}
