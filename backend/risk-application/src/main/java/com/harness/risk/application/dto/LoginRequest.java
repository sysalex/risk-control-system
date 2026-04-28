package com.harness.risk.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public record LoginRequest(
        /**
         * 用户名。
         */
        @NotBlank(message = "用户名不能为空")
        String username,

        /**
         * 密码。
         */
        @NotBlank(message = "密码不能为空")
        String password
) {
}
