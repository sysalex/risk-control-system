package com.harness.risk.starter.migration;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 审计日志表迁移脚本测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class AuditLogMigrationTest {

    /**
     * 验证 V4 迁移创建 audit_logs 表和核心约束
     *
     * @throws IOException 读取迁移脚本失败时测试失败
     */
    @Test
    void createsAuditLogsTableWithRequiredColumnsAndIndexes() throws IOException {
        String migration = readMigration();

        assertTrue(migration.contains("CREATE TABLE audit_logs"));
        assertTrue(migration.contains("user_id BIGINT NOT NULL"));
        assertTrue(migration.contains("action VARCHAR(32) NOT NULL"));
        assertTrue(migration.contains("resource_type VARCHAR(32) NOT NULL"));
        assertTrue(migration.contains("resource_id BIGINT NOT NULL"));
        assertTrue(migration.contains("old_values JSON"));
        assertTrue(migration.contains("new_values JSON"));
        assertTrue(migration.contains("ip_address VARCHAR(64)"));
        assertTrue(migration.contains("PRIMARY KEY (id)"));
        assertTrue(migration.contains("KEY idx_audit_logs_user"));
        assertTrue(migration.contains("KEY idx_audit_logs_action"));
        assertTrue(migration.contains("KEY idx_audit_logs_resource"));
        assertTrue(migration.contains("KEY idx_audit_logs_created_at"));
        assertTrue(migration.contains("CONSTRAINT fk_audit_logs_user"));
        assertTrue(migration.contains("COMMENT='审计日志表'"));
    }

    private String readMigration() throws IOException {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("db/migration/V4__create_audit_log_table.sql")) {
            assertNotNull(input);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
