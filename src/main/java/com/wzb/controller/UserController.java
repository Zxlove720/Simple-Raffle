package com.wzb.controller;

import com.wzb.pojo.dto.UserLoginDTO;
import com.wzb.pojo.entity.Result;
import com.wzb.pojo.entity.User;
import com.wzb.pojo.vo.PrizeVO;
import com.wzb.pojo.vo.UserLoginVO;
import com.wzb.server.UserService;
import com.wzb.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录注册
     * 用户登录，如果用户是第一次登录，那么就自动进行注册
     *
     * @param userLoginDTO 用户登录DTO
     * @return Result<UserLoginVO>
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.error("用户微信登录");
        User user = userService.login(userLoginDTO);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getUserId())
                .openid(user.getOpenid())
                .build();
        return Result.success(userLoginVO);
    }

    @PostMapping("/info")
    public Result<User> info() {
        return Result.success(ThreadUtil.getUser());
    }


    /**
     * 查看用户中奖情况
     *
     * @return List<PrizeVO> 奖品列表
     */
    @PostMapping("/show")
    public Result<List<PrizeVO>> showPrize() {
        log.info("用户查看中奖情况");
        List<PrizeVO> prizeVOList = userService.showPrize();
        return Result.success(prizeVOList);
    }
}
