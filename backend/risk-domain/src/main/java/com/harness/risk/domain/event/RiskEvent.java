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

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 触发的规则 ID */
    @TableField("rule_id")
    private Long ruleId;

    /** 主体类型 */
    @TableField("subject_type")
    private String subjectType;

    /** 主体 ID */
    @TableField("subject_id")
    private String subjectId;

    /** 风险等级 */
    @TableField("risk_level")
    private RiskLevel riskLevel;

    /** 处理状态 */
    @TableField("status")
    private RiskEventStatus status = RiskEventStatus.PENDING;

    /** 事件描述 */
    @TableField("description")
    private String description;

    /** 触发时间 */
    @TableField("triggered_at")
    private LocalDateTime triggeredAt;

    /** 解决时间 */
    @TableField("resolved_at")
    private LocalDateTime resolvedAt;

    /** 解决人用户 ID */
    @TableField("resolved_by")
    private Long resolvedBy;

    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
