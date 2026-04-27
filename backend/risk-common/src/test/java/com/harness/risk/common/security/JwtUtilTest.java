package com.harness.risk.common.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link JwtUtil} 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil("test-secret-key-for-unit-tests-only", 30, 7);

    /**
     * 验证 access token 生成后能被正确解析，且包含预期声明
     */
    @Test
    void generatesAndParsesValidAccessToken() {
        String token = jwtUtil.generateAccessToken(1L, "alice", "admin");
        var claims = jwtUtil.parseToken(token);

        assertEquals("1", claims.getSubject());
        assertEquals("alice", claims.get("username", String.class));
        assertEquals("admin", claims.get("role", String.class));
    }

    /**
     * 验证 refresh token 生成后能被正确解析，只包含 subject 不含扩展声明
     */
    @Test
    void generatesAndParsesValidRefreshToken() {
        String token = jwtUtil.generateRefreshToken(1L);
        var claims = jwtUtil.parseToken(token);

        assertEquals("1", claims.getSubject());
        assertNull(claims.get("username"));
    }

    /**
     * 验证过期 token 解析时抛出 ExpiredJwtException
     */
    @Test
    void throwsOnExpiredToken() {
        JwtUtil expiredJwt = new JwtUtil("test-secret-key-for-unit-tests-only", -1, -1);
        String token = expiredJwt.generateAccessToken(1L, "alice", "admin");

        assertThrows(ExpiredJwtException.class, () -> expiredJwt.parseToken(token));
    }

    /**
     * 验证被篡改签名的 token 解析时抛出 JwtException
     */
    @Test
    void throwsOnInvalidSignature() {
        String token = jwtUtil.generateAccessToken(1L, "alice", "admin");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThrows(JwtException.class, () -> jwtUtil.parseToken(tampered));
    }
}
