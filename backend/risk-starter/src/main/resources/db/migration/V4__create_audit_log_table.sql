CREATE TABLE audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '操作用户 ID',
    action VARCHAR(32) NOT NULL COMMENT '操作类型：create/update/delete/enable/disable',
    resource_type VARCHAR(32) NOT NULL COMMENT '资源类型：rule/event/score/decision/user',
    resource_id BIGINT NOT NULL COMMENT '资源 ID',
    old_values JSON NULL COMMENT '修改前值',
    new_values JSON NULL COMMENT '修改后值',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '操作时间',
    ip_address VARCHAR(64) NULL COMMENT '操作者 IP 地址',
    PRIMARY KEY (id),
    KEY idx_audit_logs_user (user_id),
    KEY idx_audit_logs_action (action),
    KEY idx_audit_logs_resource (resource_type, resource_id),
    KEY idx_audit_logs_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审计日志表';
