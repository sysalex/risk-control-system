package com.harness.risk.application.dto;

import com.harness.risk.domain.enums.UserRoleEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户响应 DTO，不包含密码。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    /**
     * 用户 ID。
     */
    private Long id;

    /**
     * 用户名。
     */
    private String username;

    /**
     * 邮箱。
     */
    private String email;

    /**
     * 用户角色。
     */
    private UserRoleEnums role;

    /**
     * 是否启用。
     */
    private boolean active;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;
}
