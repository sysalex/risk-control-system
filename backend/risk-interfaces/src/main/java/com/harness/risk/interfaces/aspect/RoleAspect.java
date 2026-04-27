package com.harness.risk.interfaces.aspect;

import com.harness.risk.common.annotation.RequireRole;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.common.security.AuthConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Set;

/**
 * {@link RequireRole} 权限切面
 * <p>
 * 拦截标注了 {@link RequireRole} 的方法，从当前请求上下文中提取角色并校验。
 * 无权限时抛出 {@link AppException#forbidden}。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Aspect
@Component
public class RoleAspect {

    /**
     * 在方法执行前校验调用者角色
     *
     * @param joinPoint   连接点
     * @param requireRole 角色注解
     */
    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw AppException.forbidden("无法获取请求上下文");
        }
        HttpServletRequest request = attrs.getRequest();
        String role = (String) request.getAttribute(AuthConstants.ATTR_ROLE);
        if (role == null) {
            throw AppException.forbidden("未认证");
        }
        Set<String> allowed = Set.of(requireRole.value());
        if (!allowed.contains(role)) {
            throw AppException.forbidden("权限不足");
        }
    }
}
