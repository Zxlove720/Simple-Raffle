package com.wzb.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录信息返回类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录信息返回VO")
public class UserLoginVO {

    @Schema(description = "用户id")
    private Integer id;

    @Schema(description = "微信登录标识符")
    private String openid;

}
