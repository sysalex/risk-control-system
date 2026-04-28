package com.harness.risk.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateRuleRequest;
import com.harness.risk.application.dto.UpdateRuleRequest;
import com.harness.risk.application.dto.RuleResponse;
import com.harness.risk.application.service.impl.RiskRuleServiceImpl;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.domain.rule.RiskRule;
import com.harness.risk.infrastructure.mapper.RiskRuleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link RiskRuleServiceImpl} 单元测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@ExtendWith(MockitoExtension.class)
class RiskRuleServiceTest {

    @Mock
    private RiskRuleMapper riskRuleMapper;

    private RiskRuleServiceImpl riskRuleService;

    @BeforeEach
    void setUp() {
        riskRuleService = new RiskRuleServiceImpl();
        ReflectionTestUtils.setField(riskRuleService, "baseMapper", riskRuleMapper);
    }

    private RiskRule sampleRule() {
        RiskRule rule = new RiskRule();
        rule.setId(1L);
        rule.setName("大额交易检测");
        rule.setDescription("检测单笔超过 1 万的交易");
        rule.setConditions("{\"operator\":\"AND\",\"conditions\":[]}");
        rule.setActions("{\"type\":\"alert\"}");
        rule.setPriority(10);
        rule.setEnabled(true);
        rule.setCreatorId(2L);
        return rule;
    }

    @Test
    void createSuccessReturnsRuleResponse() {
        when(riskRuleMapper.selectCount(any())).thenReturn(0L);

        RuleResponse resp = riskRuleService.create(
                new CreateRuleRequest("大额交易检测", "描述", "{}", "{}", 10), 2L);

        assertEquals("大额交易检测", resp.name());
        assertEquals(10, resp.priority());
        verify(riskRuleMapper).insert(any(RiskRule.class));
    }

    @Test
    void createFailsWhenNameExists() {
        when(riskRuleMapper.selectCount(any())).thenReturn(1L);

        AppException e = assertThrows(AppException.class,
                () -> riskRuleService.create(new CreateRuleRequest("大额交易检测", null, null, null, 10), 2L));
        assertEquals(409, e.getCode());
    }

    @Test
    void getByIdReturnsRuleResponse() {
        when(riskRuleMapper.selectById(1L)).thenReturn(sampleRule());

        RuleResponse resp = riskRuleService.getById(1L);

        assertEquals("大额交易检测", resp.name());
        assertTrue(resp.enabled());
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(riskRuleMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class, () -> riskRuleService.getById(1L));
        assertEquals(404, e.getCode());
    }

    @Test
    void listReturnsPagedResults() {
        Page<RiskRule> page = new Page<>(1, 20);
        page.setRecords(List.of(sampleRule()));
        page.setTotal(1);
        when(riskRuleMapper.selectPage(any(), any())).thenReturn(page);

        Page<RuleResponse> result = riskRuleService.list(1, 20);

        assertEquals(1, result.getTotal());
        assertEquals("大额交易检测", result.getRecords().get(0).name());
    }

    @Test
    void updateSuccessReturnsUpdatedRule() {
        RiskRule rule = sampleRule();
        when(riskRuleMapper.selectById(1L)).thenReturn(rule);
        when(riskRuleMapper.selectCount(any())).thenReturn(0L);

        RuleResponse resp = riskRuleService.update(1L,
                new UpdateRuleRequest("新名称", "新描述", null, null, 5));

        assertEquals("新名称", resp.name());
        assertEquals("新描述", resp.description());
        assertEquals(5, resp.priority());
        verify(riskRuleMapper).updateById(any(RiskRule.class));
    }

    @Test
    void updateFailsWhenRuleNotFound() {
        when(riskRuleMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class,
                () -> riskRuleService.update(1L, new UpdateRuleRequest("新名称", null, null, null, null)));
        assertEquals(404, e.getCode());
    }

    @Test
    void updateFailsWhenNameConflict() {
        RiskRule rule = sampleRule();
        when(riskRuleMapper.selectById(1L)).thenReturn(rule);
        when(riskRuleMapper.selectCount(any())).thenReturn(1L);

        AppException e = assertThrows(AppException.class,
                () -> riskRuleService.update(1L, new UpdateRuleRequest("冲突名称", null, null, null, null)));
        assertEquals(409, e.getCode());
    }

    @Test
    void deleteSuccess() {
        when(riskRuleMapper.selectById(1L)).thenReturn(sampleRule());

        riskRuleService.delete(1L);

        verify(riskRuleMapper).deleteById(1L);
    }

    @Test
    void deleteFailsWhenNotFound() {
        when(riskRuleMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class, () -> riskRuleService.delete(1L));
        assertEquals(404, e.getCode());
    }

    @Test
    void enableRuleSetsEnabledTrue() {
        RiskRule rule = sampleRule();
        rule.setEnabled(false);
        when(riskRuleMapper.selectById(1L)).thenReturn(rule);

        RuleResponse resp = riskRuleService.enableRule(1L);

        assertTrue(resp.enabled());
        verify(riskRuleMapper).updateById(argThat(r -> r.isEnabled()));
    }

    @Test
    void disableRuleSetsEnabledFalse() {
        RiskRule rule = sampleRule();
        when(riskRuleMapper.selectById(1L)).thenReturn(rule);

        RuleResponse resp = riskRuleService.disableRule(1L);

        assertFalse(resp.enabled());
        verify(riskRuleMapper).updateById(argThat(r -> !r.isEnabled()));
    }

    @Test
    void enableRuleThrowsWhenNotFound() {
        when(riskRuleMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class, () -> riskRuleService.enableRule(1L));
        assertEquals(404, e.getCode());
    }
}
