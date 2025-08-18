package com.wzb.controller;

import com.wzb.pojo.entity.Result;
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
    public Result<Void> draw() {
        raffleServer.draw();
        return Result.success();
    }


}
