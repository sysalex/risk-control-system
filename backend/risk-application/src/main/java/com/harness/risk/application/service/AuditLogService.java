package com.harness.risk.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.AuditLogResponse;
import com.harness.risk.application.dto.CreateAuditLogRequest;

/**
 * 审计日志服务。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
public interface AuditLogService {

    /**
     * 记录审计日志。
     *
     * @param request 审计日志创建请求
     * @return 审计日志响应
     */
    AuditLogResponse record(CreateAuditLogRequest request);

    /**
     * 查询审计日志详情。
     *
     * @param id 审计日志 ID
     * @return 审计日志响应
     */
    AuditLogResponse getById(Long id);

    /**
     * 分页查询审计日志。
     *
     * @param page         页码
     * @param limit        每页条数
     * @param userId       操作用户 ID
     * @param action       操作类型
     * @param resourceType 资源类型
     * @return 分页审计日志
     */
    Page<AuditLogResponse> list(int page, int limit, Long userId, String action, String resourceType);
}
