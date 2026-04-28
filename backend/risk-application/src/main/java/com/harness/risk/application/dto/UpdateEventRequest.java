package com.harness.risk.application.dto;

import com.harness.risk.domain.event.RiskEventStatus;
import jakarta.validation.constraints.Size;

/** 更新风险事件请求。 */
public record UpdateEventRequest(
        RiskEventStatus status,

        @Size(max = 1024, message = "事件描述长度不能超过 1024")
        String description
) {
}
