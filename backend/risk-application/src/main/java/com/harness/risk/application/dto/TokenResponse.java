package com.harness.risk.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 响应 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    /**
     * Access Token。
     */
    private String accessToken;

    /**
     * Refresh Token。
     */
    private String refreshToken;

    /**
     * Access Token 过期秒数。
     */
    private long expiresIn;
}
