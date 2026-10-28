package com.campushub.common.utils;

import com.campushub.common.exception.BusinessException;

/**
 * 当前登录用户上下文（ThreadLocal）
 * 由 JwtAuthInterceptor 在请求进入时填充，请求结束清除
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    public static void set(Long userId, String username, String role) {
        USER_ID.set(userId);
        USERNAME.set(username);
        ROLE.set(role);
    }

    public static Long getUserId() {
        Long id = USER_ID.get();
        if (id == null) {
            throw new BusinessException("未登录或登录已过期");
        }
        return id;
    }

    public static String getUsername() {
        return USERNAME.get();
    }

    public static String getRole() {
        String role = ROLE.get();
        return role == null ? "STUDENT" : role;
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getRole());
    }

    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLE.remove();
    }
}
