package com.harness.risk.domain.audit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志模型
 * <p>
 * 记录所有关键操作的历史快照，用于合规审计和问题追溯。
 * 审计日志为不可变记录，创建后不允许修改。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@TableName("audit_logs")
public class AuditLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    // create / update / delete / enable / disable
    @TableField("action")
    private String action;

    // rule / event / score / decision / user
    @TableField("resource_type")
    private String resourceType;

    @TableField("resource_id")
    private Long resourceId;

    // 修改前值 JSON
    @TableField("old_values")
    private String oldValues;

    // 修改后值 JSON
    @TableField("new_values")
    private String newValues;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("ip_address")
    private String ipAddress;
}
