package com.harness.risk.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateDecisionRequest;
import com.harness.risk.application.dto.DecisionResponse;
import com.harness.risk.application.dto.UpdateDecisionRequest;
import com.harness.risk.application.service.impl.DecisionServiceImpl;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.decision.Decision;
import com.harness.risk.domain.decision.DecisionType;
import com.harness.risk.infrastructure.mapper.DecisionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link DecisionServiceImpl} 单元测试
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@ExtendWith(MockitoExtension.class)
class DecisionServiceTest {

    @Mock
    private DecisionMapper decisionMapper;

    private DecisionServiceImpl decisionService;

    @BeforeEach
    void setUp() {
        decisionService = new DecisionServiceImpl();
        ReflectionTestUtils.setField(decisionService, "baseMapper", decisionMapper);
    }

    private Decision sampleDecision() {
        Decision decision = new Decision();
        decision.setId(1L);
        decision.setEventId(10L);
        decision.setDecisionType(DecisionType.MANUAL_REVIEW);
        decision.setReason("风险评分过高");
        decision.setNotes("需要人工复核");
        decision.setDecidedBy(2L);
        return decision;
    }

    @Test
    void createSuccessReturnsDecisionResponse() {
        when(decisionMapper.selectCount(any())).thenReturn(0L);
        when(decisionMapper.insert(any())).thenReturn(1);

        DecisionResponse resp = decisionService.create(new CreateDecisionRequest(
                10L, DecisionType.MANUAL_REVIEW, "风险评分过高", "需要人工复核", 2L));

        assertEquals(10L, resp.eventId());
        assertEquals(DecisionType.MANUAL_REVIEW, resp.decisionType());
        verify(decisionMapper).insert(any(Decision.class));
    }

    @Test
    void createFailsWhenEventAlreadyHasDecision() {
        when(decisionMapper.selectCount(any())).thenReturn(1L);

        AppException e = assertThrows(AppException.class, () -> decisionService.create(
                new CreateDecisionRequest(10L, DecisionType.REJECT, "拒绝", null, 2L)));

        assertEquals(409, e.getCode());
    }

    @Test
    void getByIdReturnsDecisionResponse() {
        when(decisionMapper.selectById(1L)).thenReturn(sampleDecision());

        DecisionResponse resp = decisionService.getById(1L);

        assertEquals("风险评分过高", resp.reason());
        assertEquals(2L, resp.decidedBy());
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(decisionMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class, () -> decisionService.getById(1L));
        assertEquals(404, e.getCode());
    }

    @Test
    void listReturnsPagedResults() {
        Page<Decision> page = new Page<>(1, 20);
        page.setRecords(List.of(sampleDecision()));
        page.setTotal(1);
        when(decisionMapper.selectPage(any(), any())).thenReturn(page);

        Page<DecisionResponse> result = decisionService.list(1, 20);

        assertEquals(1, result.getTotal());
        assertEquals(DecisionType.MANUAL_REVIEW, result.getRecords().get(0).decisionType());
    }

    @Test
    void updateSuccessDoesNotChangeDecidedBy() {
        Decision decision = sampleDecision();
        when(decisionMapper.selectById(1L)).thenReturn(decision);

        DecisionResponse resp = decisionService.update(1L,
                new UpdateDecisionRequest(DecisionType.ESCALATE, "升级处理", "转高级审核"));

        assertEquals(DecisionType.ESCALATE, resp.decisionType());
        assertEquals("升级处理", resp.reason());
        assertEquals(2L, resp.decidedBy());
        verify(decisionMapper).updateById(any(Decision.class));
    }

    @Test
    void updateThrowsWhenNotFound() {
        when(decisionMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class,
                () -> decisionService.update(1L, new UpdateDecisionRequest(DecisionType.APPROVE, "通过", null)));
        assertEquals(404, e.getCode());
    }
}
