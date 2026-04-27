package com.harness.risk.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.harness.risk.domain.audit.AuditLog;

/**
 * 审计日志 Mapper
 * <p>
 * 仅提供 MyBatis-Plus 基础数据访问能力，不承载审计业务逻辑。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
