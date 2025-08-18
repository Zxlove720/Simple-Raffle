package com.wzb.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户实体类")
public class User {
    @Schema(description = "用户id")
    private Integer userId;

    @Schema(description = "微信登录标识符")
    private String openid;

    @Schema(description = "用户状态 0普通用户|1管理员|2封禁用户")
    private Integer status;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "剩余抽奖次数")
    private String chance;

    @Schema(description = "获得奖品")
    private List<Integer> prizes;

    @Schema(description = "注册时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
