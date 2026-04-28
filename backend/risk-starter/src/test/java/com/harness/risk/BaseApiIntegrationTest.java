package com.harness.risk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harness.risk.common.security.JwtUtil;
import com.harness.risk.starter.RiskApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * API 集成测试基类，统一提供 Spring Boot 测试上下文、MockMvc 和常用工具方法。
 * <p>
 * 所有 Controller 层集成测试应继承此类，避免重复声明注解和注入字段。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@SpringBootTest(classes = RiskApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseApiIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtUtil jwtUtil;

    /**
     * 将对象序列化为 JSON 字节数组，用于 MockMvc 请求体。
     *
     * @param value 待序列化的对象
     * @return JSON 字节数组
     * @throws Exception 序列化异常
     */
    protected byte[] json(Object value) throws Exception {
        return objectMapper.writeValueAsBytes(value);
    }

    /**
     * 生成 ADMIN 角色的 Bearer Token 请求头值。
     *
     * @param userId 用户 ID
     * @param username 用户名
     * @return Bearer Token 字符串
     */
    protected String adminToken(Long userId, String username) {
        return "Bearer " + jwtUtil.generateAccessToken(userId, username, "admin");
    }

    /**
     * 生成 RISK_ANALYST 角色的 Bearer Token 请求头值。
     *
     * @param userId 用户 ID
     * @param username 用户名
     * @return Bearer Token 字符串
     */
    protected String analystToken(Long userId, String username) {
        return "Bearer " + jwtUtil.generateAccessToken(userId, username, "analyst");
    }

    /**
     * 生成 OPERATOR 角色的 Bearer Token 请求头值。
     *
     * @param userId 用户 ID
     * @param username 用户名
     * @return Bearer Token 字符串
     */
    protected String operatorToken(Long userId, String username) {
        return "Bearer " + jwtUtil.generateAccessToken(userId, username, "operator");
    }

    /**
     * 构造 {@code Content-Type: application/json} 的媒体类型常量。
     *
     * @return application/json 媒体类型
     */
    protected MediaType applicationJson() {
        return MediaType.APPLICATION_JSON;
    }
}
