package com.harness.risk.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.BaseApiIntegrationTest;
import com.harness.risk.application.dto.CreateUserRequest;
import com.harness.risk.application.dto.UpdateUserRequest;
import com.harness.risk.application.dto.UserResponse;
import com.harness.risk.application.service.AuditLogService;
import com.harness.risk.application.service.UserService;
import com.harness.risk.domain.enums.UserRoleEnums;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link UserController} Web MVC 测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class UserControllerTest extends BaseApiIntegrationTest {

    @MockBean
    private UserService userService;
    @MockBean
    private AuditLogService auditLogService;

    private String adminToken() {
        return adminToken(1L, "alice");
    }

    private UserResponse sampleUser() {
        return new UserResponse(1L, "alice", "alice@test.com", UserRoleEnums.ADMIN, true, LocalDateTime.now());
    }

    @Test
    void meReturnsUserResponse() throws Exception {
        when(userService.me(1L)).thenReturn(sampleUser());

        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    void listReturnsPagedUsers() throws Exception {
        Page<UserResponse> page = new Page<>();
        page.setRecords(List.of(sampleUser()));
        page.setTotal(1);
        when(userService.list(1, 20)).thenReturn(page);

        mockMvc.perform(get("/api/v1/users?page=1&limit=20")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].username").value("alice"));
    }

    @Test
    void createReturnsUserResponse() throws Exception {
        when(userService.create(any())).thenReturn(sampleUser());

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateUserRequest("alice", "alice@test.com", "password123", UserRoleEnums.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    void updateReturnsUpdatedUser() throws Exception {
        when(userService.update(eq(1L), any())).thenReturn(sampleUser());

        mockMvc.perform(put("/api/v1/users/1")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateUserRequest("new@test.com", UserRoleEnums.RISK_ANALYST, false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    void deleteReturnsSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
