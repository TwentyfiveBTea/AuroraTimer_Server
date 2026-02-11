package com.btea.auroratimerserver.common.constant;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/10 21:13
 * @Description: Redis 缓存常量类
 */
public class RedisCacheConstant {

    /**
     * Token 黑名单缓存 key
     */
    public static final String TOKEN_BLACKLIST_KEY = "token:blacklist:";

    /**
     * 在线用户集合（Set结构）
     */
    public static final String ONLINE_USERS_KEY = "timer:online:users";

    /**
     * 用户在线状态 Key
     */
    public static final String TIMER_STATUS_KEY = "timer:status:";

    /**
     * 在线用户过期时间（秒）
     */
    public static final long ONLINE_EXPIRE_SECONDS = 65L;
}
