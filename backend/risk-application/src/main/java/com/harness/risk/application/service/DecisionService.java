package com.harness.risk.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateDecisionRequest;
import com.harness.risk.application.dto.DecisionResponse;
import com.harness.risk.application.dto.UpdateDecisionRequest;

/**
 * 决策记录 Application Service
 *
 * @author harness-agent
 * @since 2026-04-28
 */
public interface DecisionService {

    /**
     * 创建决策
     *
     * @param request 创建请求
     * @return 决策响应
     */
    DecisionResponse create(CreateDecisionRequest request);

    /**
     * 根据 ID 查询决策
     *
     * @param id 决策 ID
     * @return 决策响应
     */
    DecisionResponse getById(Long id);

    /**
     * 分页查询决策列表
     *
     * @param page  页码
     * @param limit 每页条数
     * @return 分页结果
     */
    Page<DecisionResponse> list(int page, int limit);

    /**
     * 更新决策
     *
     * @param id      决策 ID
     * @param request 更新请求
     * @return 更新后的决策响应
     */
    DecisionResponse update(Long id, UpdateDecisionRequest request);
}
