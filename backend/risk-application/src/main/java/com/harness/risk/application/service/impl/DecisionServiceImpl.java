package com.harness.risk.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.harness.risk.application.dto.CreateDecisionRequest;
import com.harness.risk.application.dto.DecisionResponse;
import com.harness.risk.application.dto.UpdateDecisionRequest;
import com.harness.risk.application.service.DecisionService;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.decision.Decision;
import com.harness.risk.infrastructure.mapper.DecisionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 决策记录服务实现
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@Service
public class DecisionServiceImpl extends ServiceImpl<DecisionMapper, Decision> implements DecisionService {

    @Override
    @Transactional
    public DecisionResponse create(CreateDecisionRequest request) {
        long count = getBaseMapper().selectCount(
                new LambdaQueryWrapper<Decision>().eq(Decision::getEventId, request.eventId()));
        if (count > 0) {
            throw AppException.conflict("事件已存在决策");
        }

        Decision decision = new Decision();
        decision.setEventId(request.eventId());
        decision.setDecisionType(request.decisionType());
        decision.setReason(request.reason());
        decision.setNotes(request.notes());
        decision.setDecidedBy(request.decidedBy());
        decision.setDecidedAt(LocalDateTime.now());
        decision.setCreatedAt(LocalDateTime.now());
        decision.setUpdatedAt(LocalDateTime.now());
        save(decision);

        return toResponse(decision);
    }

    @Override
    public DecisionResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public Page<DecisionResponse> list(int page, int limit) {
        Page<Decision> result = page(
                new Page<>(page, limit),
                new LambdaQueryWrapper<Decision>().orderByDesc(Decision::getDecidedAt));
        List<DecisionResponse> records = result.getRecords().stream()
                .map(this::toResponse)
                .toList();
        Page<DecisionResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }

    @Override
    @Transactional
    public DecisionResponse update(Long id, UpdateDecisionRequest request) {
        Decision decision = findOrThrow(id);
        if (request.decisionType() != null) {
            decision.setDecisionType(request.decisionType());
        }
        if (request.reason() != null) {
            decision.setReason(request.reason());
        }
        if (request.notes() != null) {
            decision.setNotes(request.notes());
        }
        decision.setUpdatedAt(LocalDateTime.now());
        updateById(decision);

        return toResponse(decision);
    }

    private Decision findOrThrow(Long id) {
        Decision decision = super.getById(id);
        if (decision == null) {
            throw AppException.notFound("Decision");
        }
        return decision;
    }

    private DecisionResponse toResponse(Decision decision) {
        return new DecisionResponse(
                decision.getId(),
                decision.getEventId(),
                decision.getDecisionType(),
                decision.getReason(),
                decision.getNotes(),
                decision.getDecidedBy(),
                decision.getDecidedAt(),
                decision.getCreatedAt(),
                decision.getUpdatedAt()
        );
    }
}
