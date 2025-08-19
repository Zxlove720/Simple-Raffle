package com.wzb.util;

import com.wzb.pojo.entity.User;

public class ThreadUtil {

    public static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    // 设置当前线程局部变量的值
    public static void saveUser(User user) {
        threadLocal.set(user);
    }

    // 获取当前线程的局部变量的值
    public static User getUser() {
        return threadLocal.get();
    }

    // 清空当前线程的局部变量的值
    public static void removeUser() {
        threadLocal.remove();
    }

}
