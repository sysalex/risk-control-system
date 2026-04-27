package com.harness.risk.starter.interceptor;

import com.harness.risk.common.security.AuthConstants;
import com.harness.risk.common.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link JwtInterceptor} 单元测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@ExtendWith(MockitoExtension.class)
class JwtInterceptorTest {

    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private JwtInterceptor interceptor;

    @Test
    void preHandleReturnsTrueAndInjectsAttributesWithValidToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer valid-token");

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(claims.get("username", String.class)).thenReturn("alice");
        when(claims.get("role", String.class)).thenReturn("admin");
        when(jwtUtil.parseToken("valid-token")).thenReturn(claims);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals(1L, request.getAttribute(AuthConstants.ATTR_USER_ID));
        assertEquals("alice", request.getAttribute(AuthConstants.ATTR_USERNAME));
        assertEquals("admin", request.getAttribute(AuthConstants.ATTR_ROLE));
    }

    @Test
    void preHandleReturns401WhenNoAuthHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(401, response.getStatus());
    }

    @Test
    void preHandleReturns401WhenBearerPrefixMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(401, response.getStatus());
    }

    @Test
    void preHandleReturns401WhenTokenInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer invalid-token");

        when(jwtUtil.parseToken("invalid-token")).thenThrow(new RuntimeException("invalid"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(401, response.getStatus());
    }
}
