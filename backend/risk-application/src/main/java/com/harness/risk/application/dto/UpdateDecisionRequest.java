package com.harness.risk.application.dto;

import com.harness.risk.domain.decision.DecisionType;
import jakarta.validation.constraints.Size;

/**
 * 更新决策请求 DTO
 *
 * @param decisionType 决策类型
 * @param reason       决策原因
 * @param notes        备注
 * @author harness-agent
 * @since 2026-04-28
 */
public record UpdateDecisionRequest(
        DecisionType decisionType,

        @Size(max = 512, message = "决策原因长度不能超过 512")
        String reason,

        @Size(max = 1024, message = "备注长度不能超过 1024")
        String notes
) {
}
