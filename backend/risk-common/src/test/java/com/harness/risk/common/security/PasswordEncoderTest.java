package com.harness.risk.common.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link PasswordEncoder} 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class PasswordEncoderTest {

    private final PasswordEncoder encoder = new PasswordEncoder();

    /**
     * 验证编码后的密码不等于原始密码，且使用 BCrypt 前缀
     */
    @Test
    void encodedPasswordDoesNotMatchRawAndUsesBcryptPrefix() {
        String raw = "password123";
        String encoded = encoder.encode(raw);

        assertNotEquals(raw, encoded);
        assertTrue(encoded.startsWith("$2a$"));
    }

    /**
     * 验证正确密码匹配成功
     */
    @Test
    void matchesCorrectPassword() {
        String raw = "password123";
        String encoded = encoder.encode(raw);

        assertTrue(encoder.matches(raw, encoded));
    }

    /**
     * 验证错误密码匹配失败
     */
    @Test
    void doesNotMatchWrongPassword() {
        String raw = "password123";
        String encoded = encoder.encode(raw);

        assertFalse(encoder.matches("wrong-password", encoded));
    }
}
