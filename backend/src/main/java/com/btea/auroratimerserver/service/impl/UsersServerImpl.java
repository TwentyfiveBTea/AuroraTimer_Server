package com.btea.auroratimerserver.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.btea.auroratimerserver.common.config.AdminConfig;
import com.btea.auroratimerserver.common.constant.RedisCacheConstant;
import com.btea.auroratimerserver.common.context.UserContext;
import com.btea.auroratimerserver.common.convention.exception.ClientException;
import com.btea.auroratimerserver.common.util.AliyunOssUtil;
import com.btea.auroratimerserver.common.util.JwtUtil;
import com.btea.auroratimerserver.dao.entity.TimerSummaryDO;
import com.btea.auroratimerserver.dao.entity.UsersDO;
import com.btea.auroratimerserver.dao.mapper.TimerSummaryMapper;
import com.btea.auroratimerserver.dao.mapper.UsersMapper;
import com.btea.auroratimerserver.req.*;
import com.btea.auroratimerserver.service.UsersService;
import com.btea.auroratimerserver.vo.UserInfoVO;
import com.btea.auroratimerserver.vo.UserLoginInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.btea.auroratimerserver.common.constant.UserProfileConstant.*;
import static com.btea.auroratimerserver.common.convention.errorcode.BaseErrorCode.*;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 16:32
 * @Description: 用户接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsersServerImpl extends ServiceImpl<UsersMapper, UsersDO> implements UsersService {

    private final JwtUtil jwtUtil;
    private final AliyunOssUtil aliyunOssUtil;
    private final AdminConfig adminConfig;
    private final StringRedisTemplate stringRedisTemplate;
    private final UsersMapper usersMapper;
    private final TimerSummaryMapper timerSummaryMapper;

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

        // 根据学号判断专业
        String majorNumber = registerReq.getUserId().substring(5, 7);
        String major = switch (majorNumber) {
            case "01" -> "计算机科学与技术";
            case "02" -> "软件工程";
            case "03" -> "通信工程";
            case "04" -> "物联网工程";
            case "08" -> "数据科学与大数据技术";
            case "09" -> "网络空间安全";
            default -> throw new ClientException("专业编号不存在: " + majorNumber);
        };

        usersMapper.insert(UsersDO.builder()
                .id(UUID.randomUUID().toString())
                .userId(registerReq.getUserId())
                .name(registerReq.getName())
                .grade("20" + registerReq.getUserId().substring(0, 2) + "级")
                .email(registerReq.getEmail())
                .password(registerReq.getPassword())
                .avatar(DEFAULT_AVATAR)
                .major(major)
                .direction(registerReq.getDirection())
                .position(DEFAULT_POSITION)
                .status(1)
                .build());

        // 将用户初始化到计时器总表中notifications
        timerSummaryMapper.insert(TimerSummaryDO.builder()
                .id(UUID.randomUUID().toString())
                .userId(registerReq.getUserId())
                .weeklyTargetDuration(registerReq.getDirection().equals("设计") ? 43200 : 64800)
                .build()
        );
        log.info("用户注册成功");
    }

    /**
     * 用户登录
     *
     * @param loginReq 登录请求参数
     * @return token
     */
    @Override
    public UserLoginInfoVO login(LoginReq loginReq) {
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
            throw new ClientException(PASSWORD_ERROR);
        }
        log.info("用户登录成功，用户信息为: {}", user);
        String token = jwtUtil.generateUserToken(user.getUserId());

        // 登录时设置用户为在线（每个用户独立的 key，60秒过期）
        stringRedisTemplate.opsForValue().set(
                RedisCacheConstant.USER_ONLINE_KEY + user.getUserId(),
                "1",
                RedisCacheConstant.ONLINE_EXPIRE_SECONDS,
                TimeUnit.SECONDS
        );

        return UserLoginInfoVO.builder()
                .token(token)
                .userId(user.getUserId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .build();
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

    /**
     * 用户登出
     *
     * @param request HTTP 请求
     */
    @Override
    public void logout(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromHeader(request.getHeader("Authorization"));
        jwtUtil.addToBlacklist(token);

        // 从 Redis 中删除用户在线状态
        String userId = jwtUtil.parseUserId(token);
        stringRedisTemplate.delete(RedisCacheConstant.USER_ONLINE_KEY + userId);
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    @Override
    public UserInfoVO getUserInfo(String userId) {
        LambdaQueryWrapper<UsersDO> queryWrapper = Wrappers.lambdaQuery(UsersDO.class)
                .eq(UsersDO::getUserId, userId);
        UsersDO usersDO = usersMapper.selectOne(queryWrapper);
        return UserInfoVO.builder()
                .userId(usersDO.getUserId())
                .name(usersDO.getName())
                .direction(usersDO.getDirection())
                .position(usersDO.getPosition())
                .createTime(usersDO.getCreateTime())
                .avatar(usersDO.getAvatar())
                .build();
    }

    /**
     * 更新用户资料（支持资料+密码修改）
     *
     * @param requestParam 更新请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(UpdateProfileReq requestParam) {
        log.info("更新用户资料: userId={}", requestParam.getUserId());

        LambdaQueryWrapper<UsersDO> queryWrapper = Wrappers.lambdaQuery(UsersDO.class)
                .eq(UsersDO::getUserId, requestParam.getUserId());
        UsersDO user = usersMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new ClientException(USER_NOT_FOUND);
        }

        // 更新资料（direction、position、email）
        boolean isProfileUpdated = false;
        LambdaUpdateWrapper<UsersDO> updateWrapper = Wrappers.lambdaUpdate(UsersDO.class)
                .eq(UsersDO::getUserId, requestParam.getUserId());

        if (StrUtil.isNotBlank(requestParam.getDirection())) {
            updateWrapper.set(UsersDO::getDirection, requestParam.getDirection());
            isProfileUpdated = true;
        }
        if (StrUtil.isNotBlank(requestParam.getPosition())) {
            updateWrapper.set(UsersDO::getPosition, requestParam.getPosition());
            isProfileUpdated = true;
        }
        if (StrUtil.isNotBlank(requestParam.getEmail())) {
            // 检查邮箱是否已被其他用户使用
            LambdaQueryWrapper<UsersDO> emailWrapper = Wrappers.lambdaQuery(UsersDO.class)
                    .eq(UsersDO::getEmail, requestParam.getEmail())
                    .ne(UsersDO::getUserId, requestParam.getUserId());
            if (usersMapper.exists(emailWrapper)) {
                throw new ClientException(EMAIL_EXIST);
            }
            updateWrapper.set(UsersDO::getEmail, requestParam.getEmail());
            isProfileUpdated = true;
        }

        if (isProfileUpdated) {
            usersMapper.update(null, updateWrapper);
            log.info("用户 {} 资料更新成功", requestParam.getUserId());
        }

        // 更新密码
        if (StrUtil.isNotBlank(requestParam.getCurrentPassword())
                && StrUtil.isNotBlank(requestParam.getNewPassword())) {
            if (!requestParam.getCurrentPassword().equals(user.getPassword())) {
                throw new ClientException(PASSWORD_NOT_MATCH);
            }
            LambdaUpdateWrapper<UsersDO> passwordWrapper = Wrappers.lambdaUpdate(UsersDO.class)
                    .eq(UsersDO::getUserId, requestParam.getUserId())
                    .set(UsersDO::getPassword, requestParam.getNewPassword());
            usersMapper.update(null, passwordWrapper);
            log.info("用户 {} 密码修改成功", requestParam.getUserId());
        }
    }

    /**
     * 上传用户头像
     *
     * @param file 文件
     */
    @Override
    public void uploadAvatar(MultipartFile file) {
        String currentUserId = UserContext.getCurrentUserId();
        String avatarUrl = aliyunOssUtil.uploadAvatar(file, currentUserId);
        UsersDO userDO = UsersDO.builder()
                .avatar(avatarUrl)
                .build();
        LambdaUpdateWrapper<UsersDO> updateWrapper = Wrappers.lambdaUpdate(UsersDO.class)
                .eq(UsersDO::getUserId, currentUserId);
        if (baseMapper.update(userDO, updateWrapper) != 1) {
            throw new ClientException("上传头像失败");
        }
    }

    /**
     * 管理员登录
     *
     * @param adminLoginReq 管理员登录请求参数
     * @return token
     */
    @Override
    public String adminLogin(AdminLoginReq adminLoginReq) {
        log.info("管理员登录: {}", adminLoginReq);
        if (adminLoginReq.getUsername().equals(adminConfig.getUsername()) && adminLoginReq.getPassword().equals(adminConfig.getPassword())) {
            return jwtUtil.generateAdminToken(adminLoginReq.getUsername());
        } else {
            throw new ClientException(ADMIN_PASSWORD_ERROR);
        }
    }

    /**
     * 管理员登出
     *
     * @param request HTTP 请求
     */
    @Override
    public void adminLogout(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromHeader(request.getHeader("Authorization"));
        jwtUtil.addToBlacklist(token);
        log.info("管理员登出成功");
    }
}
