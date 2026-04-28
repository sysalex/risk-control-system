package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateScoreRequest;
import com.harness.risk.application.dto.ScoreResponse;
import com.harness.risk.application.service.RiskScoreService;
import com.harness.risk.common.annotation.RequireRole;
import com.harness.risk.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 风险评分接口。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@RestController
@RequestMapping("/api/v1/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final RiskScoreService riskScoreService;

    /**
     * 评分列表。
     *
     * @param page  页码
     * @param limit 每页数量
     * @return 分页评分列表
     */
    @GetMapping
    public ApiResponse<Page<ScoreResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.ok(riskScoreService.list(page, limit));
    }

    /**
     * 创建评分结果。
     *
     * @param request 创建评分请求
     * @return 评分响应
     */
    @PostMapping("/evaluate")
    @RequireRole("admin")
    public ApiResponse<ScoreResponse> create(@Valid @RequestBody CreateScoreRequest request) {
        return ApiResponse.ok(riskScoreService.create(request));
    }

    /**
     * 评分详情。
     *
     * @param id 评分 ID
     * @return 评分响应
     */
    @GetMapping("/{id}")
    public ApiResponse<ScoreResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(riskScoreService.getById(id));
    }

    /**
     * 查询主体最新评分。
     *
     * @param subjectType 主体类型
     * @param subjectId   主体标识
     * @return 最新评分响应
     */
    @GetMapping("/subject/{type}/{id}")
    public ApiResponse<ScoreResponse> getLatestBySubject(
            @PathVariable("type") String subjectType,
            @PathVariable("id") String subjectId) {
        return ApiResponse.ok(riskScoreService.getLatestBySubject(subjectType, subjectId));
    }
}
