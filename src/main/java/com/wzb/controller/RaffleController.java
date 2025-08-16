package com.wzb.controller;

import com.wzb.server.RaffleServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/raffle")
public class RaffleController {

    private final RaffleServer raffleServer;

    @Autowired
    public RaffleController(RaffleServer raffleServer) {
        this.raffleServer = raffleServer;
    }

    /**
     * 用户抽奖
     */
    @PostMapping
    public void raffle() {
        raffleServer.raffle();
    }


}
