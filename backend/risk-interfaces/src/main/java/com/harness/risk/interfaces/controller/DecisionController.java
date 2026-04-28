package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateDecisionRequest;
import com.harness.risk.application.dto.DecisionResponse;
import com.harness.risk.application.dto.UpdateDecisionRequest;
import com.harness.risk.application.service.DecisionService;
import com.harness.risk.common.annotation.RequireRole;
import com.harness.risk.common.response.ApiResponse;
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
 * 风控处置决策接口。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@RestController
@RequestMapping("/api/v1/decisions")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionService decisionService;

    /**
     * 决策列表。
     *
     * @param page  页码
     * @param limit 每页数量
     * @return 分页决策列表
     */
    @GetMapping
    public ApiResponse<Page<DecisionResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.ok(decisionService.list(page, limit));
    }

    /**
     * 创建处置决策。
     *
     * @param request 创建决策请求
     * @return 决策响应
     */
    @PostMapping
    @RequireRole("admin")
    public ApiResponse<DecisionResponse> create(@Valid @RequestBody CreateDecisionRequest request) {
        return ApiResponse.ok(decisionService.create(request));
    }

    /**
     * 决策详情。
     *
     * @param id 决策 ID
     * @return 决策响应
     */
    @GetMapping("/{id}")
    public ApiResponse<DecisionResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(decisionService.getById(id));
    }

    /**
     * 更新处置决策。
     *
     * @param id      决策 ID
     * @param request 更新决策请求
     * @return 更新后的决策响应
     */
    @PutMapping("/{id}")
    @RequireRole("admin")
    public ApiResponse<DecisionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDecisionRequest request) {
        return ApiResponse.ok(decisionService.update(id, request));
    }
}
