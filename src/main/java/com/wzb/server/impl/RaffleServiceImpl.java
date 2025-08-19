package com.wzb.server.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.wzb.constant.RedisConstant;
import com.wzb.mapper.RaffleMapper;
import com.wzb.mapper.UserMapper;
import com.wzb.pojo.entity.Prize;
import com.wzb.pojo.entity.User;
import com.wzb.server.RaffleService;
import com.wzb.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class RaffleServiceImpl implements RaffleService {

    private final UserMapper userMapper;

    private final RaffleMapper raffleMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RaffleServiceImpl(UserMapper userMapper, RaffleMapper raffleMapper, StringRedisTemplate stringRedisTemplate) {
        this.userMapper = userMapper;
        this.raffleMapper = raffleMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Prize draw() {
        User user = ThreadUtil.getUser();
        // 1.校验用户的状态，被封禁的用户不可抽奖
        if (user.getStatus() == 2) {
            throw new RuntimeException("用户被封禁，请联系管理员");
        }
        // 2.校验用户的剩余抽奖次数，抽奖次数不足不可抽奖
        if (user.getChance() <= 0) {
            throw new RuntimeException("抽奖次数不足");
        }
        return startDraw();
    }

    @Transactional
    public Prize startDraw() {
        // 最大重试次数
        int maxRetries = 3;
        // 重试间隔(毫秒)
        long retryInterval = 400;
        int retryCount = 0;
        Prize prize = null;
        // 1.开始抽奖，确保多线程安全
        while (retryCount < maxRetries) {
            if (tryLock()) {
                try {
                    // 1.1获取总权重
                    List<Prize> allPrize = raffleMapper.getAllPrize();
                    Map<Integer, Integer> prizeMap = new HashMap<>();
                    int totalWeight = 0;
                    for (Prize prizeTemp : allPrize) {
                        int remainingStock = prizeTemp.getRemainingStock();
                        if (remainingStock > 0) {
                            prizeMap.put(prizeTemp.getId(), remainingStock);
                            totalWeight += remainingStock;
                        }
                    }
                    // 1.2此时总权重不合法（所有奖品库存不足）
                    if (totalWeight <= 0) {
                        throw new RuntimeException("奖品库存不足");
                    }
                    // 1.2获取随机值
                    int randomNumber = ThreadLocalRandom.current().nextInt(totalWeight);
                    Integer sum = 0;
                    Integer prizeId = 0;
                    for (Map.Entry<Integer, Integer> entry : prizeMap.entrySet()) {
                        sum += entry.getValue();
                        Integer remainValue = entry.getValue();
                        if (randomNumber < sum && remainValue > 0) {
                            prizeId = entry.getKey();
                            break;
                        }
                    }
                    // 2.获取抽奖结果，减少库存，并将其加入用户奖品中
                    // 2.1获取抽奖结果
                    prize = raffleMapper.getById(prizeId);
                    // 2.2减少奖品库存并加入用户奖品
                    updateStock(prize);
                } catch (Exception e) {
                    throw new RuntimeException("抽奖出现问题");
                } finally {
                    unlock();
                }

            }
            retryCount++;
            try {
                // 休眠等待
                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
                // 恢复中断状态
                Thread.currentThread().interrupt();
                throw new RuntimeException("抽奖被中断");
            }
        }
        return prize;
    }

    @Transactional
    public void updateStock(Prize prize) {
        User user = ThreadUtil.getUser();
        raffleMapper.changeStock(prize.getId());
        // 2.3将其加入用户奖品中
        List<Integer> prizes = user.getPrizes();
        prizes.add(prize.getId());
        String prizesJson = JSONUtil.toJsonStr(prizes);
        userMapper.addPrize(prizesJson, user.getUserId());
    }

    private boolean tryLock() {
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(RedisConstant.DRAW_LOCK_KEY, "lock", RedisConstant.DRAW_LOCK_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(lock);
    }

    private void unlock() {
        stringRedisTemplate.delete(RedisConstant.DRAW_LOCK_KEY);
    }

}
