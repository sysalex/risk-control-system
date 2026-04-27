package com.harness.risk.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link User} 模型测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class UserTest {

    /**
     * 验证用户模型映射到 users 表
     *
     * @throws NoSuchFieldException 字段不存在时测试失败
     */
    @Test
    void mapsUserToUsersTable() throws NoSuchFieldException {
        TableName tableName = User.class.getAnnotation(TableName.class);
        Field id = User.class.getDeclaredField("id");
        Field username = User.class.getDeclaredField("username");
        Field hashedPassword = User.class.getDeclaredField("hashedPassword");
        Field active = User.class.getDeclaredField("active");

        assertNotNull(tableName);
        assertEquals("users", tableName.value());
        assertNotNull(id.getAnnotation(TableId.class));
        assertEquals("username", username.getAnnotation(TableField.class).value());
        assertEquals("hashed_password", hashedPassword.getAnnotation(TableField.class).value());
        assertEquals("is_active", active.getAnnotation(TableField.class).value());
    }

    /**
     * 验证用户默认状态适合新建账号
     */
    @Test
    void defaultsToActiveOperator() {
        User user = new User();

        assertTrue(user.isActive());
        assertEquals(UserRole.OPERATOR, user.getRole());
    }

    /**
     * 验证角色枚举值与数据库存储值一致
     */
    @Test
    void roleValuesMatchDatabaseValues() {
        assertEquals("admin", UserRole.ADMIN.getValue());
        assertEquals("risk_analyst", UserRole.RISK_ANALYST.getValue());
        assertEquals("operator", UserRole.OPERATOR.getValue());
    }
}
