package com.harness.risk.domain.decision;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link Decision} 模型测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class DecisionTest {

    /**
     * 验证决策模型映射到 decisions 表
     *
     * @throws NoSuchFieldException 字段不存在时测试失败
     */
    @Test
    void mapsDecisionToDecisionsTable() throws NoSuchFieldException {
        TableName tableName = Decision.class.getAnnotation(TableName.class);
        Field id = Decision.class.getDeclaredField("id");
        Field eventId = Decision.class.getDeclaredField("eventId");
        Field decisionType = Decision.class.getDeclaredField("decisionType");
        Field decidedBy = Decision.class.getDeclaredField("decidedBy");
        Field decidedAt = Decision.class.getDeclaredField("decidedAt");

        assertNotNull(tableName);
        assertEquals("decisions", tableName.value());
        assertNotNull(id.getAnnotation(TableId.class));
        assertEquals("event_id", eventId.getAnnotation(TableField.class).value());
        assertEquals("decision_type", decisionType.getAnnotation(TableField.class).value());
        assertEquals("decided_by", decidedBy.getAnnotation(TableField.class).value());
        assertEquals("decided_at", decidedAt.getAnnotation(TableField.class).value());
    }

    /**
     * 验证决策类型枚举值与数据库存储值一致
     */
    @Test
    void decisionTypeValuesMatchDatabaseValues() {
        assertEquals("approve", DecisionType.APPROVE.getValue());
        assertEquals("reject", DecisionType.REJECT.getValue());
        assertEquals("manual_review", DecisionType.MANUAL_REVIEW.getValue());
        assertEquals("escalate", DecisionType.ESCALATE.getValue());
    }
}
