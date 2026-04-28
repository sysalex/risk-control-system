package com.harness.risk.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 创建风险评分请求 DTO
 *
 * @param eventId     关联事件 ID
 * @param subjectType 主体类型
 * @param subjectId   主体 ID
 * @param score       风险评分
 * @param maxScore    满分值
 * @param dimensions  维度明细 JSON
 * @param evaluatorId 评估人用户 ID
 * @author harness-agent
 * @since 2026-04-28
 */
public record CreateScoreRequest(
        @NotNull(message = "事件 ID 不能为空")
        Long eventId,

        @NotBlank(message = "主体类型不能为空")
        @Size(max = 64, message = "主体类型长度不能超过 64")
        String subjectType,

        @NotBlank(message = "主体 ID 不能为空")
        @Size(max = 128, message = "主体 ID 长度不能超过 128")
        String subjectId,

        @NotNull(message = "评分不能为空")
        @DecimalMin(value = "0.00", message = "评分不能小于 0")
        @DecimalMax(value = "100.00", message = "评分不能大于 100")
        BigDecimal score,

        BigDecimal maxScore,

        @NotBlank(message = "维度明细不能为空")
        String dimensions,

        Long evaluatorId
) {
}
