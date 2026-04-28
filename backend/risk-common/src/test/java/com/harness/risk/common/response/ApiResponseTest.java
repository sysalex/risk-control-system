package com.harness.risk.common.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ApiResponse} 单元测试
 *
 * @author harness-agent
 * @since 2026-04-28
 */
class ApiResponseTest {

    @Test
    void okReturnsSuccessResponse() {
        ApiResponse<String> response = ApiResponse.ok("data");

        assertTrue(response.isSuccess());
        assertEquals("data", response.getData());
        assertNull(response.getMessage());
        assertNull(response.getMeta());
    }

    @Test
    void okWithMetaReturnsPaginatedResponse() {
        ApiResponse<String> response = ApiResponse.ok("data", 100L, 2, 20);

        assertTrue(response.isSuccess());
        assertEquals("data", response.getData());
        assertNotNull(response.getMeta());
        assertEquals(100L, response.getMeta().getTotal());
        assertEquals(2, response.getMeta().getPage());
        assertEquals(20, response.getMeta().getLimit());
    }

    @Test
    void failReturnsFailureResponse() {
        ApiResponse<String> response = ApiResponse.fail("error message");

        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertEquals("error message", response.getMessage());
    }

    @Test
    void builderCreatesFullResponse() {
        ApiResponse.Meta meta = new ApiResponse.Meta(50L, 1, 10);
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .success(true)
                .data(42)
                .message(null)
                .meta(meta)
                .build();

        assertTrue(response.isSuccess());
        assertEquals(42, response.getData());
        assertEquals(50L, response.getMeta().getTotal());
    }

    @Test
    void metaEqualityAndHashCode() {
        ApiResponse.Meta meta1 = new ApiResponse.Meta(10L, 1, 20);
        ApiResponse.Meta meta2 = new ApiResponse.Meta(10L, 1, 20);

        assertEquals(meta1, meta2);
        assertEquals(meta1.hashCode(), meta2.hashCode());
    }

    @Test
    void nullDataOkIsAllowed() {
        ApiResponse<Void> response = ApiResponse.ok(null);

        assertTrue(response.isSuccess());
        assertNull(response.getData());
    }
}
