package com.harness.risk.common.security;

/**
 * 认证相关常量
 * <p>
 * 定义 JWT 拦截器写入 request attribute 的键名，供各层统一引用。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
public final class AuthConstants {

    private AuthConstants() {
    }

    /** request attribute：当前用户 ID */
    public static final String ATTR_USER_ID = "userId";

    /** request attribute：当前用户名 */
    public static final String ATTR_USERNAME = "username";

    /** request attribute：当前用户角色 */
    public static final String ATTR_ROLE = "role";
}
