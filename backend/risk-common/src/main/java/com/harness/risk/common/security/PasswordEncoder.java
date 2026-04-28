package com.harness.risk.common.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码编码器
 * <p>
 * 对 {@link BCryptPasswordEncoder} 的薄包装，便于单元测试中替换为 mock。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Component
public class PasswordEncoder {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder(12);

    /**
     * 使用 BCrypt 编码原始密码
     *
     * @param rawPassword 原始密码
     * @return BCrypt 哈希值
     */
    public String encode(String rawPassword) {
        return delegate.encode(rawPassword);
    }

    /**
     * 校验原始密码是否与编码后的密码匹配
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 编码后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return delegate.matches(rawPassword, encodedPassword);
    }
}
