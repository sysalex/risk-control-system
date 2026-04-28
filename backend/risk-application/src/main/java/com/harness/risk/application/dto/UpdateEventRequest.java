package com.harness.risk.application.dto;

import com.harness.risk.domain.enums.RiskEventStatusEnums;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新风险事件请求 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {
    /**
     * 事件状态。
     */
    private RiskEventStatusEnums status;

    /**
     * 事件描述。
     */
    @Size(max = 1024, message = "事件描述长度不能超过 1024")
    private String description;
}
