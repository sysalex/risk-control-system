package com.harness.risk.domain.event;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风险事件模型
 * <p>
 * 记录规则命中的风险事实，处理流程和决策在后续模块补齐。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@TableName("risk_events")
public class RiskEvent {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("rule_id")
    private Long ruleId;

    @TableField("subject_type")
    private String subjectType;

    @TableField("subject_id")
    private String subjectId;

    @TableField("risk_level")
    private RiskLevel riskLevel;

    @TableField("status")
    private RiskEventStatus status = RiskEventStatus.PENDING;

    @TableField("description")
    private String description;

    @TableField("triggered_at")
    private LocalDateTime triggeredAt;

    @TableField("resolved_at")
    private LocalDateTime resolvedAt;

    @TableField("resolved_by")
    private Long resolvedBy;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
