package com.wzb.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "奖品VO")
public class PrizeVO {

    // 奖品id
    private Integer id;
    // 奖品名
    private String name;

}
