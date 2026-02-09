package com.btea.auroratimerserver.controller;

import com.btea.auroratimerserver.common.convention.result.Result;
import com.btea.auroratimerserver.common.convention.result.Results;
import com.btea.auroratimerserver.req.ResetPasswordReq;
import com.btea.auroratimerserver.req.LoginReq;
import com.btea.auroratimerserver.req.RegisterReq;
import com.btea.auroratimerserver.service.UsersServer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 16:31
 * @Description: 用户控制器
 */
@RestController
@RequiredArgsConstructor
public class UsersController {

    private final UsersServer usersServer;

    /**
     * 用户注册
     *
     * @param requestParam 注册请求参数
     * @return token
     */
    @PostMapping("/auth/register")
    public Result<Void> register(@RequestBody @Valid RegisterReq requestParam) {
        usersServer.register(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     *
     * @param requestParam 登录请求参数
     * @return token
     */
    @PostMapping("/auth/login")
    public Result<String> login(@RequestBody @Valid LoginReq requestParam) {
        return Results.success(usersServer.login(requestParam));
    }

    /**
     * 忘记密码
     *
     * @param requestParam 忘记密码请求参数
     * @return token
     */
    @PostMapping("/auth/reset-password")
    public Result<Void> resetPassword(@RequestBody @Valid ResetPasswordReq requestParam) {
        usersServer.resetPassword(requestParam);
        return Results.success();
    }

}
