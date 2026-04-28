package com.harness.risk.application.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新风控规则请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRuleRequest {
    /**
     * 规则名称。
     */
    @Size(max = 128, message = "规则名称长度不能超过 128")
    private String name;

    /**
     * 规则描述。
     */
    @Size(max = 512, message = "规则描述长度不能超过 512")
    private String description;

    /**
     * 规则条件 JSON。
     */
    private String conditions;

    /**
     * 触发动作 JSON。
     */
    private String actions;

    /**
     * 优先级。
     */
    private Integer priority;
}
