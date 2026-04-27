package com.harness.risk.domain.event;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 风险等级枚举
 * <p>
 * 枚举值直接对应数据库中的 risk_level 字段。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Getter
@RequiredArgsConstructor
public enum RiskLevel {

    /** 低风险 */
    LOW("low"),
    /** 中风险 */
    MEDIUM("medium"),
    /** 高风险 */
    HIGH("high"),
    /** 严重风险 */
    CRITICAL("critical");

    @EnumValue
    private final String value;
}
