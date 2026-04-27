package com.harness.risk.domain.score;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link RiskScore} 模型测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class RiskScoreTest {

    /**
     * 验证风险评分模型映射到 risk_scores 表
     *
     * @throws NoSuchFieldException 字段不存在时测试失败
     */
    @Test
    void mapsRiskScoreToRiskScoresTable() throws NoSuchFieldException {
        TableName tableName = RiskScore.class.getAnnotation(TableName.class);
        Field id = RiskScore.class.getDeclaredField("id");
        Field eventId = RiskScore.class.getDeclaredField("eventId");
        Field maxScore = RiskScore.class.getDeclaredField("maxScore");
        Field dimensions = RiskScore.class.getDeclaredField("dimensions");
        Field evaluatorId = RiskScore.class.getDeclaredField("evaluatorId");

        assertNotNull(tableName);
        assertEquals("risk_scores", tableName.value());
        assertNotNull(id.getAnnotation(TableId.class));
        assertEquals("event_id", eventId.getAnnotation(TableField.class).value());
        assertEquals("max_score", maxScore.getAnnotation(TableField.class).value());
        assertEquals("dimensions", dimensions.getAnnotation(TableField.class).value());
        assertEquals("evaluator_id", evaluatorId.getAnnotation(TableField.class).value());
    }

    /**
     * 验证默认满分值为 100
     */
    @Test
    void defaultsToOneHundredMaxScore() {
        RiskScore riskScore = new RiskScore();

        assertEquals(new BigDecimal("100.00"), riskScore.getMaxScore());
    }
}
