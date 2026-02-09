package com.btea.auroratimerserver.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 13:59
 * @Description: JWT 角色枚举类
 */
@Getter
@AllArgsConstructor
public enum JwtRoleEnum {

    /**
     * 普通用户
     */
    USER("user"),

    /**
     * 管理员
     */
    ADMIN("admin");

    private final String role;

    /**
     * 判断是否为普通用户
     *
     * @param role 角色
     * @return 是否为普通用户
     */
    public static boolean isUser(String role) {
        return USER.getRole().equalsIgnoreCase(role);
    }

    /**
     * 判断是否为管理员
     *
     * @param role 角色
     * @return 是否为管理员
     */
    public static boolean isAdmin(String role) {
        return ADMIN.getRole().equalsIgnoreCase(role);
    }
}
