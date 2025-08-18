package com.wzb.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户微信登录DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户微信登录DTO")
public class UserLoginDTO {

    @Schema(description = "登录授权码")
    private String code;

}
