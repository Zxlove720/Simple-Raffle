package com.wzb.mapper;

import com.wzb.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    @Select("select * from user where user_id = #{id}")
    User getByUserId(Integer id);

    @Update("update user set prizes = #{prizeJson} where user_id = #{userId}")
    void addPrize(String prizesJson, Integer userId);
}
