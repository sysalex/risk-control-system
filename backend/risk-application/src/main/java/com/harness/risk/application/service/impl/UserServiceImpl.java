package com.harness.risk.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.harness.risk.application.dto.CreateUserRequest;
import com.harness.risk.application.dto.UpdateUserRequest;
import com.harness.risk.application.dto.UserResponse;
import com.harness.risk.application.service.UserService;
import com.harness.risk.common.exception.AppException;
import com.harness.risk.common.security.PasswordEncoder;
import com.harness.risk.domain.user.User;
import com.harness.risk.infrastructure.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现
 * <p>
 * 继承 {@link ServiceImpl} 获得 MyBatis-Plus 基础 CRUD，
 * 自定义方法覆盖默认行为或补充业务规则。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse me(Long userId) {
        User user = findOrThrow(userId);
        return toResponse(user);
    }

    @Override
    public Page<UserResponse> list(int page, int limit) {
        Page<User> result = page(new Page<>(page, limit));
        List<UserResponse> records = result.getRecords().stream()
                .map(this::toResponse)
                .toList();
        Page<UserResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        long count = count(
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
        save(user);

        return toResponse(user);
    }

    @Override
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
        updateById(user);
        return toResponse(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        getBaseMapper().deleteById(id);
    }

    private User findOrThrow(Long id) {
        User user = getById(id);
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
