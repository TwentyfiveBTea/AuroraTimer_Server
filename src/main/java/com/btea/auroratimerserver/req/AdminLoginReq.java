package com.btea.auroratimerserver.req;

import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/10 21:36
 * @Description: 管理员登录请求参数
 */
@Data
public class AdminLoginReq {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
