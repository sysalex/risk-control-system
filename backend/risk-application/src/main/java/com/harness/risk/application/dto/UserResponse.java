package com.harness.risk.application.dto;

import com.harness.risk.domain.user.UserRole;

import java.time.LocalDateTime;

/** 用户响应，不包含密码。 */
public record UserResponse(
        Long id,
        String username,
        String email,
        UserRole role,
        boolean active,
        LocalDateTime createdAt
) {
}
