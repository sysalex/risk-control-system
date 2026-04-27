package com.harness.risk.starter.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 请求 ID 拦截器
 * <p>
 * 为每个请求生成唯一追踪 ID，写入响应头 {@code X-Request-ID}，
 * 用于前后端日志关联和请求链路追踪。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Slf4j
@Component
public class RequestIdInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理前生成并注入 requestId
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  目标处理器
     * @return 始终返回 true，放行请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString();
        response.setHeader("X-Request-ID", requestId);
        return true;
    }
}
