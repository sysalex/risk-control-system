package com.harness.risk.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.harness.risk.application.dto.CreateUserRequest;
import com.harness.risk.application.dto.UpdateUserRequest;
import com.harness.risk.application.dto.UserResponse;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.common.security.PasswordEncoder;
import com.harness.risk.domain.user.User;
import com.harness.risk.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务
 * <p>
 * 负责用户 CRUD 和查询，供 Controller 调用。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取当前用户信息
     *
     * @param userId 用户 ID
     * @return 用户响应
     * @throws AppException 404 用户不存在
     */
    public UserResponse me(Long userId) {
        User user = findOrThrow(userId);
        return toResponse(user);
    }

    /**
     * 用户列表（分页）
     *
     * @param page  页码（从 1 开始）
     * @param limit 每页条数
     * @return 分页结果
     */
    public Page<UserResponse> list(int page, int limit) {
        Page<User> result = userMapper.selectPage(new Page<>(page, limit), null);
        List<UserResponse> records = result.getRecords().stream()
                .map(this::toResponse)
                .toList();
        Page<UserResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }

    /**
     * 创建用户（管理员）
     *
     * @param request 创建请求
     * @return 用户响应
     * @throws AppException 409 用户名或邮箱已存在
     */
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.username())
                        .or()
                        .eq(User::getEmail, request.email()));
        if (count > 0) {
            throw AppException.conflict("用户名或邮箱已存在");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setHashedPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setActive(true);
        userMapper.insert(user);

        return toResponse(user);
    }

    /**
     * 更新用户（管理员）
     *
     * @param id      用户 ID
     * @param request 更新请求
     * @return 更新后的用户响应
     * @throws AppException 404 用户不存在
     */
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findOrThrow(id);
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.active() != null) {
            user.setActive(request.active());
        }
        userMapper.updateById(user);
        return toResponse(user);
    }

    /**
     * 删除用户（管理员）
     *
     * @param id 用户 ID
     * @throws AppException 404 用户不存在
     */
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        userMapper.deleteById(id);
    }

    private User findOrThrow(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw AppException.notFound("User");
        }
        return user;
    }

    private UserResponse toResponse(User user) {
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
