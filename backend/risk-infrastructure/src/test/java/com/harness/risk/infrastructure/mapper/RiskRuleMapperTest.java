package com.harness.risk.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harness.risk.domain.rule.RiskRule;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link RiskRuleMapper} 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class RiskRuleMapperTest {

    /**
     * 验证 Mapper 继承 MyBatis-Plus BaseMapper
     */
    @Test
    void extendsBaseMapperForRiskRuleEntity() {
        Type genericInterface = RiskRuleMapper.class.getGenericInterfaces()[0];

        assertTrue(BaseMapper.class.isAssignableFrom(RiskRuleMapper.class));
        assertInstanceOf(ParameterizedType.class, genericInterface);
        assertEquals(RiskRule.class, ((ParameterizedType) genericInterface).getActualTypeArguments()[0]);
    }
}
