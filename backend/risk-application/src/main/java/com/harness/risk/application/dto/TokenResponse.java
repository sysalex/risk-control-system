package com.harness.risk.application.dto;

/**
 * Token 响应 DTO
 *
 * @param accessToken  access token
 * @param refreshToken refresh token
 * @param expiresIn    access token 过期秒数
 * @author harness-agent
 * @since 2026-04-27
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}
