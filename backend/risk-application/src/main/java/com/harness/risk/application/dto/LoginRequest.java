package com.harness.risk.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 *
 * @param username 用户名
 * @param password 密码
 * @author harness-agent
 * @since 2026-04-27
 */
public record LoginRequest(
        @NotBlank(message = "用户名不能为空")
        String username,

        @NotBlank(message = "密码不能为空")
        String password
) {
}
