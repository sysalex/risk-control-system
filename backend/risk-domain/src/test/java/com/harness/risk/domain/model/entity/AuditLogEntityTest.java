package com.harness.risk.domain.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AuditLogEntity} 模型测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class AuditLogEntityTest {

    /**
     * 验证审计日志模型映射到 audit_logs 表
     *
     * @throws NoSuchFieldException 字段不存在时测试失败
     */
    @Test
    void mapsAuditLogToAuditLogsTable() throws NoSuchFieldException {
        TableName tableName = AuditLogEntity.class.getAnnotation(TableName.class);
        Field id = AuditLogEntity.class.getDeclaredField("id");
        Field userId = AuditLogEntity.class.getDeclaredField("userId");
        Field action = AuditLogEntity.class.getDeclaredField("action");
        Field resourceType = AuditLogEntity.class.getDeclaredField("resourceType");
        Field resourceId = AuditLogEntity.class.getDeclaredField("resourceId");
        Field oldValues = AuditLogEntity.class.getDeclaredField("oldValues");
        Field newValues = AuditLogEntity.class.getDeclaredField("newValues");
        Field createdAt = AuditLogEntity.class.getDeclaredField("createdAt");
        Field ipAddress = AuditLogEntity.class.getDeclaredField("ipAddress");

        assertNotNull(tableName);
        assertEquals("audit_logs", tableName.value());
        assertNotNull(id.getAnnotation(TableId.class));
        assertEquals("user_id", userId.getAnnotation(TableField.class).value());
        assertEquals("action", action.getAnnotation(TableField.class).value());
        assertEquals("resource_type", resourceType.getAnnotation(TableField.class).value());
        assertEquals("resource_id", resourceId.getAnnotation(TableField.class).value());
        assertEquals("old_values", oldValues.getAnnotation(TableField.class).value());
        assertEquals("new_values", newValues.getAnnotation(TableField.class).value());
        assertEquals("created_at", createdAt.getAnnotation(TableField.class).value());
        assertEquals("ip_address", ipAddress.getAnnotation(TableField.class).value());
    }
}
