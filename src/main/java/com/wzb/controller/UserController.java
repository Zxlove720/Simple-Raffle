package com.wzb.controller;

import com.wzb.pojo.dto.UserLoginDTO;
import com.wzb.pojo.entity.Result;
import com.wzb.pojo.entity.User;
import com.wzb.pojo.vo.UserLoginVO;
import com.wzb.server.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户微信登录");
        User user = userService.login(userLoginDTO);
        // 通过id、openid创建一个VO返回
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getUserId())
                .openid(user.getOpenid())
                .build();
        return Result.success(userLoginVO);
    }

}
