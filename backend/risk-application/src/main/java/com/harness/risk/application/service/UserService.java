package com.harness.risk.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.harness.risk.application.dto.CreateUserRequest;
import com.harness.risk.application.dto.UpdateUserRequest;
import com.harness.risk.application.dto.UserResponse;
import com.harness.risk.domain.user.User;

/**
 * 用户服务接口
 * <p>
 * 继承 {@link IService<User>} 获得基础 CRUD 能力，
 * 自定义方法供 Controller 调用。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public interface UserService extends IService<User> {

    /**
     * 获取当前用户信息
     *
     * @param userId 用户 ID
     * @return 用户响应
     */
    UserResponse me(Long userId);

    /**
     * 用户列表（分页）
     *
     * @param page  页码（从 1 开始）
     * @param limit 每页条数
     * @return 分页结果
     */
    Page<UserResponse> list(int page, int limit);

    /**
     * 创建用户（管理员）
     *
     * @param request 创建请求
     * @return 用户响应
     */
    UserResponse create(CreateUserRequest request);

    /**
     * 更新用户（管理员）
     *
     * @param id      用户 ID
     * @param request 更新请求
     * @return 更新后的用户响应
     */
    UserResponse update(Long id, UpdateUserRequest request);

    /**
     * 删除用户（管理员）
     *
     * @param id 用户 ID
     */
    void delete(Long id);
}
