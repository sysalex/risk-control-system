package com.harness.risk.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateRuleRequest;
import com.harness.risk.application.dto.RuleResponse;
import com.harness.risk.application.dto.UpdateRuleRequest;

/**
 * 风控规则 Application Service
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public interface RiskRuleService {

    /**
     * 创建规则
     *
     * @param request   创建请求
     * @param creatorId 创建者用户 ID
     * @return 规则响应
     */
    RuleResponse create(CreateRuleRequest request, Long creatorId);

    /**
     * 根据 ID 查询规则
     *
     * @param id 规则 ID
     * @return 规则响应
     */
    RuleResponse getById(Long id);

    /**
     * 分页查询规则列表
     *
     * @param page  页码
     * @param limit 每页条数
     * @return 分页结果
     */
    Page<RuleResponse> list(int page, int limit);

    /**
     * 更新规则
     *
     * @param id      规则 ID
     * @param request 更新请求
     * @return 更新后的规则响应
     */
    RuleResponse update(Long id, UpdateRuleRequest request);

    /**
     * 删除规则
     *
     * @param id 规则 ID
     */
    void delete(Long id);

    /**
     * 启用规则
     *
     * @param id 规则 ID
     * @return 更新后的规则响应
     */
    RuleResponse enableRule(Long id);

    /**
     * 停用规则
     *
     * @param id 规则 ID
     * @return 更新后的规则响应
     */
    RuleResponse disableRule(Long id);
}
