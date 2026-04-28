package com.harness.risk.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harness.risk.domain.model.entity.DecisionEntity;
import com.harness.risk.domain.model.entity.RiskScoreEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 风险评分和决策 Mapper 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class ScoreDecisionMapperTest {

    /**
     * 验证 RiskScoreMapper 继承 MyBatis-Plus BaseMapper
     */
    @Test
    void riskScoreMapperExtendsBaseMapperForRiskScore() {
        assertExtendsBaseMapper(RiskScoreMapper.class, RiskScoreEntity.class);
    }

    /**
     * 验证 DecisionMapper 继承 MyBatis-Plus BaseMapper
     */
    @Test
    void decisionMapperExtendsBaseMapperForDecision() {
        assertExtendsBaseMapper(DecisionMapper.class, DecisionEntity.class);
    }

    private void assertExtendsBaseMapper(Class<?> mapperType, Class<?> entityType) {
        ParameterizedType genericInterface = (ParameterizedType) mapperType.getGenericInterfaces()[0];

        assertTrue(BaseMapper.class.isAssignableFrom(mapperType));
        assertEquals(entityType, genericInterface.getActualTypeArguments()[0]);
    }
}
