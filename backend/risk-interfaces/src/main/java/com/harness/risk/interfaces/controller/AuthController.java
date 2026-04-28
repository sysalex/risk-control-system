package com.harness.risk.interfaces.controller;

import com.harness.risk.application.dto.LoginRequest;
import com.harness.risk.application.dto.RefreshRequest;
import com.harness.risk.application.dto.RegisterRequest;
import com.harness.risk.application.dto.TokenResponse;
import com.harness.risk.application.dto.UserResponse;
import com.harness.risk.application.service.AuthService;
import com.harness.risk.common.annotation.AuditOperation;
import com.harness.risk.common.response.ApiResponse;
import com.harness.risk.common.security.AuthConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 * <p>
 * 提供登录、注册、token 刷新和登出功能。
 * 本组接口无需 JWT 认证（由 {@link com.harness.risk.starter.config.WebMvcConfig} 排除）。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return token 响应
     */
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    /**
     * 刷新 access token
     *
     * @param request 刷新请求
     * @return 新 token 响应
     */
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.ok(authService.refresh(request));
    }

    /**
     * 用户登出
     *
     * @param request HTTP 请求（提取 userId）
     * @return 成功响应
     */
    @PostMapping("/logout")
    @AuditOperation(action = "logout", resourceType = "auth")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(AuthConstants.ATTR_USER_ID);
        authService.logout(userId);
        return ApiResponse.ok(null);
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户响应
     */
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }
}
