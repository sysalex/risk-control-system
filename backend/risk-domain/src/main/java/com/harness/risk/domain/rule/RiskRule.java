package com.harness.risk.domain.rule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风控规则模型
 * <p>
 * 条件和动作暂以 JSON 文本保存，规则引擎落地后再演进为类型化表达式。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@TableName("risk_rules")
public class RiskRule {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    // 规则条件 JSON
    @TableField("conditions")
    private String conditions;

    // 触发动作 JSON
    @TableField("actions")
    private String actions;

    // 数值越小优先级越高
    @TableField("priority")
    private Integer priority = 100;

    @TableField("enabled")
    private boolean enabled = true;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
