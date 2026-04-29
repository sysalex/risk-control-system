CREATE TABLE risk_rules (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(128) NOT NULL COMMENT '规则名称',
    description VARCHAR(512) NULL COMMENT '规则描述',
    conditions JSON NOT NULL COMMENT '规则条件表达式',
    actions JSON NOT NULL COMMENT '触发动作',
    priority INT NOT NULL DEFAULT 100 COMMENT '优先级，数值越小优先级越高',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    creator_id BIGINT NOT NULL COMMENT '创建者用户 ID',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_risk_rules_name (name),
    KEY idx_risk_rules_creator (creator_id),
    KEY idx_risk_rules_enabled_priority (enabled, priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='风控规则表';

CREATE TABLE risk_events (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    rule_id BIGINT NOT NULL COMMENT '触发的规则 ID',
    subject_type VARCHAR(64) NOT NULL COMMENT '主体类型',
    subject_id VARCHAR(128) NOT NULL COMMENT '主体 ID',
    risk_level VARCHAR(32) NOT NULL COMMENT '风险等级：low/medium/high/critical',
    status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '事件状态：pending/investigating/resolved/false_positive',
    description VARCHAR(1024) NULL COMMENT '事件描述',
    triggered_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '触发时间',
    resolved_at DATETIME(3) NULL COMMENT '解决时间',
    resolved_by BIGINT NULL COMMENT '解决人用户 ID',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_risk_events_rule (rule_id),
    KEY idx_risk_events_subject (subject_type, subject_id),
    KEY idx_risk_events_status (status, risk_level),
    KEY idx_risk_events_triggered_at (triggered_at),
    KEY idx_risk_events_resolved_by (resolved_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='风险事件表';
