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

    // 主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 规则名称
    @TableField("name")
    private String name;

    // 规则描述
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

    // 是否启用
    @TableField("enabled")
    private boolean enabled = true;

    // 创建者用户 ID
    @TableField("creator_id")
    private Long creatorId;

    // 创建时间
    @TableField("created_at")
    private LocalDateTime createdAt;

    // 更新时间
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
