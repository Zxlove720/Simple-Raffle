package com.wzb.interceptor;

import com.wzb.pojo.entity.User;
import com.wzb.util.ThreadUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class ShopInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        User user = ThreadUtil.getUser();
        String path = request.getPathInfo();
        return !path.startsWith("shop") || user.getStatus() == 1;
    }

}
