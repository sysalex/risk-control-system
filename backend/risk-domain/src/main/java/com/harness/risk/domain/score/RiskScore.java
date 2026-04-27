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

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 关联的风险事件 ID */
    @TableField("event_id")
    private Long eventId;

    /** 主体类型 */
    @TableField("subject_type")
    private String subjectType;

    /** 主体 ID */
    @TableField("subject_id")
    private String subjectId;

    /** 风险评分 */
    @TableField("score")
    private BigDecimal score;

    /** 满分值 */
    @TableField("max_score")
    private BigDecimal maxScore = new BigDecimal("100.00");

    /** 各维度评分明细 JSON */
    @TableField("dimensions")
    private String dimensions;

    /** 评估时间 */
    @TableField("evaluated_at")
    private LocalDateTime evaluatedAt;

    /** 评估人用户 ID，可为空表示系统自动评估 */
    @TableField("evaluator_id")
    private Long evaluatorId;

    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
