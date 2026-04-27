package com.harness.risk.interfaces.controller;

import com.harness.risk.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 健康检查接口
 * <p>
 * 用于负载均衡器存活探针和基础连通性测试。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    /**
     * 健康检查
     *
     * @return {@code {success: true, data: {status: "ok"}}}
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.ok(Map.of("status", "ok"));
    }
}
