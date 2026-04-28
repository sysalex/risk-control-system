package com.harness.risk.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Token 刷新请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public record RefreshRequest(
        /**
         * Refresh Token。
         */
        @NotBlank(message = "refreshToken 不能为空")
        String refreshToken
) {
}
