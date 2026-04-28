package com.harness.risk.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateUserRequest;
import com.harness.risk.application.dto.UpdateUserRequest;
import com.harness.risk.application.dto.UserResponse;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.common.security.PasswordEncoder;
import com.harness.risk.domain.model.entity.UserEntity;
import com.harness.risk.domain.enums.UserRoleEnums;
import com.harness.risk.application.service.impl.UserServiceImpl;
import com.harness.risk.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link UserServiceImpl} 单元测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
    }

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
    void meReturnsUserResponse() {
        when(userMapper.selectById(1L)).thenReturn(sampleUser());

        UserResponse resp = userService.me(1L);

        assertEquals("alice", resp.getUsername());
        assertEquals("alice@test.com", resp.getEmail());
    }

    @Test
    void meThrowsWhenUserNotFound() {
        when(userMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class, () -> userService.me(1L));
        assertEquals(404, e.getCode());
    }

    @Test
    void listReturnsPagedResults() {
        Page<UserEntity> page = new Page<>(1, 20);
        page.setRecords(List.of(sampleUser()));
        page.setTotal(1);
        when(userMapper.selectPage(any(), any())).thenReturn(page);

        Page<UserResponse> result = userService.list(1, 20);

        assertEquals(1, result.getTotal());
        assertEquals("alice", result.getRecords().get(0).getUsername());
    }

    @Test
    void createSuccessReturnsUserResponse() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");

        UserResponse resp = userService.create(
                new CreateUserRequest("alice", "alice@test.com", "password123", UserRoleEnums.OPERATOR));

        assertEquals("alice", resp.getUsername());
        verify(userMapper).insert(any(UserEntity.class));
    }

    @Test
    void createFailsWhenUsernameExists() {
        when(userMapper.selectCount(any())).thenReturn(1L);

        AppException e = assertThrows(AppException.class,
                () -> userService.create(new CreateUserRequest("alice", "a@test.com", "password123", UserRoleEnums.OPERATOR)));
        assertEquals(409, e.getCode());
    }

    @Test
    void updateSuccessReturnsUpdatedUser() {
        UserEntity user = sampleUser();
        when(userMapper.selectById(1L)).thenReturn(user);

        UserResponse resp = userService.update(1L,
                new UpdateUserRequest("new@test.com", UserRoleEnums.RISK_ANALYST, false));

        assertEquals("new@test.com", resp.getEmail());
        assertEquals(UserRoleEnums.RISK_ANALYST, resp.getRole());
        assertFalse(resp.isActive());
        verify(userMapper).updateById(any(UserEntity.class));
    }

    @Test
    void updateFailsWhenUserNotFound() {
        when(userMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class,
                () -> userService.update(1L, new UpdateUserRequest("new@test.com", null, null)));
        assertEquals(404, e.getCode());
    }

    @Test
    void deleteSuccess() {
        when(userMapper.selectById(1L)).thenReturn(sampleUser());

        userService.delete(1L);

        verify(userMapper).deleteById(1L);
    }

    @Test
    void deleteFailsWhenUserNotFound() {
        when(userMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class, () -> userService.delete(1L));
        assertEquals(404, e.getCode());
    }
}
