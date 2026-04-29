package com.harness.risk.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.harness.risk.application.dto.CreateRuleRequest;
import com.harness.risk.application.dto.RuleResponse;
import com.harness.risk.application.dto.UpdateRuleRequest;
import com.harness.risk.application.service.RiskRuleService;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.model.entity.RiskEventEntity;
import com.harness.risk.domain.model.entity.RiskRuleEntity;
import com.harness.risk.infrastructure.mapper.RiskEventMapper;
import com.harness.risk.infrastructure.mapper.RiskRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 风控规则服务实现
 * <p>
 * 继承 {@link ServiceImpl} 获得 MyBatis-Plus 基础 CRUD，
 * 自定义方法覆盖默认行为或补充业务规则。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Service
public class RiskRuleServiceImpl extends ServiceImpl<RiskRuleMapper, RiskRuleEntity> implements RiskRuleService {

    private final RiskEventMapper riskEventMapper;

    public RiskRuleServiceImpl(RiskEventMapper riskEventMapper) {
        this.riskEventMapper = riskEventMapper;
    }

    @Override
    @Transactional
    public RuleResponse create(CreateRuleRequest request, Long creatorId) {
        long count = getBaseMapper().selectCount(
                new LambdaQueryWrapper<RiskRuleEntity>().eq(RiskRuleEntity::getName, request.getName()));
        if (count > 0) {
            throw AppException.conflict("规则名称已存在");
        }

        RiskRuleEntity rule = new RiskRuleEntity();
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setConditions(request.getConditions());
        rule.setActions(request.getActions());
        rule.setPriority(request.getPriority());
        rule.setEnabled(true);
        rule.setCreatorId(creatorId);
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        save(rule);

        return toResponse(rule);
    }

    @Override
    public RuleResponse getById(Long id) {
        RiskRuleEntity rule = findOrThrow(id);
        return toResponse(rule);
    }

    @Override
    public Page<RuleResponse> list(int page, int limit) {
        Page<RiskRuleEntity> result = page(
                new Page<>(page, limit),
                new LambdaQueryWrapper<RiskRuleEntity>()
                        .orderByAsc(RiskRuleEntity::getPriority)
                        .orderByDesc(RiskRuleEntity::getCreatedAt));
        List<RuleResponse> records = result.getRecords().stream()
                .map(this::toResponse)
                .toList();
        Page<RuleResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }

    @Override
    @Transactional
    public RuleResponse update(Long id, UpdateRuleRequest request) {
        RiskRuleEntity rule = findOrThrow(id);

        if (request.getName() != null && !request.getName().equals(rule.getName())) {
            long count = getBaseMapper().selectCount(
                    new LambdaQueryWrapper<RiskRuleEntity>().eq(RiskRuleEntity::getName, request.getName()));
            if (count > 0) {
                throw AppException.conflict("规则名称已存在");
            }
            rule.setName(request.getName());
        }
        if (request.getDescription() != null) {
            rule.setDescription(request.getDescription());
        }
        if (request.getConditions() != null) {
            rule.setConditions(request.getConditions());
        }
        if (request.getActions() != null) {
            rule.setActions(request.getActions());
        }
        if (request.getPriority() != null) {
            rule.setPriority(request.getPriority());
        }
        rule.setUpdatedAt(LocalDateTime.now());
        updateById(rule);

        return toResponse(rule);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        long eventCount = riskEventMapper.selectCount(
                new LambdaQueryWrapper<RiskEventEntity>().eq(RiskEventEntity::getRuleId, id));
        if (eventCount > 0) {
            throw AppException.conflict("规则已被风险事件引用，无法删除");
        }
        getBaseMapper().deleteById(id);
    }

    @Override
    @Transactional
    public RuleResponse enableRule(Long id) {
        RiskRuleEntity rule = findOrThrow(id);
        rule.setEnabled(true);
        rule.setUpdatedAt(LocalDateTime.now());
        updateById(rule);
        return toResponse(rule);
    }

    @Override
    @Transactional
    public RuleResponse disableRule(Long id) {
        RiskRuleEntity rule = findOrThrow(id);
        rule.setEnabled(false);
        rule.setUpdatedAt(LocalDateTime.now());
        updateById(rule);
        return toResponse(rule);
    }

    private RiskRuleEntity findOrThrow(Long id) {
        RiskRuleEntity rule = super.getById(id);
        if (rule == null) {
            throw AppException.notFound("Rule");
        }
        return rule;
    }

    private RuleResponse toResponse(RiskRuleEntity rule) {
        return new RuleResponse(
                rule.getId(),
                rule.getName(),
                rule.getDescription(),
                rule.getConditions(),
                rule.getActions(),
                rule.getPriority(),
                rule.isEnabled(),
                rule.getCreatorId(),
                rule.getCreatedAt(),
                rule.getUpdatedAt()
        );
    }
}
