package com.wzb.mapper;

import com.wzb.pojo.entity.Prize;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RaffleMapper {

    @Select("select * from prize where remaining_stock > 0")
    List<Prize> getValidPrizes();

    @Select("select * from prize where id = #{prizeId}")
    Prize getById(Integer prizeId);

    @Update("update prize set remaining_stock = (remaining_stock - 1) where id = #{prizeId} and remaining_stock > 0")
    int changeStock(Integer prizeId);

}
