package com.campushub;

import com.campushub.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT 工具单元测试
 */
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", "campushub-test-secret-key-2026-very-long-secure");
        ReflectionTestUtils.setField(jwtUtils, "expire", 7200000L);
    }

    @Test
    void 生成token非空且包含用户信息() {
        String token = jwtUtils.generateToken(1L, "student", "STUDENT");
        assertNotNull(token);
        assertFalse(token.isEmpty());
        Claims claims = jwtUtils.parseToken(token);
        assertEquals(1L, claims.get("userId", Long.class));
        assertEquals("student", claims.get("username", String.class));
        assertEquals("STUDENT", claims.get("role", String.class));
    }

    @Test
    void 解析用户ID正确() {
        String token = jwtUtils.generateToken(42L, "admin", "ADMIN");
        Long userId = jwtUtils.getUserId(token);
        assertEquals(42L, userId);
    }

    @Test
    void 无效token解析抛异常() {
        assertThrows(Exception.class, () -> jwtUtils.parseToken("invalid.token.value"));
    }
}
