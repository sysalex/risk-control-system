package com.harness.risk.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 风控系统启动入口
 * <p>
 * 负责 Spring Boot 应用启动和 MyBatis Mapper 扫描配置。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@SpringBootApplication(scanBasePackages = "com.harness.risk")
@MapperScan("com.harness.risk.infrastructure.mapper")
public class RiskApplication {

    /**
     * 应用启动入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(RiskApplication.class, args);
    }
}
