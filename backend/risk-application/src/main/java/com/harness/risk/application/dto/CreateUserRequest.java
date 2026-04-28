package com.harness.risk.application.dto;

import com.harness.risk.domain.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 管理员创建用户请求。 */
public record CreateUserRequest(
        @NotBlank(message = "用户名不能为空")
        String username,

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        String email,

        @NotBlank(message = "密码不能为空")
        @Size(min = 8, message = "密码长度至少 8 位")
        String password,

        @NotNull(message = "角色不能为空")
        UserRole role
) {
}
