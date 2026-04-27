package com.harness.risk.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harness.risk.domain.event.RiskEvent;

/**
 * 风险事件 Mapper
 * <p>
 * 仅提供 MyBatis-Plus 基础数据访问能力，不承载事件处理业务逻辑。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public interface RiskEventMapper extends BaseMapper<RiskEvent> {
}
