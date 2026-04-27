package com.harness.risk.starter.migration;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User 表迁移脚本测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class UserMigrationTest {

    /**
     * 验证 V1 迁移创建 users 表和核心约束
     *
     * @throws IOException 读取迁移脚本失败时测试失败
     */
    @Test
    void createsUsersTableWithRequiredColumnsAndIndexes() throws IOException {
        String migration = readMigration();

        assertTrue(migration.contains("CREATE TABLE users"));
        assertTrue(migration.contains("username VARCHAR(64) NOT NULL"));
        assertTrue(migration.contains("email VARCHAR(128) NOT NULL"));
        assertTrue(migration.contains("hashed_password VARCHAR(255) NOT NULL"));
        assertTrue(migration.contains("role VARCHAR(32) NOT NULL"));
        assertTrue(migration.contains("is_active TINYINT(1) NOT NULL DEFAULT 1"));
        assertTrue(migration.contains("UNIQUE KEY uk_users_username"));
        assertTrue(migration.contains("UNIQUE KEY uk_users_email"));
        assertTrue(migration.contains("COMMENT='用户表'"));
    }

    private String readMigration() throws IOException {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("db/migration/V1__create_users_table.sql")) {
            assertNotNull(input);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
