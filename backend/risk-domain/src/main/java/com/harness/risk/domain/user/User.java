package com.harness.risk.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户模型
 * <p>
 * 保存认证和授权所需的基础用户资料；密码字段只存储哈希值。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@TableName("users")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("email")
    private String email;

    // BCrypt 密码哈希
    @TableField("hashed_password")
    private String hashedPassword;

    @TableField("role")
    private UserRole role = UserRole.OPERATOR;

    @TableField("is_active")
    private boolean active = true;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
