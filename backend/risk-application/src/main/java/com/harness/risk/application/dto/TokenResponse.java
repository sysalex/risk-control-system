package com.harness.risk.application.dto;

/** Token 响应。 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}
