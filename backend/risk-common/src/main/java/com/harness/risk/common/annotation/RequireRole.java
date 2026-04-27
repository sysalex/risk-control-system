package com.harness.risk.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法级角色权限注解
 * <p>
 * 标注在 Controller 方法上，要求调用者必须具备指定角色之一。
 * 由 {@link com.harness.risk.interfaces.aspect.RoleAspect} 切面拦截执行。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /**
     * 允许访问的角色列表（与数据库 role 字段值对应）
     *
     * @return 角色字符串数组，如 {"admin"}、{"admin","risk_analyst"}
     */
    String[] value();
}
