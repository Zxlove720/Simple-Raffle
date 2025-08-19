package com.wzb.constant;

import io.swagger.v3.oas.annotations.media.Schema;

public class RedisConstant {

    @Schema(description = "用户登录Key")
    public static final String USER_LOGIN_KEY = "raffle:user:login";

    @Schema(description = "用户登录过期时间")
    public static final Long USER_LOGIN_TTL = 60L;

}
