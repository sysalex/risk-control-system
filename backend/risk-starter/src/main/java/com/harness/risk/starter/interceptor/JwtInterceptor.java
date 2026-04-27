package com.harness.risk.starter.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器
 * <p>
 * 校验请求头 {@code Authorization} 是否携带合法的 Bearer token，
 * 未携带或格式错误时返回 401 状态码拦截请求。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    /**
     * 校验 JWT token 合法性
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  目标处理器
     * @return token 有效时返回 true，否则返回 false 拦截请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        // 未携带 Authorization 头或不是 Bearer 格式
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }
        String token = authHeader.substring(7);
        // token 为空
        if (token.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
