package com.harness.risk.application.dto;

import com.harness.risk.domain.user.UserRole;
import jakarta.validation.constraints.Email;

/**
 * 管理员更新用户请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public record UpdateUserRequest(
        /**
         * 邮箱。
         */
        @Email(message = "邮箱格式不正确")
        String email,

        /**
         * 用户角色。
         */
        UserRole role,

        /**
         * 是否启用。
         */
        Boolean active
) {
}
