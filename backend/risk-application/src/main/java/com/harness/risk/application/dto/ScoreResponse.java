package com.harness.risk.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 风险评分响应 DTO。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
public record ScoreResponse(
        /**
         * 评分 ID。
         */
        Long id,

        /**
         * 关联事件 ID。
         */
        Long eventId,

        /**
         * 主体类型。
         */
        String subjectType,

        /**
         * 主体 ID。
         */
        String subjectId,

        /**
         * 风险评分。
         */
        BigDecimal score,

        /**
         * 满分值。
         */
        BigDecimal maxScore,

        /**
         * 各维度评分明细 JSON。
         */
        String dimensions,

        /**
         * 评估时间。
         */
        LocalDateTime evaluatedAt,

        /**
         * 评估人用户 ID。
         */
        Long evaluatorId,

        /**
         * 创建时间。
         */
        LocalDateTime createdAt,

        /**
         * 更新时间。
         */
        LocalDateTime updatedAt
) {
}
