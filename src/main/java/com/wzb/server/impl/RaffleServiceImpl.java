package com.wzb.server.impl;

import cn.hutool.json.JSONUtil;
import com.wzb.mapper.RaffleMapper;
import com.wzb.mapper.UserMapper;
import com.wzb.pojo.entity.Prize;
import com.wzb.pojo.entity.User;
import com.wzb.server.RaffleService;
import com.wzb.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Prize draw() {
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
        return startDraw(user);
    }

    @Transactional
    public Prize startDraw(User user) {
        // 1.开始抽奖，确保多线程安全
        // 1.1获取总概率
        List<Prize> allPrize = raffleMapper.getAllPrize();
        Map<Integer, Integer> prizeMap = new HashMap<>();
        for (Prize prize : allPrize) {
            prizeMap.put(prize.getId(), prize.getRemainingStock());
        }
        int totalWeight = prizeMap.values().stream().mapToInt(i -> i).sum();
        // 1.2获取随机值
        int randomNumber = new Random().nextInt(totalWeight);
        Integer prizeId = 0;
        for (Map.Entry<Integer, Integer> entry : prizeMap.entrySet()) {
            if (randomNumber < entry.getValue()) {
                prizeId = entry.getKey();
                break;
            }
        }
        // 2.获取抽奖结果，减少库存，并将其加入用户奖品中
        // 2.1获取抽奖结果
        Prize prize = raffleMapper.getById(prizeId);
        // 2.2减少奖品库存
        raffleMapper.changeStock(prizeId);
        // 2.3将其加入用户奖品中
        List<Integer> prizes = user.getPrizes();
        prizes.add(prizeId);
        String prizesJson = JSONUtil.toJsonStr(prizes);
        userMapper.addPrize(prizesJson, user.getUserId());
        // 3.返回抽奖结果
        return prize;
    }

}
