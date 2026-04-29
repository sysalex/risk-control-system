USE risk_db;

-- 规则数据
INSERT INTO risk_rules (name, description, conditions, actions, priority, enabled, creator_id, created_at, updated_at) VALUES
('大额交易检测', '单笔金额超过1万触发预警', '{"operator":"AND","conditions":[{"field":"amount","op":">","value":10000}]}', '{"type":"create_event","riskLevel":"high"}', 10, 1, 1, NOW(3), NOW(3)),
('异地登录检测', '非常用IP登录触发预警', '{"operator":"AND","conditions":[{"field":"ip","op":"not_in","value":["192.168.1.0/24"]}]}', '{"type":"create_event","riskLevel":"medium"}', 20, 1, 1, NOW(3), NOW(3)),
('高频交易检测', '1分钟内超过10笔交易', '{"operator":"AND","conditions":[{"field":"frequency","op":">","value":10}]}', '{"type":"create_event","riskLevel":"critical"}', 5, 1, 1, NOW(3), NOW(3));

-- 风险事件数据
INSERT INTO risk_events (rule_id, subject_type, subject_id, risk_level, status, description, triggered_at, resolved_at, resolved_by, created_at, updated_at) VALUES
(1, 'transaction', 'TX-20250429-001', 'high',    'pending',       '用户 admin 发起 5 万元转账', NOW(3), NULL,   NULL, NOW(3), NOW(3)),
(1, 'transaction', 'TX-20250429-002', 'high',    'investigating', '用户 bob 发起 12 万元转账，正在调查', NOW(3), NULL,   NULL, NOW(3), NOW(3)),
(2, 'login',       'LOGIN-20250429-003', 'medium',  'resolved',      '用户 alice 从异常 IP 登录', NOW(3), NOW(3), 1,    NOW(3), NOW(3)),
(3, 'transaction', 'TX-20250429-004', 'critical','pending',       '用户 mallory 1 分钟内发起 15 笔交易', NOW(3), NULL,   NULL, NOW(3), NOW(3));

-- 风险评分数据
INSERT INTO risk_scores (event_id, subject_type, subject_id, score, max_score, dimensions, evaluated_at, evaluator_id, created_at, updated_at) VALUES
(1, 'transaction', 'TX-20250429-001', 85.50, 100.00, '{"amount_factor":30,"frequency_factor":25,"location_factor":30,"behavior_factor":0.5}', NOW(3), 1, NOW(3), NOW(3)),
(2, 'transaction', 'TX-20250429-002', 72.00, 100.00, '{"amount_factor":25,"frequency_factor":20,"location_factor":27}', NOW(3), 1, NOW(3), NOW(3)),
(4, 'transaction', 'TX-20250429-004', 95.00, 100.00, '{"amount_factor":35,"frequency_factor":35,"location_factor":25}', NOW(3), 1, NOW(3), NOW(3));

-- 决策记录数据
INSERT INTO decisions (event_id, decision_type, reason, notes, decided_by, decided_at, created_at, updated_at) VALUES
(1, 'MANUAL_REVIEW', '金额较大，需要人工复核', '已通知风控分析师', 1, NOW(3), NOW(3), NOW(3)),
(3, 'APPROVE',       '确认为用户本人操作',     '用户已通过二次验证', 1, NOW(3), NOW(3), NOW(3)),
(4, 'REJECT',        '疑似盗刷，建议冻结账户', '已联系用户确认',    1, NOW(3), NOW(3), NOW(3));

-- 审计日志数据
INSERT INTO audit_logs (user_id, action, resource_type, resource_id, old_values, new_values, created_at, ip_address) VALUES
(1, 'create', 'rule',     1, NULL,                   '{"name":"大额交易检测"}',           NOW(3), '127.0.0.1'),
(1, 'create', 'event',    1, NULL,                   '{"subjectId":"TX-20250429-001"}', NOW(3), '127.0.0.1'),
(1, 'update', 'event',    3, '{"status":"pending"}', '{"status":"resolved"}',           NOW(3), '127.0.0.1'),
(1, 'create', 'decision', 1, NULL,                   '{"decisionType":"MANUAL_REVIEW"}',NOW(3), '127.0.0.1');
