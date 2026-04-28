package com.harness.risk.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体。
 * <p>
 * 保存认证和授权所需的基础用户资料；密码字段只存储哈希值。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@TableName("users")
public class User {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名。
     */
    @TableField("username")
    private String username;

    /**
     * 邮箱。
     */
    @TableField("email")
    private String email;

    /**
     * BCrypt 密码哈希。
     */
    @TableField("hashed_password")
    private String hashedPassword;

    /**
     * 用户角色。
     */
    @TableField("role")
    private UserRole role = UserRole.OPERATOR;

    /**
     * 是否启用。
     */
    @TableField("is_active")
    private boolean active = true;

    /**
     * 创建时间。
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
