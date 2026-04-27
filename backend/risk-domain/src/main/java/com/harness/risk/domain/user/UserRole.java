package com.harness.risk.domain.user;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户角色枚举
 * <p>
 * 枚举值直接对应数据库中的 role 字段，避免展示名称和存储值混用。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {

    /** 管理员 */
    ADMIN("admin"),
    /** 风控分析师 */
    RISK_ANALYST("risk_analyst"),
    /** 操作员 */
    OPERATOR("operator");

    @EnumValue
    private final String value;
}
