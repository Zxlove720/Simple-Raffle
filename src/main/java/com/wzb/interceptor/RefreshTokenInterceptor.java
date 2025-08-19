package com.wzb.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.wzb.constant.RedisConstant;
import com.wzb.pojo.entity.User;
import com.wzb.util.ThreadUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 用户登录状态刷新拦截器
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 1.获取请求头中的token
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            // 1.1如果没有请求头，直接放行到下一个拦截器
            return true;
        }
        // 2.基于token获取redis中的用户
        String key = RedisConstant.USER_LOGIN_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
        // 3.判断用户是否存在
        if (userMap.isEmpty()) {
            // 3.1如果用户不存在，直接放行至下一个拦截器
            return true;
        }
        // 4.将查询到的Hash数据转换为UserDTO对象
        User user = BeanUtil.fillBeanWithMap(userMap, new User(), false);
        // 5.保存用户信息到ThreadLocal
        ThreadUtil.saveUser(user);
        // 6.刷新token有效期
        stringRedisTemplate.expire(key, RedisConstant.USER_LOGIN_TTL, TimeUnit.MINUTES);
        // 7.放行到下一个拦截器
        return true;
    }

    /**
     * 响应时从ThreadLocal移除用户
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception exception) {
        // 移除用户
        ThreadUtil.removeUser();
    }
}
