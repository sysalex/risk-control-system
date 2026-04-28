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
import com.harness.risk.domain.model.entity.UserEntity;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse me(Long userId) {
        UserEntity user = findOrThrow(userId);
        return toResponse(user);
    }

    @Override
    public Page<UserResponse> list(int page, int limit) {
        Page<UserEntity> result = page(new Page<>(page, limit));
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
        user.setRole(request.getRole());
        user.setActive(true);
        save(user);

        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        UserEntity user = findOrThrow(id);
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
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

    private UserEntity findOrThrow(Long id) {
        UserEntity user = getById(id);
        if (user == null) {
            throw AppException.notFound("User");
        }
        return user;
    }

    private UserResponse toResponse(UserEntity user) {
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
