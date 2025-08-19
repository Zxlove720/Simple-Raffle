package com.wzb.interceptor;

import com.wzb.pojo.entity.User;
import com.wzb.util.ThreadUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 登录拦截器
     *
     * @param request  请求
     * @param response 响应
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 1.从ThreadLocal中获取用户信息
        User user = ThreadUtil.getUser();
        if (user == null) {
            // 1.1如果用户信息为null，则进行拦截并返回未登录信息
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        // 2.有用户信息，则放行
        return true;
    }
}
