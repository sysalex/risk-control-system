package com.harness.risk.application.dto;

import com.harness.risk.domain.enums.DecisionTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 决策响应 DTO。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionResponse {
    /**
     * 决策 ID。
     */
    private Long id;

    /**
     * 关联事件 ID。
     */
    private Long eventId;

    /**
     * 决策类型。
     */
    private DecisionTypeEnums decisionType;

    /**
     * 决策原因。
     */
    private String reason;

    /**
     * 备注。
     */
    private String notes;

    /**
     * 决策人用户 ID。
     */
    private Long decidedBy;

    /**
     * 决策时间。
     */
    private LocalDateTime decidedAt;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    private LocalDateTime updatedAt;
}
