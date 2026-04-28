package com.harness.risk.application.dto;

import com.harness.risk.domain.enums.RiskEventStatusEnums;
import com.harness.risk.domain.enums.RiskLevelEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 风险事件响应 DTO。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    /**
     * 事件 ID。
     */
    private Long id;

    /**
     * 触发规则 ID。
     */
    private Long ruleId;

    /**
     * 主体类型。
     */
    private String subjectType;

    /**
     * 主体 ID。
     */
    private String subjectId;

    /**
     * 风险等级。
     */
    private RiskLevelEnums riskLevel;

    /**
     * 处理状态。
     */
    private RiskEventStatusEnums status;

    /**
     * 事件描述。
     */
    private String description;

    /**
     * 触发时间。
     */
    private LocalDateTime triggeredAt;

    /**
     * 解决时间。
     */
    private LocalDateTime resolvedAt;

    /**
     * 解决人用户 ID。
     */
    private Long resolvedBy;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    private LocalDateTime updatedAt;
}
