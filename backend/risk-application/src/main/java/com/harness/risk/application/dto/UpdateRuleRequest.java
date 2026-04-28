package com.harness.risk.application.dto;

import jakarta.validation.constraints.Size;

/** 更新风控规则请求。 */
public record UpdateRuleRequest(
        @Size(max = 128, message = "规则名称长度不能超过 128")
        String name,

        @Size(max = 512, message = "规则描述长度不能超过 512")
        String description,

        String conditions,

        String actions,

        Integer priority
) {
}
