package com.harness.risk;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * 测试用最小 Spring Boot 配置
 * <p>
 * 为 {@code @WebMvcTest} 提供配置锚点，仅启用自动配置，避免组件扫描和完整上下文加载。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class TestConfiguration {
}
