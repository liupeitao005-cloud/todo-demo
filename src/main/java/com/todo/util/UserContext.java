package com.todo.util;

/**
 * 保存当前登录用户 ID，后续查询待办/任务时在 SQL 里加 user_id 条件即可实现数据隔离。
 */
public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
    }
}