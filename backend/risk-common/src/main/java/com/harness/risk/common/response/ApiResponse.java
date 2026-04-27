package com.harness.risk.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应信封
 * <p>
 * 所有 REST 接口返回统一格式：{@code {success, data, message, meta}}。
 * 前端通过 Axios 拦截器自动解包 {@code data} 字段，业务代码无需关心信封结构。
 *
 * @param <T> 响应数据类型
 * @author harness-agent
 * @since 2026-04-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** 请求是否成功 */
    private boolean success;
    /** 响应数据 */
    private T data;
    /** 错误描述（失败时） */
    private String message;
    /** 分页元信息（列表接口） */
    private Meta meta;

    /**
     * 构建成功响应（单条数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * 构建成功响应（列表数据 + 分页信息）
     *
     * @param data  响应数据
     * @param total 总记录数
     * @param page  当前页码
     * @param limit 每页条数
     * @param <T>   数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> ok(T data, long total, int page, int limit) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(new Meta(total, page, limit))
                .build();
    }

    /**
     * 构建失败响应
     *
     * @param message 错误描述
     * @param <T>     数据类型（忽略）
     * @return 失败响应
     */
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * 分页元信息
     *
     * @author harness-agent
     * @since 2026-04-27
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private long total;
        private int page;
        private int limit;
    }
}
