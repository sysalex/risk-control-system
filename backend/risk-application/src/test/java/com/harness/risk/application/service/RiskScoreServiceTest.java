package com.harness.risk.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateScoreRequest;
import com.harness.risk.application.dto.ScoreResponse;
import com.harness.risk.application.service.impl.RiskScoreServiceImpl;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.model.entity.RiskScoreEntity;
import com.harness.risk.infrastructure.mapper.RiskScoreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link RiskScoreServiceImpl} 单元测试
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@ExtendWith(MockitoExtension.class)
class RiskScoreServiceTest {

    @Mock
    private RiskScoreMapper riskScoreMapper;

    private RiskScoreServiceImpl riskScoreService;

    @BeforeEach
    void setUp() {
        riskScoreService = new RiskScoreServiceImpl();
        ReflectionTestUtils.setField(riskScoreService, "baseMapper", riskScoreMapper);
    }

    private RiskScoreEntity sampleScore() {
        RiskScoreEntity score = new RiskScoreEntity();
        score.setId(1L);
        score.setEventId(10L);
        score.setSubjectType("user");
        score.setSubjectId("user-123");
        score.setScore(new BigDecimal("86.50"));
        score.setMaxScore(new BigDecimal("100.00"));
        score.setDimensions("{\"behavior\":40}");
        score.setEvaluatorId(2L);
        return score;
    }

    @Test
    void createSuccessReturnsScoreResponse() {
        when(riskScoreMapper.selectCount(any())).thenReturn(0L);
        when(riskScoreMapper.insert(any())).thenReturn(1);

        ScoreResponse resp = riskScoreService.create(new CreateScoreRequest(
                10L, "user", "user-123", new BigDecimal("86.50"), new BigDecimal("100.00"),
                "{\"behavior\":40}", 2L));

        assertEquals(10L, resp.getEventId());
        assertEquals(new BigDecimal("86.50"), resp.getScore());
        verify(riskScoreMapper).insert(any(RiskScoreEntity.class));
    }

    @Test
    void createFailsWhenEventAlreadyHasScore() {
        when(riskScoreMapper.selectCount(any())).thenReturn(1L);

        AppException e = assertThrows(AppException.class, () -> riskScoreService.create(new CreateScoreRequest(
                10L, "user", "user-123", new BigDecimal("86.50"), null, "{}", 2L)));

        assertEquals(409, e.getCode());
    }

    @Test
    void getByIdReturnsScoreResponse() {
        when(riskScoreMapper.selectById(1L)).thenReturn(sampleScore());

        ScoreResponse resp = riskScoreService.getById(1L);

        assertEquals("user-123", resp.getSubjectId());
        assertEquals(new BigDecimal("86.50"), resp.getScore());
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(riskScoreMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class, () -> riskScoreService.getById(1L));
        assertEquals(404, e.getCode());
    }

    @Test
    void listReturnsPagedResults() {
        Page<RiskScoreEntity> page = new Page<>(1, 20);
        page.setRecords(List.of(sampleScore()));
        page.setTotal(1);
        when(riskScoreMapper.selectPage(any(), any())).thenReturn(page);

        Page<ScoreResponse> result = riskScoreService.list(1, 20);

        assertEquals(1, result.getTotal());
        assertEquals("user-123", result.getRecords().get(0).getSubjectId());
    }

    @Test
    void getLatestBySubjectReturnsNewestScore() {
        when(riskScoreMapper.selectOne(any())).thenReturn(sampleScore());

        ScoreResponse resp = riskScoreService.getLatestBySubject("user", "user-123");

        assertEquals(10L, resp.getEventId());
        assertEquals("user", resp.getSubjectType());
    }

    @Test
    void getLatestBySubjectThrowsWhenNotFound() {
        when(riskScoreMapper.selectOne(any())).thenReturn(null);

        AppException e = assertThrows(AppException.class,
                () -> riskScoreService.getLatestBySubject("user", "missing"));
        assertEquals(404, e.getCode());
    }
}
