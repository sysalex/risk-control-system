package com.harness.risk.application.dto;

import com.harness.risk.domain.event.RiskEventStatus;
import jakarta.validation.constraints.Size;

/**
 * 更新风险事件请求 DTO
 *
 * @param status      事件状态（可选）
 * @param description 事件描述（可选）
 * @author harness-agent
 * @since 2026-04-27
 */
public record UpdateEventRequest(
        RiskEventStatus status,

        @Size(max = 1024, message = "事件描述长度不能超过 1024")
        String description
) {
}
