package com.btea.auroratimerserver.req;

import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 17:38
 * @Description: 登录请求参数
 */
@Data
public class LoginReq {

    /**
     * 账号（学号和邮箱二选一）
     */
    private String account;

    /**
     * 密码
     */
    private String password;
}
