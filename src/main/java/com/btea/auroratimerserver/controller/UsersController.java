package com.btea.auroratimerserver.controller;

import com.btea.auroratimerserver.common.convention.result.Result;
import com.btea.auroratimerserver.common.convention.result.Results;
import com.btea.auroratimerserver.req.*;
import com.btea.auroratimerserver.service.UsersServer;
import com.btea.auroratimerserver.vo.UserInfoVO;
import com.btea.auroratimerserver.vo.UserLoginInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * @return 注册结果
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
    public Result<UserLoginInfoVO> login(@RequestBody @Valid LoginReq requestParam) {
        return Results.success(usersServer.login(requestParam));
    }

    /**
     * 忘记密码
     *
     * @param requestParam 忘记密码请求参数
     * @return 操作结果
     */
    @PostMapping("/auth/reset-password")
    public Result<Void> resetPassword(@RequestBody @Valid ResetPasswordReq requestParam) {
        usersServer.resetPassword(requestParam);
        return Results.success();
    }

    /**
     * 用户登出
     *
     * @param request HTTP 请求
     * @return 操作结果
     */
    @PostMapping("/auth/logout")
    public Result<Void> logout(HttpServletRequest request) {
        usersServer.logout(request);
        return Results.success();
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    @PostMapping("/users/{userId}")
    public Result<UserInfoVO> getUserInfo(@PathVariable("userId") String userId) {
        return Results.success(usersServer.getUserInfo(userId));
    }

    /**
     * 更新用户资料（支持资料+密码修改）
     *
     * @param requestParam 更新请求参数
     * @return 操作结果
     */
    @PutMapping("/users/profile")
    public Result<Void> updateProfile(@RequestBody UpdateProfileReq requestParam) {
        usersServer.updateProfile(requestParam);
        return Results.success();
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 操作结果
     */
    @PostMapping("/users/avatar")
    public Result<Void> uploadAvatar(@RequestParam("file") MultipartFile file) {
        usersServer.uploadAvatar(file);
        return Results.success();
    }

    /**
     * 管理员登录
     *
     * @param requestParam 管理员登录请求参数
     * @return token
     */
    @PostMapping("/admin/auth/login")
    public Result<String> adminLogin(@RequestBody @Valid AdminLoginReq requestParam) {
        return Results.success(usersServer.adminLogin(requestParam));
    }

    /**
     * 管理员登出
     *
     * @param request HTTP 请求
     * @return 操作结果
     */
    @PostMapping("/admin/auth/logout")
    public Result<Void> adminLogout(HttpServletRequest request) {
        usersServer.adminLogout(request);
        return Results.success();
    }
}
