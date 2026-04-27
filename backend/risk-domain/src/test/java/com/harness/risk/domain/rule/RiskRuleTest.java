package com.harness.risk.domain.rule;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link RiskRule} 模型测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class RiskRuleTest {

    /**
     * 验证风控规则模型映射到 risk_rules 表
     *
     * @throws NoSuchFieldException 字段不存在时测试失败
     */
    @Test
    void mapsRiskRuleToRiskRulesTable() throws NoSuchFieldException {
        TableName tableName = RiskRule.class.getAnnotation(TableName.class);
        Field id = RiskRule.class.getDeclaredField("id");
        Field conditions = RiskRule.class.getDeclaredField("conditions");
        Field creatorId = RiskRule.class.getDeclaredField("creatorId");
        Field enabled = RiskRule.class.getDeclaredField("enabled");

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
        RiskRule riskRule = new RiskRule();

        assertTrue(riskRule.isEnabled());
        assertEquals(100, riskRule.getPriority());
    }
}
