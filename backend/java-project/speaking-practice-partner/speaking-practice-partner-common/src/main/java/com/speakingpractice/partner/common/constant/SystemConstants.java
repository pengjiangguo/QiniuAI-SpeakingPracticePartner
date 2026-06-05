package com.speakingpractice.partner.common.constant;

/**
 * 系统常量
 */
public class SystemConstants {

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 默认页码
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页数量
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Token请求头
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * 用户ID请求头
     */
    public static final String USER_ID_HEADER = "X-User-Id";

    /**
     * Redis键前缀
     */
    public static final String REDIS_PREFIX = "speaking:practice:";

    /**
     * 用户Token缓存键
     */
    public static final String USER_TOKEN_KEY = REDIS_PREFIX + "user:token:";

    /**
     * 用户信息缓存键
     */
    public static final String USER_INFO_KEY = REDIS_PREFIX + "user:info:";

    /**
     * 默认Token过期时间（秒）- 7天
     */
    public static final Long DEFAULT_TOKEN_EXPIRE = 7 * 24 * 60 * 60L;

}
