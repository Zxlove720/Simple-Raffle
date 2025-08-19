package com.wzb.server.impl;

import cn.hutool.core.util.BooleanUtil;
import com.wzb.constant.RedisConstant;
import com.wzb.mapper.RaffleMapper;
import com.wzb.mapper.UserMapper;
import com.wzb.pojo.entity.Prize;
import com.wzb.pojo.entity.User;
import com.wzb.server.RaffleService;
import com.wzb.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class RaffleServiceImpl implements RaffleService {

    private final UserMapper userMapper;
    private final RaffleMapper raffleMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RaffleService selfProxy; // 注入自身代理对象

    @Autowired
    public RaffleServiceImpl(
            UserMapper userMapper,
            RaffleMapper raffleMapper,
            StringRedisTemplate stringRedisTemplate,
            @Lazy RaffleService selfProxy // 延迟注入代理对象
    ) {
        this.userMapper = userMapper;
        this.raffleMapper = raffleMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.selfProxy = selfProxy; // 解决自调用事务失效
    }

    /**
     * 入口方法：处理用户校验和重试机制（无事务）
     *
     * @return Prize实体
     */
    @Override
    public Prize draw() throws InterruptedException {
        User user = ThreadUtil.getUser();
        if (user.getStatus() == 2) {
            throw new RuntimeException("用户被封禁");
        }
        if (user.getChance() <= 0) {
            throw new RuntimeException("抽奖次数不足");
        }
        // 重试逻辑（非事务操作）
        int maxRetries = 3;
        long retryInterval = 400;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            if (tryLock()) {
                try {
                    // 通过代理对象调用事务方法
                    return selfProxy.executeDraw();
                } finally {
                    unlock();
                }
            }
            retryCount++;
            Thread.sleep(retryInterval);
        }
        throw new RuntimeException("系统繁忙");
    }


    // 核心事务方法（解决自调用问题）
    @Transactional(rollbackFor = Exception.class) // 明确指定所有异常回滚
    public Prize executeDraw() {
        try {
            // 1. 计算权重
            List<Prize> prizes = raffleMapper.getValidPrizes();
            int totalWeight = prizes.stream().mapToInt(Prize::getRemainingStock).sum();
            if (totalWeight <= 0) {
                throw new RuntimeException("奖品库存不足");
            }
            // 2. 随机选择奖品
            int randomNum = ThreadLocalRandom.current().nextInt(totalWeight);
            int sum = 0;
            Integer prizeId = null;
            for (Prize p : prizes) {
                sum += p.getRemainingStock();
                if (randomNum < sum) {
                    prizeId = p.getId();
                    break;
                }
            }
            if (prizeId == null) {
                throw new RuntimeException("抽奖失败");
            }
            // 3. 扣减库存（原子操作）
            int rows = raffleMapper.changeStock(prizeId);
            if (rows == 0) {
                // 手动标记回滚（避免抛出受检异常）
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new RuntimeException("库存不足");
            }
            // 4. 更新用户奖品（原子操作）
            User user = ThreadUtil.getUser();
            userMapper.addPrizeAtomically(user.getUserId(), prizeId);
            return raffleMapper.getById(prizeId);
        } catch (Exception e) {
            // 已配置rollbackFor=Exception.class，无需手动处理
            throw new RuntimeException("抽奖失败", e);
        }
    }

    private boolean tryLock() {
        Boolean result = stringRedisTemplate.opsForValue()
                .setIfAbsent(
                        RedisConstant.DRAW_LOCK_KEY,
                        "lock",
                        RedisConstant.DRAW_LOCK_TTL,
                        TimeUnit.SECONDS
                );
        return BooleanUtil.isTrue(result);
    }

    private void unlock() {
        stringRedisTemplate.delete(RedisConstant.DRAW_LOCK_KEY);
    }
}