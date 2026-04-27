package com.harness.risk.application.service;

import com.harness.risk.application.dto.LoginRequest;
import com.harness.risk.application.dto.RefreshRequest;
import com.harness.risk.application.dto.RegisterRequest;
import com.harness.risk.application.dto.TokenResponse;
import com.harness.risk.application.dto.UserResponse;

/**
 * 认证服务接口
 * <p>
 * 负责登录、注册、token 刷新和登出逻辑。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return token 响应
     */
    TokenResponse login(LoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户响应
     */
    UserResponse register(RegisterRequest request);

    /**
     * 刷新 token
     *
     * @param request 刷新请求
     * @return 新 token 响应
     */
    TokenResponse refresh(RefreshRequest request);

    /**
     * 登出
     *
     * @param userId 用户 ID
     */
    void logout(Long userId);
}
