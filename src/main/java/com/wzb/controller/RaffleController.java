package com.wzb.controller;

import cn.hutool.core.bean.BeanUtil;
import com.wzb.pojo.entity.Prize;
import com.wzb.pojo.entity.Result;
import com.wzb.pojo.vo.PrizeVO;
import com.wzb.server.RaffleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/raffle")
public class RaffleController {

    private final RaffleService raffleServer;

    @Autowired
    public RaffleController(RaffleService raffleServer) {
        this.raffleServer = raffleServer;
    }

    /**
     * 用户抽奖
     */
    @PostMapping("/draw")
    public Result<PrizeVO> draw() throws InterruptedException {
        Prize prize = raffleServer.draw();
        PrizeVO prizeVO = BeanUtil.copyProperties(prize, PrizeVO.class);
        return Result.success(prizeVO);
    }

}
