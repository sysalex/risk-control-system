package com.harness.risk.application.dto;

import com.harness.risk.domain.decision.DecisionType;
import jakarta.validation.constraints.Size;

/**
 * 更新决策请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
public record UpdateDecisionRequest(
        /**
         * 决策类型。
         */
        DecisionType decisionType,

        /**
         * 决策原因。
         */
        @Size(max = 512, message = "决策原因长度不能超过 512")
        String reason,

        /**
         * 备注。
         */
        @Size(max = 1024, message = "备注长度不能超过 1024")
        String notes
) {
}
