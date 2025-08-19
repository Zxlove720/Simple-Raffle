package com.wzb.mapper;

import com.wzb.pojo.entity.Prize;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShopMapper {

    @Select("select id, name, total_stock, remaining_stock from prize order by total_stock")
    List<Prize> getAllPrize();

    @Insert("insert into prize(name, total_stock, remaining_stock) values (#{name}, #{totalStack}, #{remaingStock})")
    void addPrize(Prize prize);

    void updatePrize(Prize prize);
}
