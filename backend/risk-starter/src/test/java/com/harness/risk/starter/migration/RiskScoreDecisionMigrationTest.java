package com.harness.risk.starter.migration;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RiskScore / Decision 表迁移脚本测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class RiskScoreDecisionMigrationTest {

    /**
     * 验证 V3 迁移创建风险评分和决策表
     *
     * @throws IOException 读取迁移脚本失败时测试失败
     */
    @Test
    void createsRiskScoreAndDecisionTablesWithRequiredConstraints() throws IOException {
        String migration = readMigration();

        assertTrue(migration.contains("CREATE TABLE risk_scores"));
        assertTrue(migration.contains("event_id BIGINT NOT NULL"));
        assertTrue(migration.contains("score DECIMAL(5,2) NOT NULL"));
        assertTrue(migration.contains("max_score DECIMAL(5,2) NOT NULL DEFAULT 100.00"));
        assertTrue(migration.contains("dimensions JSON NOT NULL"));
        assertTrue(migration.contains("UNIQUE KEY uk_risk_scores_event"));
        assertTrue(migration.contains("CONSTRAINT fk_risk_scores_event"));

        assertTrue(migration.contains("CREATE TABLE decisions"));
        assertTrue(migration.contains("decision_type VARCHAR(32) NOT NULL"));
        assertTrue(migration.contains("reason VARCHAR(512) NOT NULL"));
        assertTrue(migration.contains("UNIQUE KEY uk_decisions_event"));
        assertTrue(migration.contains("CONSTRAINT fk_decisions_event"));
        assertTrue(migration.contains("CONSTRAINT fk_decisions_decided_by"));
    }

    private String readMigration() throws IOException {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("db/migration/V3__create_risk_score_decision_tables.sql")) {
            assertNotNull(input);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
