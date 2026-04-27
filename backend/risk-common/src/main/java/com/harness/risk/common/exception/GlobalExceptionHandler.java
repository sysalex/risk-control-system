package com.harness.risk.common.exception;

import com.harness.risk.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一拦截 Controller 层抛出的异常，转换为统一响应格式返回客户端。
 * 禁止在 Controller 层自行捕获和包装异常。
 *
 * @author harness-agent
 * @since 2026-04-27
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 异常状态码和描述
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException e) {
        log.warn("AppException: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseEntity.status(e.getCode()).body(ApiResponse.fail(e.getMessage()));
    }

    /**
     * 处理参数校验异常（JSR-303 @Valid）
     *
     * @param e 校验异常
     * @return 400 + 字段错误信息拼接
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", message);
        return ResponseEntity.badRequest().body(ApiResponse.fail(message));
    }

    /**
     * 兜底处理：捕获所有未预期的异常
     *
     * @param e 异常
     * @return 500 + 通用错误提示（不暴露堆栈信息）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Internal server error"));
    }
}
