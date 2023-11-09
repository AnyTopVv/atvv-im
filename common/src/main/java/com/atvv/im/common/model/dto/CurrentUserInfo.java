package com.atvv.im.common.model.dto;


import java.util.Objects;

/**
 * 基于当前线程的User信息
 */
public class CurrentUserInfo {
    /**
     * 当前线程的UserInfo
     */
    public static final ThreadLocal<Long> CURRENT_THREAD_USER_INFO = new ThreadLocal<>();

    /**
     * 设置当前线程的UserInfo对象
     * @param userId
     */
    public static void set(Long userId) {
        CURRENT_THREAD_USER_INFO.set(Objects.requireNonNull(userId));
    }

    /**
     * 从当前线程获取UserInfo对象
     * @return 当前线程下的UserInfo对象
     */
    public static Long get() {
        return Objects.requireNonNull(CURRENT_THREAD_USER_INFO.get());
    }

    /**
     * 移除当前线程的UserInfo对象
     */
    public static void remove() {
        CURRENT_THREAD_USER_INFO.remove();
    }
}
