package com.harness.risk.application.dto;

import com.harness.risk.domain.event.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 创建风险事件请求。 */
public record CreateEventRequest(
        @NotNull(message = "规则 ID 不能为空")
        Long ruleId,

        @NotBlank(message = "主体类型不能为空")
        @Size(max = 64, message = "主体类型长度不能超过 64")
        String subjectType,

        @NotBlank(message = "主体 ID 不能为空")
        @Size(max = 128, message = "主体 ID 长度不能超过 128")
        String subjectId,

        @NotNull(message = "风险等级不能为空")
        RiskLevel riskLevel,

        @Size(max = 1024, message = "事件描述长度不能超过 1024")
        String description
) {
}
