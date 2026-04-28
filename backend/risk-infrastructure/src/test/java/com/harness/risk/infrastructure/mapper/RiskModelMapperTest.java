package com.harness.risk.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harness.risk.domain.model.entity.RiskEventEntity;
import com.harness.risk.domain.model.entity.RiskRuleEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 风控模型 Mapper 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class RiskModelMapperTest {

    /**
     * 验证 RiskRuleMapper 继承 MyBatis-Plus BaseMapper
     */
    @Test
    void riskRuleMapperExtendsBaseMapperForRiskRule() {
        assertExtendsBaseMapper(RiskRuleMapper.class, RiskRuleEntity.class);
    }

    /**
     * 验证 RiskEventMapper 继承 MyBatis-Plus BaseMapper
     */
    @Test
    void riskEventMapperExtendsBaseMapperForRiskEvent() {
        assertExtendsBaseMapper(RiskEventMapper.class, RiskEventEntity.class);
    }

    private void assertExtendsBaseMapper(Class<?> mapperType, Class<?> entityType) {
        ParameterizedType genericInterface = (ParameterizedType) mapperType.getGenericInterfaces()[0];

        assertTrue(BaseMapper.class.isAssignableFrom(mapperType));
        assertEquals(entityType, genericInterface.getActualTypeArguments()[0]);
    }
}
