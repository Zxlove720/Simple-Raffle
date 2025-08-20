package com.wzb.config;

import com.wzb.interceptor.LoginInterceptor;
import com.wzb.interceptor.RefreshTokenInterceptor;
import com.wzb.interceptor.ShopInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ShopInterceptor())
                .excludePathPatterns(
                        "/user/login"
                )
                .order(2);
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/user/login"
                ).order(1);
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
                .order(0);

    }

}
