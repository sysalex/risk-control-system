package com.harness.risk.starter.interceptor;

import com.harness.risk.common.security.AuthConstants;
import com.harness.risk.common.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器
 * <p>
 * 校验请求头 {@code Authorization} 是否携带合法的 Bearer token，
 * 验证通过后将 userId、username、role 写入 request attribute，供后续业务使用。
 * 未携带或格式错误时返回 401 状态码拦截请求。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 校验 JWT token 合法性并将用户信息注入 request attribute
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  目标处理器
     * @return token 有效时返回 true，否则返回 false 拦截请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }
        String token = authHeader.substring(7);
        if (token.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        try {
            Claims claims = jwtUtil.parseToken(token);
            request.setAttribute(AuthConstants.ATTR_USER_ID, Long.valueOf(claims.getSubject()));
            request.setAttribute(AuthConstants.ATTR_USERNAME, claims.get("username", String.class));
            request.setAttribute(AuthConstants.ATTR_ROLE, claims.get("role", String.class));
            return true;
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            response.setStatus(401);
            return false;
        }
    }
}
