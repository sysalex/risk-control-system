package com.harness.risk.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harness.risk.domain.score.RiskScore;

/**
 * 风险评分 Mapper
 * <p>
 * 仅提供 MyBatis-Plus 基础数据访问能力，不承载评分计算逻辑。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public interface RiskScoreMapper extends BaseMapper<RiskScore> {
}
