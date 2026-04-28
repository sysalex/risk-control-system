package com.harness.risk.domain.audit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志实体。
 * <p>
 * 记录关键操作的历史快照，用于合规审计和问题追溯。
 * 审计日志创建后不允许被业务流程修改。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@TableName("audit_logs")
public class AuditLog {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户 ID。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 操作类型，例如 create、update、delete、enable、disable。
     */
    @TableField("action")
    private String action;

    /**
     * 资源类型，例如 rule、event、score、decision、user。
     */
    @TableField("resource_type")
    private String resourceType;

    /**
     * 资源 ID。
     */
    @TableField("resource_id")
    private Long resourceId;

    /**
     * 修改前值快照，JSON 格式。
     */
    @TableField("old_values")
    private String oldValues;

    /**
     * 修改后值快照，JSON 格式。
     */
    @TableField("new_values")
    private String newValues;

    /**
     * 操作时间。
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 操作者 IP 地址。
     */
    @TableField("ip_address")
    private String ipAddress;
}
