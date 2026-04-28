package com.harness.risk.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harness.risk.domain.model.entity.RiskEventEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link RiskEventMapper} 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class RiskEventMapperTest {

    /**
     * 验证 Mapper 继承 MyBatis-Plus BaseMapper
     */
    @Test
    void extendsBaseMapperForRiskEventEntity() {
        Type genericInterface = RiskEventMapper.class.getGenericInterfaces()[0];

        assertTrue(BaseMapper.class.isAssignableFrom(RiskEventMapper.class));
        assertInstanceOf(ParameterizedType.class, genericInterface);
        assertEquals(RiskEventEntity.class, ((ParameterizedType) genericInterface).getActualTypeArguments()[0]);
    }
}
