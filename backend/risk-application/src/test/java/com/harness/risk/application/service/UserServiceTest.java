package com.harness.risk.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateUserRequest;
import com.harness.risk.application.dto.UpdateUserRequest;
import com.harness.risk.application.dto.UserResponse;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.common.security.PasswordEncoder;
import com.harness.risk.domain.user.User;
import com.harness.risk.domain.user.UserRole;
import com.harness.risk.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link UserService} 单元测试
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
    @InjectMocks
    private UserService userService;

    private User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@test.com");
        user.setHashedPassword("encoded");
        user.setRole(UserRole.ADMIN);
        user.setActive(true);
        return user;
    }

    @Test
    void meReturnsUserResponse() {
        when(userMapper.selectById(1L)).thenReturn(sampleUser());

        UserResponse resp = userService.me(1L);

        assertEquals("alice", resp.username());
        assertEquals("alice@test.com", resp.email());
    }

    @Test
    void meThrowsWhenUserNotFound() {
        when(userMapper.selectById(1L)).thenReturn(null);

        AppException e = assertThrows(AppException.class, () -> userService.me(1L));
        assertEquals(404, e.getCode());
    }

    @Test
    void listReturnsPagedResults() {
        Page<User> page = new Page<>(1, 20);
        page.setRecords(List.of(sampleUser()));
        page.setTotal(1);
        when(userMapper.selectPage(any(), any())).thenReturn(page);

        Page<UserResponse> result = userService.list(1, 20);

        assertEquals(1, result.getTotal());
        assertEquals("alice", result.getRecords().get(0).username());
    }

    @Test
    void createSuccessReturnsUserResponse() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");

        UserResponse resp = userService.create(
                new CreateUserRequest("alice", "alice@test.com", "password123", UserRole.OPERATOR));

        assertEquals("alice", resp.username());
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void createFailsWhenUsernameExists() {
        when(userMapper.selectCount(any())).thenReturn(1L);

        AppException e = assertThrows(AppException.class,
                () -> userService.create(new CreateUserRequest("alice", "a@test.com", "password123", UserRole.OPERATOR)));
        assertEquals(409, e.getCode());
    }

    @Test
    void updateSuccessReturnsUpdatedUser() {
        User user = sampleUser();
        when(userMapper.selectById(1L)).thenReturn(user);

        UserResponse resp = userService.update(1L,
                new UpdateUserRequest("new@test.com", UserRole.RISK_ANALYST, false));

        assertEquals("new@test.com", resp.email());
        assertEquals(UserRole.RISK_ANALYST, resp.role());
        assertFalse(resp.active());
        verify(userMapper).updateById(any(User.class));
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
