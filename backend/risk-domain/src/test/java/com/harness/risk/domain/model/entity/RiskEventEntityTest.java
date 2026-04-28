package com.harness.risk.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.harness.risk.domain.enums.RiskEventStatusEnums;
import com.harness.risk.domain.enums.RiskLevelEnums;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link RiskEventEntity} 模型测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class RiskEventEntityTest {

    /**
     * 验证风险事件模型映射到 risk_events 表
     *
     * @throws NoSuchFieldException 字段不存在时测试失败
     */
    @Test
    void mapsRiskEventToRiskEventsTable() throws NoSuchFieldException {
        TableName tableName = RiskEventEntity.class.getAnnotation(TableName.class);
        Field id = RiskEventEntity.class.getDeclaredField("id");
        Field ruleId = RiskEventEntity.class.getDeclaredField("ruleId");
        Field subjectType = RiskEventEntity.class.getDeclaredField("subjectType");
        Field riskLevel = RiskEventEntity.class.getDeclaredField("riskLevel");
        Field resolvedBy = RiskEventEntity.class.getDeclaredField("resolvedBy");

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
        RiskEventEntity riskEvent = new RiskEventEntity();

        assertEquals(RiskEventStatusEnums.PENDING, riskEvent.getStatus());
    }

    /**
     * 验证风险等级和事件状态枚举值与数据库存储值一致
     */
    @Test
    void enumValuesMatchDatabaseValues() {
        assertEquals("low", RiskLevelEnums.LOW.getValue());
        assertEquals("medium", RiskLevelEnums.MEDIUM.getValue());
        assertEquals("high", RiskLevelEnums.HIGH.getValue());
        assertEquals("critical", RiskLevelEnums.CRITICAL.getValue());

        assertEquals("pending", RiskEventStatusEnums.PENDING.getValue());
        assertEquals("investigating", RiskEventStatusEnums.INVESTIGATING.getValue());
        assertEquals("resolved", RiskEventStatusEnums.RESOLVED.getValue());
        assertEquals("false_positive", RiskEventStatusEnums.FALSE_POSITIVE.getValue());
    }
}
