package com.harness.risk.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 工具类
 * <p>
 * 负责 access token / refresh token 的生成、解析和验证。
 * 使用 HS256 签名，密钥通过 SHA-256 派生固定为 32 字节。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long accessTokenExpireMinutes;
    private final long refreshTokenExpireDays;

    /**
     * @param secret                     JWT 密钥（任意长度，内部派生为 256-bit）
     * @param accessTokenExpireMinutes   access token 过期分钟数
     * @param refreshTokenExpireDays     refresh token 过期天数
     */
    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expire-minutes:30}") long accessTokenExpireMinutes,
            @Value("${app.jwt.refresh-token-expire-days:7}") long refreshTokenExpireDays) {
        this.key = deriveKey(secret);
        this.accessTokenExpireMinutes = accessTokenExpireMinutes;
        this.refreshTokenExpireDays = refreshTokenExpireDays;
    }

    private SecretKey deriveKey(String secret) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * 生成 access token
     *
     * @param userId   用户 ID
     * @param username 用户名
     * @param role     角色
     * @return JWT 字符串
     */
    public String generateAccessToken(Long userId, String username, String role) {
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofMinutes(accessTokenExpireMinutes));
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    /**
     * 生成 refresh token
     *
     * @param userId 用户 ID
     * @return JWT 字符串
     */
    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofDays(refreshTokenExpireDays));
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    /**
     * 解析并验证 token
     *
     * @param token JWT 字符串
     * @return 声明体
     * @throws io.jsonwebtoken.ExpiredJwtException token 已过期
     * @throws io.jsonwebtoken.JwtException        token 签名无效或格式错误
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
