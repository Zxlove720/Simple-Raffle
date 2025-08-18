package com.wzb.mapper;

import com.wzb.pojo.entity.Prize;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RaffleMapper {

    @Select("select * from prize")
    List<Prize> getAllPrize();

    @Select("select * from prize where id = #{prizeId}")
    Prize getById(Integer prizeId);

    @Update("update prize set remaining_stock = (remaining_stock - 1) where id = #{prizeId}")
    void changeStock(Integer prizeId);

}
