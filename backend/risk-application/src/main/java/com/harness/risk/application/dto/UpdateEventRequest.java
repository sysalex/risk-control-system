package com.harness.risk.application.dto;

import com.harness.risk.domain.event.RiskEventStatus;
import jakarta.validation.constraints.Size;

/**
 * 更新风险事件请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public record UpdateEventRequest(
        /**
         * 事件状态。
         */
        RiskEventStatus status,

        /**
         * 事件描述。
         */
        @Size(max = 1024, message = "事件描述长度不能超过 1024")
        String description
) {
}
