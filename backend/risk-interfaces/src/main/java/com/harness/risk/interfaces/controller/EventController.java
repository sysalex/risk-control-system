package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateEventRequest;
import com.harness.risk.application.dto.EventResponse;
import com.harness.risk.application.dto.UpdateEventRequest;
import com.harness.risk.application.service.RiskEventService;
import com.harness.risk.common.annotation.AuditOperation;
import com.harness.risk.common.annotation.RequireRole;
import com.harness.risk.common.response.ApiResponse;
import com.harness.risk.common.security.AuthConstants;
import com.harness.risk.domain.enums.RiskEventStatusEnums;
import com.harness.risk.domain.enums.RiskLevelEnums;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 风险事件管理接口
 * <p>
 * 提供风险事件的查询、创建、状态更新和解决功能。
 * 列表/详情对所有认证用户开放；写操作仅限 ADMIN。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final RiskEventService riskEventService;

    /**
     * 事件列表（分页，支持筛选）
     *
     * @param page      页码
     * @param limit     每页条数
     * @param riskLevel 风险等级筛选（可选）
     * @param status    状态筛选（可选）
     * @return 分页事件列表
     */
    @GetMapping
    public ApiResponse<Page<EventResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) RiskLevelEnums riskLevel,
            @RequestParam(required = false) RiskEventStatusEnums status) {
        return ApiResponse.ok(riskEventService.list(page, limit, riskLevel, status));
    }

    /**
     * 事件详情
     *
     * @param id 事件 ID
     * @return 事件响应
     */
    @GetMapping("/{id}")
    public ApiResponse<EventResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(riskEventService.getById(id));
    }

    /**
     * 创建事件（管理员）
     *
     * @param request 创建请求
     * @return 事件响应
     */
    @PostMapping
    @RequireRole("admin")
    @AuditOperation(action = "create", resourceType = "event")
    public ApiResponse<EventResponse> create(@Valid @RequestBody CreateEventRequest request) {
        return ApiResponse.ok(riskEventService.create(request));
    }

    /**
     * 更新事件（管理员）
     *
     * @param id      事件 ID
     * @param request 更新请求
     * @return 更新后的事件响应
     */
    @PutMapping("/{id}")
    @RequireRole("admin")
    @AuditOperation(action = "update", resourceType = "event")
    public ApiResponse<EventResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request) {
        return ApiResponse.ok(riskEventService.update(id, request));
    }

    /**
     * 解决事件（管理员）
     *
     * @param id          事件 ID
     * @param httpRequest HTTP 请求（提取解决人 userId）
     * @return 更新后的事件响应
     */
    @PostMapping("/{id}/resolve")
    @RequireRole("admin")
    @AuditOperation(action = "resolve", resourceType = "event")
    public ApiResponse<EventResponse> resolve(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long resolvedBy = (Long) httpRequest.getAttribute(AuthConstants.ATTR_USER_ID);
        return ApiResponse.ok(riskEventService.resolveEvent(id, resolvedBy));
    }
}
