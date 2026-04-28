package com.harness.risk.application.dto;

/**
 * Token 响应 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public record TokenResponse(
        /**
         * Access Token。
         */
        String accessToken,

        /**
         * Refresh Token。
         */
        String refreshToken,

        /**
         * Access Token 过期秒数。
         */
        long expiresIn
) {
}
