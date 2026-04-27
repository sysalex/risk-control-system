package com.harness.risk.common.exception;

import lombok.Getter;

/**
 * 业务异常基类
 * <p>
 * 所有业务异常继承此类，由 {@link GlobalExceptionHandler} 统一处理并转换为 HTTP 响应。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Getter
public class AppException extends RuntimeException {

    /** HTTP 状态码 */
    private final int code;

    /**
     * 构造带状态码的业务异常
     *
     * @param code    HTTP 状态码
     * @param message 错误描述
     */
    public AppException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造默认 400 状态码的业务异常
     *
     * @param message 错误描述
     */
    public AppException(String message) {
        this(400, message);
    }

    /**
     * 构造 404 资源不存在异常
     *
     * @param resource 资源名称
     * @return 业务异常
     */
    public static AppException notFound(String resource) {
        return new AppException(404, resource + " not found");
    }

    /**
     * 构造 403 无权限异常
     *
     * @param message 错误描述
     * @return 业务异常
     */
    public static AppException forbidden(String message) {
        return new AppException(403, message);
    }

    /**
     * 构造 409 资源冲突异常
     *
     * @param message 错误描述
     * @return 业务异常
     */
    public static AppException conflict(String message) {
        return new AppException(409, message);
    }
}
