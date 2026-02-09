package com.btea.auroratimerserver.common.context;

import com.btea.auroratimerserver.common.enums.JwtRoleEnum;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/02/08 03:09
 * @Description: 用户上下文
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<JwtRoleEnum> USER_ROLE_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public static void setCurrentUserId(Long userId) {
        USER_ID_THREAD_LOCAL.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return USER_ID_THREAD_LOCAL.get();
    }

    /**
     * 设置当前用户角色
     */
    public static void setCurrentUserRole(JwtRoleEnum role) {
        USER_ROLE_THREAD_LOCAL.set(role);
    }

    /**
     * 获取当前用户角色
     */
    public static JwtRoleEnum getCurrentUserRole() {
        return USER_ROLE_THREAD_LOCAL.get();
    }

    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin() {
        JwtRoleEnum role = getCurrentUserRole();
        return role == JwtRoleEnum.ADMIN;
    }

    /**
     * 判断是否为普通用户
     */
    public static boolean isUser() {
        JwtRoleEnum role = getCurrentUserRole();
        return role == JwtRoleEnum.USER;
    }

    /**
     * 清理ThreadLocal
     */
    public static void clear() {
        USER_ID_THREAD_LOCAL.remove();
        USER_ROLE_THREAD_LOCAL.remove();
    }
}
