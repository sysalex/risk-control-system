package com.harness.risk.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.harness.risk.application.dto.CreateRuleRequest;
import com.harness.risk.application.dto.RuleResponse;
import com.harness.risk.application.dto.UpdateRuleRequest;
import com.harness.risk.application.service.RiskRuleService;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.rule.RiskRule;
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
public class RiskRuleServiceImpl extends ServiceImpl<RiskRuleMapper, RiskRule> implements RiskRuleService {

    @Override
    @Transactional
    public RuleResponse create(CreateRuleRequest request, Long creatorId) {
        long count = getBaseMapper().selectCount(
                new LambdaQueryWrapper<RiskRule>().eq(RiskRule::getName, request.name()));
        if (count > 0) {
            throw AppException.conflict("规则名称已存在");
        }

        RiskRule rule = new RiskRule();
        rule.setName(request.name());
        rule.setDescription(request.description());
        rule.setConditions(request.conditions());
        rule.setActions(request.actions());
        rule.setPriority(request.priority());
        rule.setEnabled(true);
        rule.setCreatorId(creatorId);
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        save(rule);

        return toResponse(rule);
    }

    @Override
    public RuleResponse getById(Long id) {
        RiskRule rule = findOrThrow(id);
        return toResponse(rule);
    }

    @Override
    public Page<RuleResponse> list(int page, int limit) {
        Page<RiskRule> result = page(
                new Page<>(page, limit),
                new LambdaQueryWrapper<RiskRule>()
                        .orderByAsc(RiskRule::getPriority)
                        .orderByDesc(RiskRule::getCreatedAt));
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
        RiskRule rule = findOrThrow(id);

        if (request.name() != null && !request.name().equals(rule.getName())) {
            long count = getBaseMapper().selectCount(
                    new LambdaQueryWrapper<RiskRule>().eq(RiskRule::getName, request.name()));
            if (count > 0) {
                throw AppException.conflict("规则名称已存在");
            }
            rule.setName(request.name());
        }
        if (request.description() != null) {
            rule.setDescription(request.description());
        }
        if (request.conditions() != null) {
            rule.setConditions(request.conditions());
        }
        if (request.actions() != null) {
            rule.setActions(request.actions());
        }
        if (request.priority() != null) {
            rule.setPriority(request.priority());
        }
        rule.setUpdatedAt(LocalDateTime.now());
        updateById(rule);

        return toResponse(rule);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        getBaseMapper().deleteById(id);
    }

    @Override
    @Transactional
    public RuleResponse enableRule(Long id) {
        RiskRule rule = findOrThrow(id);
        rule.setEnabled(true);
        rule.setUpdatedAt(LocalDateTime.now());
        updateById(rule);
        return toResponse(rule);
    }

    @Override
    @Transactional
    public RuleResponse disableRule(Long id) {
        RiskRule rule = findOrThrow(id);
        rule.setEnabled(false);
        rule.setUpdatedAt(LocalDateTime.now());
        updateById(rule);
        return toResponse(rule);
    }

    private RiskRule findOrThrow(Long id) {
        RiskRule rule = super.getById(id);
        if (rule == null) {
            throw AppException.notFound("Rule");
        }
        return rule;
    }

    private RuleResponse toResponse(RiskRule rule) {
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
