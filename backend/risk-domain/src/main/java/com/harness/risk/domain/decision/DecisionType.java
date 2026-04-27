package com.harness.risk.domain.decision;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 决策类型枚举
 * <p>
 * 枚举值直接对应数据库中的 decision_type 字段。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Getter
@RequiredArgsConstructor
public enum DecisionType {

    /** 通过 */
    APPROVE("approve"),
    /** 拒绝 */
    REJECT("reject"),
    /** 人工复核 */
    MANUAL_REVIEW("manual_review"),
    /** 升级处理 */
    ESCALATE("escalate");

    @EnumValue
    private final String value;
}
