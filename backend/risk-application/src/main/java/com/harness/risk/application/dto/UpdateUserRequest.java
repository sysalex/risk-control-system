package com.harness.risk.application.dto;

import com.harness.risk.domain.user.UserRole;
import jakarta.validation.constraints.Email;

/** 管理员更新用户请求。 */
public record UpdateUserRequest(
        @Email(message = "邮箱格式不正确")
        String email,

        UserRole role,

        Boolean active
) {
}
