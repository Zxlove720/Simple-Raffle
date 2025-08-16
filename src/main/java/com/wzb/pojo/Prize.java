package com.wzb.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "奖品类")
public class Prize {

    // 奖品id
    private Integer id;
    // 奖品名
    private String name;
    // 总库存
    private Integer totalStock;
    // 实时库存
    private Integer remainingStock;
    // 中奖概率
    private Double probability;

}
