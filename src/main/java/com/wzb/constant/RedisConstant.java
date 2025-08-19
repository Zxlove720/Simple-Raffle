package com.wzb.constant;

import io.swagger.v3.oas.annotations.media.Schema;

public class RedisConstant {

    @Schema(description = "用户登录Key")
    public static final String USER_LOGIN_KEY = "raffle:user:login:";

    @Schema(description = "用户登录过期时间")
    public static final Long USER_LOGIN_TTL = 60L;

    @Schema(description = "抽奖锁")
    public static final String DRAW_LOCK_KEY = "raffle:draw:lock:";

    @Schema(description = "抽奖锁过期时间")
    public static final Long DRAW_LOCK_TTL = 10L;

}
