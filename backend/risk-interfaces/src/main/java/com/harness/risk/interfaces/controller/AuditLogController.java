package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.AuditLogResponse;
import com.harness.risk.application.service.AuditLogService;
import com.harness.risk.common.annotation.RequireRole;
import com.harness.risk.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审计日志查询接口。
 * <p>
 * 审计日志包含敏感操作轨迹，仅管理员可查询。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * 审计日志列表（分页）。
     *
     * @param page         页码
     * @param limit        每页条数
     * @param userId       操作用户 ID
     * @param action       操作类型
     * @param resourceType 资源类型
     * @return 分页审计日志
     */
    @GetMapping
    @RequireRole("admin")
    public ApiResponse<Page<AuditLogResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String resourceType) {
        return ApiResponse.ok(auditLogService.list(page, limit, userId, action, resourceType));
    }

    /**
     * 审计日志详情。
     *
     * @param id 审计日志 ID
     * @return 审计日志响应
     */
    @GetMapping("/{id}")
    @RequireRole("admin")
    public ApiResponse<AuditLogResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(auditLogService.getById(id));
    }
}
