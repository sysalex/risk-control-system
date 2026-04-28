package com.harness.risk.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateScoreRequest;
import com.harness.risk.application.dto.ScoreResponse;

/**
 * 风险评分 Application Service
 *
 * @author harness-agent
 * @since 2026-04-28
 */
public interface RiskScoreService {

    /**
     * 创建评分
     *
     * @param request 创建请求
     * @return 评分响应
     */
    ScoreResponse create(CreateScoreRequest request);

    /**
     * 根据 ID 查询评分
     *
     * @param id 评分 ID
     * @return 评分响应
     */
    ScoreResponse getById(Long id);

    /**
     * 分页查询评分列表
     *
     * @param page  页码
     * @param limit 每页条数
     * @return 分页结果
     */
    Page<ScoreResponse> list(int page, int limit);

    /**
     * 查询主体最新评分
     *
     * @param subjectType 主体类型
     * @param subjectId   主体 ID
     * @return 最新评分响应
     */
    ScoreResponse getLatestBySubject(String subjectType, String subjectId);
}
