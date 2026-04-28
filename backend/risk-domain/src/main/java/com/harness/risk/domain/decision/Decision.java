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

    // 主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 关联的风险事件 ID
    @TableField("event_id")
    private Long eventId;

    // 决策类型
    @TableField("decision_type")
    private DecisionType decisionType;

    // 决策原因
    @TableField("reason")
    private String reason;

    // 备注
    @TableField("notes")
    private String notes;

    // 决策人用户 ID
    @TableField("decided_by")
    private Long decidedBy;

    // 决策时间
    @TableField("decided_at")
    private LocalDateTime decidedAt;

    // 创建时间
    @TableField("created_at")
    private LocalDateTime createdAt;

    // 更新时间
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
