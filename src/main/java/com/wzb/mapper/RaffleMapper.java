package com.wzb.mapper;

import com.wzb.pojo.entity.Prize;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RaffleMapper {

    @Select("select * from prize")
    List<Prize> getAllPrize();
}
