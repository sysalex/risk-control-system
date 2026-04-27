package com.harness.risk.domain.event;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link RiskEvent} 模型测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class RiskEventTest {

    /**
     * 验证风险事件模型映射到 risk_events 表
     *
     * @throws NoSuchFieldException 字段不存在时测试失败
     */
    @Test
    void mapsRiskEventToRiskEventsTable() throws NoSuchFieldException {
        TableName tableName = RiskEvent.class.getAnnotation(TableName.class);
        Field id = RiskEvent.class.getDeclaredField("id");
        Field ruleId = RiskEvent.class.getDeclaredField("ruleId");
        Field subjectType = RiskEvent.class.getDeclaredField("subjectType");
        Field riskLevel = RiskEvent.class.getDeclaredField("riskLevel");
        Field resolvedBy = RiskEvent.class.getDeclaredField("resolvedBy");

        assertNotNull(tableName);
        assertEquals("risk_events", tableName.value());
        assertNotNull(id.getAnnotation(TableId.class));
        assertEquals("rule_id", ruleId.getAnnotation(TableField.class).value());
        assertEquals("subject_type", subjectType.getAnnotation(TableField.class).value());
        assertEquals("risk_level", riskLevel.getAnnotation(TableField.class).value());
        assertEquals("resolved_by", resolvedBy.getAnnotation(TableField.class).value());
    }

    /**
     * 验证事件默认状态适合新触发事件
     */
    @Test
    void defaultsToPendingStatus() {
        RiskEvent riskEvent = new RiskEvent();

        assertEquals(RiskEventStatus.PENDING, riskEvent.getStatus());
    }

    /**
     * 验证风险等级和事件状态枚举值与数据库存储值一致
     */
    @Test
    void enumValuesMatchDatabaseValues() {
        assertEquals("low", RiskLevel.LOW.getValue());
        assertEquals("medium", RiskLevel.MEDIUM.getValue());
        assertEquals("high", RiskLevel.HIGH.getValue());
        assertEquals("critical", RiskLevel.CRITICAL.getValue());

        assertEquals("pending", RiskEventStatus.PENDING.getValue());
        assertEquals("investigating", RiskEventStatus.INVESTIGATING.getValue());
        assertEquals("resolved", RiskEventStatus.RESOLVED.getValue());
        assertEquals("false_positive", RiskEventStatus.FALSE_POSITIVE.getValue());
    }
}
