package com.harness.risk.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审计日志响应 DTO。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    /**
     * 审计日志 ID。
     */
    private Long id;

    /**
     * 操作用户 ID。
     */
    private Long userId;

    /**
     * 操作类型。
     */
    private String action;

    /**
     * 资源类型。
     */
    private String resourceType;

    /**
     * 资源 ID。
     */
    private Long resourceId;

    /**
     * 修改前快照。
     */
    private String oldValues;

    /**
     * 修改后快照。
     */
    private String newValues;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 操作 IP 地址。
     */
    private String ipAddress;
}
