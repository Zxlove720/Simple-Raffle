package com.wzb.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "raffle.wechat")
public class WechatProperties {

    @Schema(description = "appid")
    private String appid;

    @Schema(description = "小程序密钥")
    private String secret;

    @Schema(description = "商户号")
    private String mchid;

    @Schema(description = "商户API证书的证书序列号")
    private String mchSerialNo;

    @Schema(description = "商户私钥文件路径")
    private String privateKeyFilePath;

    @Schema(description = "证书解密的密钥")
    private String apiV3Key;

    @Schema(description = "平台证书文件路径")
    private String weChatPayCertFilePath;

    @Schema(description = "支付成功的回调地址")
    private String notifyUrl;

    @Schema(description = "退款成功的回调地址")
    private String refundNotifyUrl;

}
