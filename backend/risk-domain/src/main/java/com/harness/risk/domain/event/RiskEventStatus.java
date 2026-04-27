package com.harness.risk.domain.event;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 风险事件状态枚举
 * <p>
 * 枚举值直接对应数据库中的 status 字段。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Getter
@RequiredArgsConstructor
public enum RiskEventStatus {

    /** 待处理 */
    PENDING("pending"),
    /** 调查中 */
    INVESTIGATING("investigating"),
    /** 已解决 */
    RESOLVED("resolved"),
    /** 误报 */
    FALSE_POSITIVE("false_positive");

    @EnumValue
    private final String value;
}
