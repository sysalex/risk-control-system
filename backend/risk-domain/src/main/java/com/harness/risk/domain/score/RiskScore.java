package com.harness.risk.domain.score;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 风险评分模型
 * <p>
 * 每条风险事件只保留一份最终评分结果，维度明细暂以 JSON 文本保存。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@TableName("risk_scores")
public class RiskScore {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("event_id")
    private Long eventId;

    @TableField("subject_type")
    private String subjectType;

    @TableField("subject_id")
    private String subjectId;

    @TableField("score")
    private BigDecimal score;

    @TableField("max_score")
    private BigDecimal maxScore = new BigDecimal("100.00");

    // 各维度评分明细 JSON
    @TableField("dimensions")
    private String dimensions;

    @TableField("evaluated_at")
    private LocalDateTime evaluatedAt;

    // 为空表示系统自动评估
    @TableField("evaluator_id")
    private Long evaluatorId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
