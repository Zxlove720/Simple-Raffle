package com.wzb.util;

public class ThreadUtil {

    public static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    // 设置当前线程局部变量的值
    public static void setCurrentId(Integer id) {
        threadLocal.set(id);
    }

    // 获取当前线程的局部变量的值
    public static Integer getCurrentId() {
        return threadLocal.get();
    }

    // 清空当前线程的局部变量的值
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
