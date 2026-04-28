package com.harness.risk.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计操作标记。
 * <p>
 * 标注在需要自动记录审计日志的写接口上，由接口层切面统一落库。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditOperation {

    /**
     * 操作类型。
     *
     * @return 操作类型
     */
    String action();

    /**
     * 资源类型。
     *
     * @return 资源类型
     */
    String resourceType();
}
