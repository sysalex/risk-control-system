# RiskScore / Decision 模型规格

## 背景

阶段 1.3 建立风险评分和决策记录的数据模型，为后续评分服务、人工决策、事件闭环处理和审计日志提供数据基础。

## 目标

- 定义 RiskScore 领域模型，记录风险事件的评分结果、主体信息、满分值、维度明细、评估时间和评估人。
- 定义 Decision 领域模型，记录风险事件的最终处理决策、原因、备注、决策人和决策时间。
- 定义决策类型枚举：`approve`、`reject`、`manual_review`、`escalate`。
- 提供 MyBatis-Plus Mapper 骨架，为后续 Repository/Service 做准备。
- 新增 Flyway V3 迁移，创建 `risk_scores`、`decisions` 表、外键、唯一约束和查询索引。
- 补充测试验证模型映射、默认值、枚举存储值、Mapper 边界和迁移脚本关键约束。

## 非目标

- 不实现评分算法、规则执行或决策流转业务。
- 不实现 `/scores`、`/decisions` API。
- 不实现审计日志自动注入。
- 不插入评分或决策种子数据。

## 字段要求

### RiskScore

| 字段 | 要求 |
|------|------|
| id | BIGINT 主键，自增 |
| event_id | 必填，唯一，关联 risk_events.id |
| subject_type | 必填，最长 64 |
| subject_id | 必填，最长 128 |
| score | 必填，0-100 分值 |
| max_score | 必填，默认 100.00 |
| dimensions | 必填，JSON |
| evaluated_at | 评估时间 |
| evaluator_id | 评估人，关联 users.id，可空 |
| created_at | 创建时间 |
| updated_at | 更新时间 |

### Decision

| 字段 | 要求 |
|------|------|
| id | BIGINT 主键，自增 |
| event_id | 必填，唯一，关联 risk_events.id |
| decision_type | 必填，枚举字符串 |
| reason | 必填，最长 512 |
| notes | 可选，最长 1024 |
| decided_by | 必填，关联 users.id |
| decided_at | 决策时间 |
| created_at | 创建时间 |
| updated_at | 更新时间 |

## 验收标准

- `RiskScore`、`Decision` 包含 MyBatis-Plus 表名和字段映射。
- `DecisionType` 能输出数据库存储值。
- `RiskScoreMapper`、`DecisionMapper` 继承对应实体的 `BaseMapper`。
- Flyway V3 迁移文件创建两张表、外键、唯一约束和查询索引。
- 后端 `mvn test` 通过，最终 `scripts/check.ps1` 通过。

## 澄清记录

- 当前任务来自 `docs/task-list.md` 的 `1.3 RiskScore / Decision 模型 + 迁移`。
- `RiskScore.event_id` 设计为唯一约束，保持 RiskEvent 与 RiskScore 的 1:1 关系。
- `Decision.event_id` 继续保持唯一约束，保证同一风险事件只有一个最终决策。
