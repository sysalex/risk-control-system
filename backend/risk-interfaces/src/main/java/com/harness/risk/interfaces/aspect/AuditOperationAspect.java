package com.harness.risk.interfaces.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harness.risk.application.dto.CreateAuditLogRequest;
import com.harness.risk.application.service.AuditLogService;
import com.harness.risk.common.annotation.AuditOperation;
import com.harness.risk.common.response.ApiResponse;
import com.harness.risk.common.security.AuthConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 审计操作切面。
 * <p>
 * 写接口成功返回后记录审计日志；审计失败只记录日志，不影响原业务响应。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditOperationAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    /**
     * 写操作成功返回后记录审计日志。
     *
     * @param joinPoint      连接点
     * @param auditOperation 审计操作标记
     * @param result         接口返回结果
     */
    @AfterReturning(pointcut = "@annotation(auditOperation)", returning = "result")
    public void record(JoinPoint joinPoint, AuditOperation auditOperation, Object result) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }
        HttpServletRequest request = attrs.getRequest();
        Long userId = (Long) request.getAttribute(AuthConstants.ATTR_USER_ID);
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Object data = unwrapData(result);
            auditLogService.record(new CreateAuditLogRequest(
                    userId,
                    auditOperation.action(),
                    auditOperation.resourceType(),
                    resolveResourceId(signature, joinPoint.getArgs(), data).orElse(null),
                    null,
                    toJson(data),
                    resolveIpAddress(request)
            ));
        } catch (RuntimeException ex) {
            log.warn("记录审计日志失败，uri={}", request.getRequestURI(), ex);
        }
    }

    private Object unwrapData(Object result) {
        if (result instanceof ApiResponse<?> response) {
            return response.getData();
        }
        return result;
    }

    private Optional<Long> resolveResourceId(MethodSignature signature, Object[] args, Object data) {
        Optional<Long> pathId = resolvePathVariableId(signature, args);
        if (pathId.isPresent()) {
            return pathId;
        }
        return resolveIdFromResponse(data);
    }

    private Optional<Long> resolvePathVariableId(MethodSignature signature, Object[] values) {
        Method method = signature.getMethod();
        String[] parameterNames = signature.getParameterNames() == null ? new String[0] : signature.getParameterNames();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length && i < values.length; i++) {
            PathVariable pathVariable = findPathVariable(annotations[i]);
            String parameterName = i < parameterNames.length ? parameterNames[i] : "";
            if (pathVariable != null && isIdPathVariable(pathVariable, parameterName) && values[i] instanceof Long id) {
                return Optional.of(id);
            }
        }
        return Optional.empty();
    }

    private PathVariable findPathVariable(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            PathVariable pathVariable = AnnotationUtils.getAnnotation(annotation, PathVariable.class);
            if (pathVariable != null) {
                return pathVariable;
            }
        }
        return null;
    }

    private boolean isIdPathVariable(PathVariable pathVariable, String parameterName) {
        return "id".equals(pathVariable.value())
                || "id".equals(pathVariable.name())
                || ("".equals(pathVariable.value()) && "".equals(pathVariable.name())
                && ("id".equals(parameterName) || parameterName.isBlank()));
    }

    private Optional<Long> resolveIdFromResponse(Object data) {
        if (data == null) {
            return Optional.empty();
        }
        try {
            Method method = data.getClass().getMethod("getId");
            Object id = method.invoke(data);
            if (id instanceof Long value) {
                return Optional.of(value);
            }
        } catch (ReflectiveOperationException ignored) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private String toJson(Object data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    private String resolveIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
