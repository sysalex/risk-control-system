package com.harness.risk.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AppException} 单元测试
 *
 * @author harness-agent
 * @since 2026-04-28
 */
class AppExceptionTest {

    @Test
    void constructorWithCodeAndMessage() {
        AppException ex = new AppException(404, "not found");

        assertEquals(404, ex.getCode());
        assertEquals("not found", ex.getMessage());
    }

    @Test
    void constructorWithMessageDefaultsTo400() {
        AppException ex = new AppException("bad request");

        assertEquals(400, ex.getCode());
        assertEquals("bad request", ex.getMessage());
    }

    @Test
    void notFoundFactoryMethod() {
        AppException ex = AppException.notFound("User");

        assertEquals(404, ex.getCode());
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void forbiddenFactoryMethod() {
        AppException ex = AppException.forbidden("no permission");

        assertEquals(403, ex.getCode());
        assertEquals("no permission", ex.getMessage());
    }

    @Test
    void conflictFactoryMethod() {
        AppException ex = AppException.conflict("duplicate");

        assertEquals(409, ex.getCode());
        assertEquals("duplicate", ex.getMessage());
    }

    @Test
    void inheritsRuntimeException() {
        AppException ex = new AppException(500, "error");

        assertTrue(ex instanceof RuntimeException);
    }
}
