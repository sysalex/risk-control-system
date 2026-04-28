package com.harness.risk.interfaces.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harness.risk.application.dto.CreateAuditLogRequest;
import com.harness.risk.application.service.AuditLogService;
import com.harness.risk.common.annotation.AuditOperation;
import com.harness.risk.common.response.ApiResponse;
import com.harness.risk.common.security.AuthConstants;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link AuditOperationAspect} 单元测试。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
class AuditOperationAspectTest {

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void recordCreatesAuditLogFromSuccessfulResponse() throws Exception {
        AuditLogService auditLogService = mock(AuditLogService.class);
        AuditOperationAspect aspect = new AuditOperationAspect(auditLogService, new ObjectMapper());
        Method method = SampleController.class.getMethod("update", Long.class);
        AuditOperation annotation = method.getAnnotation(AuditOperation.class);
        JoinPoint joinPoint = mock(JoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{3L});
        when(signature.getMethod()).thenReturn(method);
        when(signature.getParameterNames()).thenReturn(new String[]{"id"});
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/rules/3");
        request.setRemoteAddr("127.0.0.1");
        request.setAttribute(AuthConstants.ATTR_USER_ID, 2L);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        aspect.record(joinPoint, annotation, ApiResponse.ok(new SampleResponse(3L, "规则")));

        verify(auditLogService).record(argThat((CreateAuditLogRequest auditLog) ->
                auditLog.getUserId().equals(2L)
                        && auditLog.getAction().equals("update")
                        && auditLog.getResourceType().equals("rule")
                        && auditLog.getResourceId().equals(3L)
                        && auditLog.getIpAddress().equals("127.0.0.1")
                        && auditLog.getNewValues().contains("\"id\":3")));
    }

    static class SampleController {
        @AuditOperation(action = "update", resourceType = "rule")
        public void update(@PathVariable Long id) {
        }
    }

    record SampleResponse(Long id, String name) {
    }
}
