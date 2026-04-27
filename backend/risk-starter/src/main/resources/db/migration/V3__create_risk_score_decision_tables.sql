CREATE TABLE risk_scores (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    event_id BIGINT NOT NULL COMMENT '关联的风险事件 ID',
    subject_type VARCHAR(64) NOT NULL COMMENT '主体类型',
    subject_id VARCHAR(128) NOT NULL COMMENT '主体 ID',
    score DECIMAL(5,2) NOT NULL COMMENT '风险评分',
    max_score DECIMAL(5,2) NOT NULL DEFAULT 100.00 COMMENT '满分值',
    dimensions JSON NOT NULL COMMENT '各维度评分明细',
    evaluated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '评估时间',
    evaluator_id BIGINT NULL COMMENT '评估人用户 ID，空值表示系统自动评估',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_risk_scores_event (event_id),
    KEY idx_risk_scores_subject (subject_type, subject_id),
    KEY idx_risk_scores_evaluated_at (evaluated_at),
    KEY idx_risk_scores_evaluator (evaluator_id),
    CONSTRAINT fk_risk_scores_event FOREIGN KEY (event_id) REFERENCES risk_events (id),
    CONSTRAINT fk_risk_scores_evaluator FOREIGN KEY (evaluator_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='风险评分表';

CREATE TABLE decisions (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    event_id BIGINT NOT NULL COMMENT '关联的风险事件 ID',
    decision_type VARCHAR(32) NOT NULL COMMENT '决策类型：approve/reject/manual_review/escalate',
    reason VARCHAR(512) NOT NULL COMMENT '决策原因',
    notes VARCHAR(1024) NULL COMMENT '备注',
    decided_by BIGINT NOT NULL COMMENT '决策人用户 ID',
    decided_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '决策时间',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_decisions_event (event_id),
    KEY idx_decisions_type (decision_type),
    KEY idx_decisions_decided_by (decided_by),
    KEY idx_decisions_decided_at (decided_at),
    CONSTRAINT fk_decisions_event FOREIGN KEY (event_id) REFERENCES risk_events (id),
    CONSTRAINT fk_decisions_decided_by FOREIGN KEY (decided_by) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='决策记录表';
