package com.harness.risk.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 刷新请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {
    /**
     * Refresh Token。
     */
    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;
}
