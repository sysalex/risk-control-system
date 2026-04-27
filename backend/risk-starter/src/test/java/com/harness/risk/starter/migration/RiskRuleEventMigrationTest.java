package com.harness.risk.starter.migration;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RiskRule / RiskEvent 表迁移脚本测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class RiskRuleEventMigrationTest {

    /**
     * 验证 V2 迁移创建风控规则和风险事件表
     *
     * @throws IOException 读取迁移脚本失败时测试失败
     */
    @Test
    void createsRiskRuleAndRiskEventTablesWithRequiredConstraints() throws IOException {
        String migration = readMigration();

        assertTrue(migration.contains("CREATE TABLE risk_rules"));
        assertTrue(migration.contains("name VARCHAR(128) NOT NULL"));
        assertTrue(migration.contains("conditions JSON NOT NULL"));
        assertTrue(migration.contains("actions JSON NOT NULL"));
        assertTrue(migration.contains("UNIQUE KEY uk_risk_rules_name"));
        assertTrue(migration.contains("CONSTRAINT fk_risk_rules_creator"));

        assertTrue(migration.contains("CREATE TABLE risk_events"));
        assertTrue(migration.contains("rule_id BIGINT NOT NULL"));
        assertTrue(migration.contains("risk_level VARCHAR(32) NOT NULL"));
        assertTrue(migration.contains("status VARCHAR(32) NOT NULL DEFAULT 'pending'"));
        assertTrue(migration.contains("CONSTRAINT fk_risk_events_rule"));
        assertTrue(migration.contains("KEY idx_risk_events_subject"));
        assertTrue(migration.contains("KEY idx_risk_events_status"));
    }

    private String readMigration() throws IOException {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("db/migration/V2__create_risk_rule_event_tables.sql")) {
            assertNotNull(input);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
