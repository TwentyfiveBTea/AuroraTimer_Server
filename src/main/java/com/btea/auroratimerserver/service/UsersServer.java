package com.btea.auroratimerserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.btea.auroratimerserver.dao.entity.UsersDO;
import com.btea.auroratimerserver.req.AdminLoginReq;
import com.btea.auroratimerserver.req.LoginReq;
import com.btea.auroratimerserver.req.RegisterReq;
import com.btea.auroratimerserver.req.ResetPasswordReq;
import com.btea.auroratimerserver.req.UpdateProfileReq;
import com.btea.auroratimerserver.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 用户登出
     *
     * @param request HTTP 请求
     */
    void logout(HttpServletRequest request);

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    UserInfoVO getUserInfo(String userId);

    /**
     * 更新用户资料（支持资料+密码修改）
     *
     * @param requestParam 更新请求参数
     */
    void updateProfile(UpdateProfileReq requestParam);

    /**
     * 上传用户头像
     *
     * @param file 头像文件
     */
    void uploadAvatar(MultipartFile file);

    /**
     * 管理员登录
     *
     * @param adminLoginReq 登录请求参数
     * @return token
     */
    String adminLogin(AdminLoginReq adminLoginReq);

    /**
     * 管理员登出
     *
     * @param request HTTP 请求
     */
    void adminLogout(HttpServletRequest request);
}
