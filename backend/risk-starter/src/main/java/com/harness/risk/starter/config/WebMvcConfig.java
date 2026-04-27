package com.harness.risk.starter.config;

import com.harness.risk.starter.interceptor.RequestIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * <p>
 * 负责注册全局拦截器和 CORS 跨域配置。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestIdInterceptor requestIdInterceptor;

    /**
     * 注册请求追踪拦截器
     *
     * @param registry 拦截器注册中心
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestIdInterceptor);
    }

    /**
     * 配置 CORS 跨域策略，仅允许前端开发服务器
     *
     * @param registry CORS 注册中心
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
