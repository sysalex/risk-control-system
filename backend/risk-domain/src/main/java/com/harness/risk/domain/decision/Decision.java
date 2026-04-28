package com.harness.risk.domain.decision;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 决策记录模型
 * <p>
 * 同一风险事件只能生成一条最终决策，后续审计模块负责记录变更轨迹。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@TableName("decisions")
public class Decision {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("event_id")
    private Long eventId;

    @TableField("decision_type")
    private DecisionType decisionType;

    @TableField("reason")
    private String reason;

    @TableField("notes")
    private String notes;

    @TableField("decided_by")
    private Long decidedBy;

    @TableField("decided_at")
    private LocalDateTime decidedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
