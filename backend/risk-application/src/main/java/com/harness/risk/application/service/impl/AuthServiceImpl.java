package com.harness.risk.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.harness.risk.application.dto.LoginRequest;
import com.harness.risk.application.dto.RefreshRequest;
import com.harness.risk.application.dto.RegisterRequest;
import com.harness.risk.application.dto.TokenResponse;
import com.harness.risk.application.dto.UserResponse;
import com.harness.risk.application.service.AuthService;
import com.harness.risk.application.service.UserService;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.common.security.JwtUtil;
import com.harness.risk.common.security.PasswordEncoder;
import com.harness.risk.domain.model.entity.UserEntity;
import com.harness.risk.domain.enums.UserRoleEnums;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证服务实现
 * <p>
 * 负责登录、注册、token 刷新和登出逻辑。
 * 登录失败次数记录在内存，服务重启清零。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    @Data
    @AllArgsConstructor
    private static class LoginAttempt {
        private int count;
        private Instant firstFailure;
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        String username = request.getUsername();
        checkLock(username);

        UserEntity user = userService.getOne(
                new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getHashedPassword())) {
            recordFailure(username);
            throw new AppException(401, "用户名或密码错误");
        }

        clearFailures(username);
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole().getValue());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        return new TokenResponse(accessToken, refreshToken, 30 * 60L);
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        long count = userService.count(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUsername, request.getUsername())
                        .or()
                        .eq(UserEntity::getEmail, request.getEmail()));
        if (count > 0) {
            throw AppException.conflict("用户名或邮箱已存在");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setHashedPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("admin".equalsIgnoreCase(request.getUsername()) ? UserRoleEnums.ADMIN : UserRoleEnums.OPERATOR);
        user.setActive(true);
        userService.save(user);

        return toUserResponse(user);
    }

    @Override
    public TokenResponse refresh(RefreshRequest request) {
        Claims claims;
        try {
            claims = jwtUtil.parseToken(request.getRefreshToken());
        } catch (Exception e) {
            throw new AppException(401, "Token 已过期或无效");
        }

        Long userId = Long.valueOf(claims.getSubject());
        UserEntity user = userService.getById(userId);
        if (user == null) {
            throw new AppException(401, "用户不存在");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole().getValue());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        return new TokenResponse(accessToken, refreshToken, 30 * 60L);
    }

    @Override
    public void logout(Long userId) {
        log.info("User {} logged out", userId);
    }

    private void checkLock(String username) {
        LoginAttempt attempt = loginAttempts.get(username);
        if (attempt != null && attempt.getCount() >= MAX_LOGIN_ATTEMPTS) {
            if (Instant.now().isBefore(attempt.getFirstFailure().plus(LOCK_DURATION))) {
                throw new AppException(429, "账号已锁定，请 15 分钟后重试");
            }
            clearFailures(username);
        }
    }

    private void recordFailure(String username) {
        loginAttempts.compute(username, (k, v) -> {
            if (v == null || Instant.now().isAfter(v.getFirstFailure().plus(LOCK_DURATION))) {
                return new LoginAttempt(1, Instant.now());
            }
            return new LoginAttempt(v.getCount() + 1, v.getFirstFailure());
        });
    }

    private void clearFailures(String username) {
        loginAttempts.remove(username);
    }

    private UserResponse toUserResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
