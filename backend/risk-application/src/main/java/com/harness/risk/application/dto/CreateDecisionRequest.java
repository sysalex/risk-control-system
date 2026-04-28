package com.harness.risk.application.dto;

import com.harness.risk.domain.decision.DecisionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 创建决策请求。 */
public record CreateDecisionRequest(
        @NotNull(message = "事件 ID 不能为空")
        Long eventId,

        @NotNull(message = "决策类型不能为空")
        DecisionType decisionType,

        @NotBlank(message = "决策原因不能为空")
        @Size(max = 512, message = "决策原因长度不能超过 512")
        String reason,

        @Size(max = 1024, message = "备注长度不能超过 1024")
        String notes,

        @NotNull(message = "决策人不能为空")
        Long decidedBy
) {
}
