package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateRuleRequest;
import com.harness.risk.application.dto.RuleResponse;
import com.harness.risk.application.dto.UpdateRuleRequest;
import com.harness.risk.application.service.RiskRuleService;
import com.harness.risk.common.annotation.RequireRole;
import com.harness.risk.common.response.ApiResponse;
import com.harness.risk.common.security.AuthConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 风控规则管理接口
 * <p>
 * 提供规则 CRUD 和启停管理能力。
 * 列表/详情对所有认证用户开放；写操作仅限 ADMIN。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RiskRuleService riskRuleService;

    /**
     * 规则列表（分页）
     *
     * @param page  页码
     * @param limit 每页条数
     * @return 分页规则列表
     */
    @GetMapping
    public ApiResponse<Page<RuleResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.ok(riskRuleService.list(page, limit));
    }

    /**
     * 规则详情
     *
     * @param id 规则 ID
     * @return 规则响应
     */
    @GetMapping("/{id}")
    public ApiResponse<RuleResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(riskRuleService.getById(id));
    }

    /**
     * 创建规则（管理员）
     *
     * @param request     创建请求
     * @param httpRequest HTTP 请求（提取创建者 userId）
     * @return 规则响应
     */
    @PostMapping
    @RequireRole("admin")
    public ApiResponse<RuleResponse> create(
            @Valid @RequestBody CreateRuleRequest request,
            HttpServletRequest httpRequest) {
        Long creatorId = (Long) httpRequest.getAttribute(AuthConstants.ATTR_USER_ID);
        return ApiResponse.ok(riskRuleService.create(request, creatorId));
    }

    /**
     * 更新规则（管理员）
     *
     * @param id      规则 ID
     * @param request 更新请求
     * @return 更新后的规则响应
     */
    @PutMapping("/{id}")
    @RequireRole("admin")
    public ApiResponse<RuleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRuleRequest request) {
        return ApiResponse.ok(riskRuleService.update(id, request));
    }

    /**
     * 删除规则（管理员）
     *
     * @param id 规则 ID
     * @return 空响应
     */
    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        riskRuleService.delete(id);
        return ApiResponse.ok(null);
    }

    /**
     * 启用规则（管理员）
     *
     * @param id 规则 ID
     * @return 更新后的规则响应
     */
    @PostMapping("/{id}/enable")
    @RequireRole("admin")
    public ApiResponse<RuleResponse> enable(@PathVariable Long id) {
        return ApiResponse.ok(riskRuleService.enableRule(id));
    }

    /**
     * 停用规则（管理员）
     *
     * @param id 规则 ID
     * @return 更新后的规则响应
     */
    @PostMapping("/{id}/disable")
    @RequireRole("admin")
    public ApiResponse<RuleResponse> disable(@PathVariable Long id) {
        return ApiResponse.ok(riskRuleService.disableRule(id));
    }
}
