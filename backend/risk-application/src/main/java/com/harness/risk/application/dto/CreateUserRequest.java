package com.harness.risk.application.dto;

import com.harness.risk.domain.enums.UserRoleEnums;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员创建用户请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    /**
     * 用户名。
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 邮箱。
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 密码。
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度至少 8 位")
    private String password;

    /**
     * 用户角色。
     */
    @NotNull(message = "角色不能为空")
    private UserRoleEnums role;
}
