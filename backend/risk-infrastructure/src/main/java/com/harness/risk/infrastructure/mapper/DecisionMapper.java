package com.harness.risk.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harness.risk.domain.decision.Decision;

/**
 * 决策记录 Mapper
 * <p>
 * 仅提供 MyBatis-Plus 基础数据访问能力，不承载决策业务逻辑。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public interface DecisionMapper extends BaseMapper<Decision> {
}
