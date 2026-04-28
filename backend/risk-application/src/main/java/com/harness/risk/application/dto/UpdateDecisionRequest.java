package com.harness.risk.application.dto;

import com.harness.risk.domain.decision.DecisionType;
import jakarta.validation.constraints.Size;

/** 更新决策请求。 */
public record UpdateDecisionRequest(
        DecisionType decisionType,

        @Size(max = 512, message = "决策原因长度不能超过 512")
        String reason,

        @Size(max = 1024, message = "备注长度不能超过 1024")
        String notes
) {
}
