package com.wzb.constant;

import io.swagger.v3.oas.annotations.media.Schema;

public class WechatConstant {

    @Schema(description = "需要服务端请求的微信服务的接口地址")
    public static final String WECHAT_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

}
