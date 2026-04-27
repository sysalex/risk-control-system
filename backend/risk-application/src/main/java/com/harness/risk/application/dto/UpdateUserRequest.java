package com.harness.risk.application.dto;

import com.harness.risk.domain.user.UserRole;
import jakarta.validation.constraints.Email;

/**
 * 管理员更新用户请求 DTO
 *
 * @param email   邮箱（可选）
 * @param role    角色（可选）
 * @param active  是否启用（可选）
 * @author harness-agent
 * @since 2026-04-27
 */
public record UpdateUserRequest(
        @Email(message = "邮箱格式不正确")
        String email,

        UserRole role,

        Boolean active
) {
}
