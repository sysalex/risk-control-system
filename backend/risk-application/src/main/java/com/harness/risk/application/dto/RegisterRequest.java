package com.harness.risk.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 注册请求 DTO
 *
 * @param username 用户名
 * @param email    邮箱
 * @param password 密码（至少 8 位）
 * @author harness-agent
 * @since 2026-04-27
 */
public record RegisterRequest(
        @NotBlank(message = "用户名不能为空")
        String username,

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        String email,

        @NotBlank(message = "密码不能为空")
        @Size(min = 8, message = "密码长度至少 8 位")
        String password
) {
}
