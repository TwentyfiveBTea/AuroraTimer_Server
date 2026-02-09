package com.btea.auroratimerserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.btea.auroratimerserver.dao.entity.UsersDO;
import com.btea.auroratimerserver.req.ResetPasswordReq;
import com.btea.auroratimerserver.req.LoginReq;
import com.btea.auroratimerserver.req.RegisterReq;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 16:32
 * @Description: 用户接口层
 */
public interface UsersServer extends IService<UsersDO> {

    /**
     * 用户注册
     *
     * @param registerReq 注册请求参数
     */
    void register(RegisterReq registerReq);

    /**
     * 用户登录
     *
     * @param loginReq 登录请求参数
     * @return token
     */
    String login(LoginReq loginReq);

    /**
     * 用户忘记密码
     *
     * @param resetPasswordReq 忘记密码请求参数
     */
    void resetPassword(ResetPasswordReq resetPasswordReq);
}
