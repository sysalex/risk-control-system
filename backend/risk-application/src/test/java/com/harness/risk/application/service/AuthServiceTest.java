package com.harness.risk.application.service;

import com.harness.risk.application.dto.LoginRequest;
import com.harness.risk.application.service.impl.AuthServiceImpl;
import com.harness.risk.application.dto.RefreshRequest;
import com.harness.risk.application.dto.RegisterRequest;
import com.harness.risk.application.dto.TokenResponse;
import com.harness.risk.application.dto.UserResponse;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.common.security.JwtUtil;
import com.harness.risk.common.security.PasswordEncoder;
import com.harness.risk.domain.model.entity.UserEntity;
import com.harness.risk.domain.enums.UserRoleEnums;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link AuthServiceImpl} 单元测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthServiceImpl authService;

    private UserEntity sampleUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@test.com");
        user.setHashedPassword("encoded");
        user.setRole(UserRoleEnums.ADMIN);
        user.setActive(true);
        return user;
    }

    @Test
    void loginSuccessReturnsTokens() {
        UserEntity user = sampleUser();
        when(userService.getOne(any())).thenReturn(user);
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);
        when(jwtUtil.generateAccessToken(1L, "alice", "admin")).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(1L)).thenReturn("refresh-token");

        TokenResponse resp = authService.login(new LoginRequest("alice", "password123"));

        assertEquals("access-token", resp.getAccessToken());
        assertEquals("refresh-token", resp.getRefreshToken());
        assertEquals(1800, resp.getExpiresIn());
    }

    @Test
    void loginFailsWhenUserNotFound() {
        when(userService.getOne(any())).thenReturn(null);

        AppException e = assertThrows(AppException.class,
                () -> authService.login(new LoginRequest("bob", "password")));
        assertEquals(401, e.getCode());
    }

    @Test
    void loginFailsWhenPasswordMismatch() {
        UserEntity user = sampleUser();
        when(userService.getOne(any())).thenReturn(user);
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        AppException e = assertThrows(AppException.class,
                () -> authService.login(new LoginRequest("alice", "wrong")));
        assertEquals(401, e.getCode());
    }

    @Test
    void loginLocksAfterFiveFailures() {
        UserEntity user = sampleUser();
        when(userService.getOne(any())).thenReturn(user);
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        for (int i = 0; i < 5; i++) {
            assertThrows(AppException.class,
                    () -> authService.login(new LoginRequest("alice", "wrong")));
        }

        AppException e = assertThrows(AppException.class,
                () -> authService.login(new LoginRequest("alice", "wrong")));
        assertEquals(429, e.getCode());
    }

    @Test
    void registerSuccessReturnsUserResponse() {
        when(userService.count(any())).thenReturn(0L);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");

        UserResponse resp = authService.register(
                new RegisterRequest("alice", "alice@test.com", "password123"));

        assertEquals("alice", resp.getUsername());
        assertEquals("alice@test.com", resp.getEmail());
        verify(userService).save(any(UserEntity.class));
    }

    @Test
    void registerFailsWhenUsernameOrEmailExists() {
        when(userService.count(any())).thenReturn(1L);

        AppException e = assertThrows(AppException.class,
                () -> authService.register(new RegisterRequest("alice", "a@test.com", "password123")));
        assertEquals(409, e.getCode());
    }

    @Test
    void refreshSuccessReturnsNewTokens() {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(jwtUtil.parseToken("old-refresh")).thenReturn(claims);

        UserEntity user = sampleUser();
        when(userService.getById(1L)).thenReturn(user);
        when(jwtUtil.generateAccessToken(1L, "alice", "admin")).thenReturn("new-access");
        when(jwtUtil.generateRefreshToken(1L)).thenReturn("new-refresh");

        TokenResponse resp = authService.refresh(new RefreshRequest("old-refresh"));

        assertEquals("new-access", resp.getAccessToken());
        assertEquals("new-refresh", resp.getRefreshToken());
    }

    @Test
    void refreshFailsWhenTokenInvalid() {
        when(jwtUtil.parseToken("bad-token")).thenThrow(new AppException(401, "Token invalid"));

        AppException e = assertThrows(AppException.class,
                () -> authService.refresh(new RefreshRequest("bad-token")));
        assertEquals(401, e.getCode());
    }
}
