package com.harness.risk.application.dto;

import com.harness.risk.domain.user.UserRole;

import java.time.LocalDateTime;

/**
 * 用户响应 DTO，不包含密码。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public record UserResponse(
        /**
         * 用户 ID。
         */
        Long id,

        /**
         * 用户名。
         */
        String username,

        /**
         * 邮箱。
         */
        String email,

        /**
         * 用户角色。
         */
        UserRole role,

        /**
         * 是否启用。
         */
        boolean active,

        /**
         * 创建时间。
         */
        LocalDateTime createdAt
) {
}
