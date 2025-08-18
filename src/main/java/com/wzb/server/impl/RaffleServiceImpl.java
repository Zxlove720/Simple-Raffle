package com.wzb.server.impl;

import com.wzb.mapper.RaffleMapper;
import com.wzb.mapper.UserMapper;
import com.wzb.pojo.entity.Prize;
import com.wzb.pojo.entity.User;
import com.wzb.server.RaffleService;
import com.wzb.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class RaffleServiceImpl implements RaffleService {

    private final UserMapper userMapper;

    private final RaffleMapper raffleMapper;

    @Autowired
    public RaffleServiceImpl(UserMapper userMapper, RaffleMapper raffleMapper) {
        this.userMapper = userMapper;
        this.raffleMapper = raffleMapper;
    }

    @Override
    public void draw() {
        Integer userId = ThreadUtil.getCurrentId();
        User user = userMapper.getByUserId(userId);
        // 1.校验用户的状态，被封禁的用户不可抽奖
        if (user.getStatus() == 2) {
            throw new RuntimeException("用户被封禁，请联系管理员");
        }
        // 2.校验用户的剩余抽奖次数，抽奖次数不足不可抽奖
        if (user.getChance() <= 0) {
            throw new RuntimeException("抽奖次数不足");
        }
        // 3.开始抽奖，确保多线程安全
        // 3.1获取总概率
        List<Prize> allPrize = raffleMapper.getAllPrize();
        Map<Integer, Integer> prizeMap = new HashMap<>();
        for (Prize prize : allPrize) {
            prizeMap.put(prize.getId(), prize.getRemainingStock());
        }
        int totalWeight = prizeMap.values().stream().mapToInt(i -> i).sum();
        // 3.2获取随机值
        int randomNumber = new Random().nextInt(totalWeight);
        Integer result = 0;
        for (Map.Entry<Integer, Integer> entry : prizeMap.entrySet()) {
            if (randomNumber < entry.getValue()) {
                result = entry.getKey();
                break;
            }
        }
        // 4.获取抽奖结果，减少库存，并将其加入用户奖品中

    }

}
