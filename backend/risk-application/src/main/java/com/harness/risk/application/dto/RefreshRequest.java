package com.harness.risk.application.dto;

import jakarta.validation.constraints.NotBlank;

/** Token 刷新请求。 */
public record RefreshRequest(
        @NotBlank(message = "refreshToken 不能为空")
        String refreshToken
) {
}
