package com.harness.risk.interfaces.controller;

import com.harness.risk.BaseApiIntegrationTest;
import com.harness.risk.application.dto.LoginRequest;
import com.harness.risk.application.dto.RefreshRequest;
import com.harness.risk.application.dto.RegisterRequest;
import com.harness.risk.application.dto.TokenResponse;
import com.harness.risk.application.dto.UserResponse;
import com.harness.risk.application.service.AuditLogService;
import com.harness.risk.application.service.AuthService;
import com.harness.risk.domain.enums.UserRoleEnums;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link AuthController} Web MVC 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class AuthControllerTest extends BaseApiIntegrationTest {

    @MockBean
    private AuthService authService;
    @MockBean
    private AuditLogService auditLogService;

    @Test
    void loginReturnsTokenResponse() throws Exception {
        when(authService.login(any())).thenReturn(new TokenResponse("access-token", "refresh-token", 1800));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("alice", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    @Test
    void registerReturnsUserResponse() throws Exception {
        when(authService.register(any())).thenReturn(
                new UserResponse(1L, "alice", "alice@test.com", UserRoleEnums.OPERATOR, true, LocalDateTime.now()));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("alice", "alice@test.com", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    void refreshReturnsTokenResponse() throws Exception {
        when(authService.refresh(any())).thenReturn(new TokenResponse("new-access", "new-refresh", 1800));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RefreshRequest("old-refresh"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-access"));
    }

    @Test
    void loginReturnsBadRequestWhenUsernameBlank() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("", "password123"))))
                .andExpect(status().isBadRequest());
    }
}
