package com.wzb.mapper;

import com.wzb.pojo.entity.User;
import com.wzb.pojo.vo.PrizeVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    @Select("select * from user where user_id = #{id}")
    User getByUserId(Integer id);

    @Insert("insert into user_prize(prize_id, user_id) values (#{prizeId}, #{userId})")
    void addPrize(Integer prizeId, Integer userId);

    List<PrizeVO> showPrize(Integer userId);
}
