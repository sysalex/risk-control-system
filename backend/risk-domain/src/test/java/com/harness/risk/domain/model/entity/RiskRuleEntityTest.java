package com.harness.risk.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link RiskRuleEntity} 模型测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class RiskRuleEntityTest {

    /**
     * 验证风控规则模型映射到 risk_rules 表
     *
     * @throws NoSuchFieldException 字段不存在时测试失败
     */
    @Test
    void mapsRiskRuleToRiskRulesTable() throws NoSuchFieldException {
        TableName tableName = RiskRuleEntity.class.getAnnotation(TableName.class);
        Field id = RiskRuleEntity.class.getDeclaredField("id");
        Field conditions = RiskRuleEntity.class.getDeclaredField("conditions");
        Field creatorId = RiskRuleEntity.class.getDeclaredField("creatorId");
        Field enabled = RiskRuleEntity.class.getDeclaredField("enabled");

        assertNotNull(tableName);
        assertEquals("risk_rules", tableName.value());
        assertNotNull(id.getAnnotation(TableId.class));
        assertEquals("conditions", conditions.getAnnotation(TableField.class).value());
        assertEquals("creator_id", creatorId.getAnnotation(TableField.class).value());
        assertEquals("enabled", enabled.getAnnotation(TableField.class).value());
    }

    /**
     * 验证规则默认值适合新建草稿规则
     */
    @Test
    void defaultsToEnabledWithStandardPriority() {
        RiskRuleEntity riskRule = new RiskRuleEntity();

        assertTrue(riskRule.isEnabled());
        assertEquals(100, riskRule.getPriority());
    }
}
