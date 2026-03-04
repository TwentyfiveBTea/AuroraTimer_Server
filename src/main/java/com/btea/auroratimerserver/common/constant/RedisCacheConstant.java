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
     * 用户在线状态 Key（单个用户，用于独立过期）
     */
    public static final String USER_ONLINE_KEY = "timer:online:user:";

    /**
     * 在线用户集合（Set结构）- 已弃用，使用 USER_ONLINE_KEY 代替
     */
    @Deprecated
    public static final String ONLINE_USERS_KEY = "timer:online:users";

    /**
     * 用户在线状态 Key
     */
    public static final String TIMER_STATUS_KEY = "timer:status:";

    /**
     * 用户进行中的计时记录 Key（用于缓存当前记录ID，减少数据库查询）
     * 缓存格式: endTime|duration
     */
    public static final String ONGOING_RECORD_KEY = "timer:ongoing:";

    /**
     * 用户本周累计工时缓存 Key（用于减少数据库查询）
     */
    public static final String WEEK_SECONDS_KEY = "timer:week_seconds:";

    /**
     * 用户累计总工时缓存 Key（用于减少数据库查询）
     */
    public static final String TOTAL_SECONDS_KEY = "timer:total_seconds:";

    /**
     * 缓存过期时间（小时）
     */
    public static final long CACHE_EXPIRE_HOURS = 1L;

    /**
     * 周数据缓存过期时间（天）
     */
    public static final long WEEK_CACHE_EXPIRE_DAYS = 7L;

    /**
     * 在线用户过期时间（秒）
     */
    public static final long ONLINE_EXPIRE_SECONDS = 60L;
}
