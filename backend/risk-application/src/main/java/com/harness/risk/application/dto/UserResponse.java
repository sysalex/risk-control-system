package com.harness.risk.application.dto;

import com.harness.risk.domain.user.UserRole;

import java.time.LocalDateTime;

/**
 * 用户响应 DTO（不含密码）
 *
 * @param id        用户 ID
 * @param username  用户名
 * @param email     邮箱
 * @param role      角色
 * @param active    是否启用
 * @param createdAt 创建时间
 * @author harness-agent
 * @since 2026-04-27
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        UserRole role,
        boolean active,
        LocalDateTime createdAt
) {
}
