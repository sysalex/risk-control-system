package com.harness.risk.application.dto;

import com.harness.risk.domain.enums.UserRoleEnums;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员更新用户请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    /**
     * 邮箱。
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户角色。
     */
    private UserRoleEnums role;

    /**
     * 是否启用。
     */
    private Boolean active;
}
