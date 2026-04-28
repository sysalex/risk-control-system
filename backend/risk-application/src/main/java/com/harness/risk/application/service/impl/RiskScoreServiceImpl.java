package com.harness.risk.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.harness.risk.application.dto.CreateScoreRequest;
import com.harness.risk.application.dto.ScoreResponse;
import com.harness.risk.application.service.RiskScoreService;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.model.entity.RiskScoreEntity;
import com.harness.risk.infrastructure.mapper.RiskScoreMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 风险评分服务实现
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@Service
public class RiskScoreServiceImpl extends ServiceImpl<RiskScoreMapper, RiskScoreEntity> implements RiskScoreService {

    @Override
    @Transactional
    public ScoreResponse create(CreateScoreRequest request) {
        long count = getBaseMapper().selectCount(
                new LambdaQueryWrapper<RiskScoreEntity>().eq(RiskScoreEntity::getEventId, request.getEventId()));
        if (count > 0) {
            throw AppException.conflict("事件已存在评分");
        }

        RiskScoreEntity score = new RiskScoreEntity();
        score.setEventId(request.getEventId());
        score.setSubjectType(request.getSubjectType());
        score.setSubjectId(request.getSubjectId());
        score.setScore(request.getScore());
        score.setMaxScore(request.getMaxScore() == null ? new BigDecimal("100.00") : request.getMaxScore());
        score.setDimensions(request.getDimensions());
        score.setEvaluatorId(request.getEvaluatorId());
        score.setEvaluatedAt(LocalDateTime.now());
        score.setCreatedAt(LocalDateTime.now());
        score.setUpdatedAt(LocalDateTime.now());
        save(score);

        return toResponse(score);
    }

    @Override
    public ScoreResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public Page<ScoreResponse> list(int page, int limit) {
        Page<RiskScoreEntity> result = page(
                new Page<>(page, limit),
                new LambdaQueryWrapper<RiskScoreEntity>().orderByDesc(RiskScoreEntity::getEvaluatedAt));
        List<ScoreResponse> records = result.getRecords().stream()
                .map(this::toResponse)
                .toList();
        Page<ScoreResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }

    @Override
    public ScoreResponse getLatestBySubject(String subjectType, String subjectId) {
        RiskScoreEntity score = getBaseMapper().selectOne(
                new LambdaQueryWrapper<RiskScoreEntity>()
                        .eq(RiskScoreEntity::getSubjectType, subjectType)
                        .eq(RiskScoreEntity::getSubjectId, subjectId)
                        .orderByDesc(RiskScoreEntity::getEvaluatedAt)
                        .last("LIMIT 1"));
        if (score == null) {
            throw AppException.notFound("Score");
        }
        return toResponse(score);
    }

    private RiskScoreEntity findOrThrow(Long id) {
        RiskScoreEntity score = super.getById(id);
        if (score == null) {
            throw AppException.notFound("Score");
        }
        return score;
    }

    private ScoreResponse toResponse(RiskScoreEntity score) {
        return new ScoreResponse(
                score.getId(),
                score.getEventId(),
                score.getSubjectType(),
                score.getSubjectId(),
                score.getScore(),
                score.getMaxScore(),
                score.getDimensions(),
                score.getEvaluatedAt(),
                score.getEvaluatorId(),
                score.getCreatedAt(),
                score.getUpdatedAt()
        );
    }
}
