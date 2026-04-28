package com.harness.risk.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateEventRequest;
import com.harness.risk.application.dto.EventResponse;
import com.harness.risk.application.dto.UpdateEventRequest;
import com.harness.risk.domain.enums.RiskEventStatusEnums;
import com.harness.risk.domain.enums.RiskLevelEnums;

/**
 * 风险事件 Application Service
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public interface RiskEventService {

    /**
     * 创建事件
     *
     * @param request 创建请求
     * @return 事件响应
     */
    EventResponse create(CreateEventRequest request);

    /**
     * 根据 ID 查询事件
     *
     * @param id 事件 ID
     * @return 事件响应
     */
    EventResponse getById(Long id);

    /**
     * 分页查询事件列表
     *
     * @param page      页码
     * @param limit     每页条数
     * @param riskLevel 风险等级筛选（可选）
     * @param status    状态筛选（可选）
     * @return 分页结果
     */
    Page<EventResponse> list(int page, int limit, RiskLevelEnums riskLevel, RiskEventStatusEnums status);

    /**
     * 更新事件
     *
     * @param id      事件 ID
     * @param request 更新请求
     * @return 更新后的事件响应
     */
    EventResponse update(Long id, UpdateEventRequest request);

    /**
     * 解决事件
     *
     * @param id         事件 ID
     * @param resolvedBy 解决人用户 ID
     * @return 更新后的事件响应
     */
    EventResponse resolveEvent(Long id, Long resolvedBy);
}
