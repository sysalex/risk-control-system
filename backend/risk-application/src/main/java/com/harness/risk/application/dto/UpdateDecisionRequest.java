package com.harness.risk.application.dto;

import com.harness.risk.domain.enums.DecisionTypeEnums;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新决策请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDecisionRequest {
    /**
     * 决策类型。
     */
    private DecisionTypeEnums decisionType;

    /**
     * 决策原因。
     */
    @Size(max = 512, message = "决策原因长度不能超过 512")
    private String reason;

    /**
     * 备注。
     */
    @Size(max = 1024, message = "备注长度不能超过 1024")
    private String notes;
}
