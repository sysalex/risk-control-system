package com.harness.risk.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 风险评分响应 DTO
 *
 * @param id          评分 ID
 * @param eventId     关联事件 ID
 * @param subjectType 主体类型
 * @param subjectId   主体 ID
 * @param score       风险评分
 * @param maxScore    满分值
 * @param dimensions  维度明细 JSON
 * @param evaluatedAt 评估时间
 * @param evaluatorId 评估人用户 ID
 * @param createdAt   创建时间
 * @param updatedAt   更新时间
 * @author harness-agent
 * @since 2026-04-28
 */
public record ScoreResponse(
        Long id,
        Long eventId,
        String subjectType,
        String subjectId,
        BigDecimal score,
        BigDecimal maxScore,
        String dimensions,
        LocalDateTime evaluatedAt,
        Long evaluatorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
