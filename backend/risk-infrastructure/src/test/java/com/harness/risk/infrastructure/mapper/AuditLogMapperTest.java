package com.harness.risk.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harness.risk.domain.audit.AuditLog;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AuditLogMapper} 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class AuditLogMapperTest {

    /**
     * 验证 Mapper 继承 MyBatis-Plus BaseMapper
     */
    @Test
    void extendsBaseMapperForAuditLogEntity() {
        Type genericInterface = AuditLogMapper.class.getGenericInterfaces()[0];

        assertTrue(BaseMapper.class.isAssignableFrom(AuditLogMapper.class));
        assertInstanceOf(ParameterizedType.class, genericInterface);
        assertEquals(AuditLog.class, ((ParameterizedType) genericInterface).getActualTypeArguments()[0]);
    }
}
