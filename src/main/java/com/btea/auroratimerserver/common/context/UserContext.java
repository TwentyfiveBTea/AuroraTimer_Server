package com.btea.auroratimerserver.common.context;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/02/08 03:09
 * @Description: 用户上下文
 */
public class UserContext {

    private static final ThreadLocal<String> USER_ID_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(String userId) {
        USER_ID_THREAD_LOCAL.set(userId);
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static String getUserId() {
        return USER_ID_THREAD_LOCAL.get();
    }

    /**
     * 清除当前用户ID
     */
    public static void clear() {
        USER_ID_THREAD_LOCAL.remove();
    }
}
