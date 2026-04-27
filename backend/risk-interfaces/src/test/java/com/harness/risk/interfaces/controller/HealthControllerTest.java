package com.harness.risk.interfaces.controller;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link HealthController} 单元测试
 *
 * @author harness-agent
 * @since 2026-04-27
 */
class HealthControllerTest {

    private final HealthController controller = new HealthController();

    /**
     * 验证健康检查返回成功状态
     */
    @Test
    void healthReturnsOk() {
        var response = controller.health();
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("ok", response.getData().get("status"));
    }
}
