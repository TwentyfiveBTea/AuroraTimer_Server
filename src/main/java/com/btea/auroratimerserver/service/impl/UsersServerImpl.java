package com.btea.auroratimerserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.btea.auroratimerserver.common.convention.exception.ClientException;
import com.btea.auroratimerserver.common.util.JwtUtil;
import com.btea.auroratimerserver.dao.entity.UsersDO;
import com.btea.auroratimerserver.dao.mapper.UsersMapper;
import com.btea.auroratimerserver.req.ResetPasswordReq;
import com.btea.auroratimerserver.req.LoginReq;
import com.btea.auroratimerserver.req.RegisterReq;
import com.btea.auroratimerserver.service.UsersServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

import static com.btea.auroratimerserver.common.constant.UserProfileConstant.DEFAULT_POSITION;
import static com.btea.auroratimerserver.common.constant.UserProfileConstant.RESET_PASSWORD;
import static com.btea.auroratimerserver.common.convention.errorcode.BaseErrorCode.*;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 16:32
 * @Description:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsersServerImpl extends ServiceImpl<UsersMapper, UsersDO> implements UsersServer {

    private final JwtUtil jwtUtil;
    private final UsersMapper usersMapper;

    /**
     * 用户注册
     *
     * @param registerReq 注册请求参数
     */
    @Override
    public void register(RegisterReq registerReq) {
        log.info("用户注册: {}", registerReq);
        LambdaQueryWrapper<UsersDO> queryWrapper = Wrappers.lambdaQuery(UsersDO.class)
                .eq(UsersDO::getUserId, registerReq.getUserId());
        if (usersMapper.selectOne(queryWrapper) != null) {
            throw new ClientException(USER_REGISTERED);
        }
        if (!Objects.equals(registerReq.getPassword(), registerReq.getConfirmPassword())) {
            throw new ClientException(PASSWORD_NOT_MATCH);
        }

        usersMapper.insert(UsersDO.builder()
                .id(UUID.randomUUID().toString())
                .userId(registerReq.getUserId())
                .name(registerReq.getName())
                .email(registerReq.getEmail())
                .password(registerReq.getPassword())
                .direction(registerReq.getDirection())
                .position(DEFAULT_POSITION)
                .status(1)
                .build());
        log.info("用户注册成功");
    }

    /**
     * 用户登录
     *
     * @param loginReq 登录请求参数
     * @return token
     */
    @Override
    public String login(LoginReq loginReq) {
        LambdaQueryWrapper<UsersDO> queryWrapper = Wrappers.lambdaQuery(UsersDO.class);
        UsersDO user;
        // 判断 account 中是学号还是邮箱
        if (loginReq.getAccount().contains("@")) {
            queryWrapper.eq(UsersDO::getEmail, loginReq.getAccount())
                    .eq(UsersDO::getPassword, loginReq.getPassword());
            user = usersMapper.selectOne(queryWrapper);
        } else if (loginReq.getAccount().matches("\\d{11}")) {
            queryWrapper.eq(UsersDO::getUserId, loginReq.getAccount())
                    .eq(UsersDO::getPassword, loginReq.getPassword());
            user = usersMapper.selectOne(queryWrapper);
        } else {
            throw new ClientException(USER_NOT_FOUND);
        }

        if (user == null) {
            throw new ClientException(USER_NOT_FOUND);
        }
        if (!Objects.equals(loginReq.getPassword(), user.getPassword())) {
            throw new ClientException(PASSWORD_NOT_MATCH);
        }
        log.info("用户登录成功，用户信息为: {}", user);
        return jwtUtil.generateUserToken(user.getUserId());
    }

    /**
     * 忘记密码
     *
     * @param resetPasswordReq 忘记密码请求参数
     */
    @Override
    public void resetPassword(ResetPasswordReq resetPasswordReq) {
        LambdaQueryWrapper<UsersDO> queryWrapper = Wrappers.lambdaQuery(UsersDO.class)
                .eq(UsersDO::getUserId, resetPasswordReq.getUserId());
        if (usersMapper.selectOne(queryWrapper) == null) {
            throw new ClientException(USER_NOT_FOUND);
        }
        LambdaUpdateWrapper<UsersDO> updateWrapper = Wrappers.lambdaUpdate(UsersDO.class)
                .eq(UsersDO::getUserId, resetPasswordReq.getUserId())
                .set(UsersDO::getPassword, RESET_PASSWORD);
        usersMapper.update(updateWrapper);
        log.info("用户 {} 忘记密码成功，新密码为: {}", resetPasswordReq.getUserId(), RESET_PASSWORD);
    }
}
