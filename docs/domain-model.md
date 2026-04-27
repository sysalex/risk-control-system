# 领域模型

## 实体关系图

```
┌──────────┐       ┌─────────────────┐       ┌──────────────┐
│   User   │──1:N──│   RiskRule      │──1:N──│  RiskEvent   │
│          │       │                 │       │              │
│ id       │       │ id              │       │ id           │
│ username │       │ name            │       │ rule_id      │
│ email    │       │ description     │       │ subject_type │
│ password │       │ conditions      │       │ subject_id   │
│ role     │       │ actions         │       │ risk_level   │
│ role     │       │ priority        │       │ status       │
└──────────┘       │ enabled         │       │ triggered_at │
     │             │ creator_id      │       └──────┬───────┘
     │             └─────────────────┘              │
     │ 1:N                                          │ 1:1
     ▼                                              ▼
┌──────────┐       ┌─────────────────┐       ┌──────────────┐
│Decision  │◄──────│                 │       │  RiskScore   │
│          │       │                 │──────►│              │
│ id       │       │                 │       │ id           │
│ event_id │       │                 │       │ subject_type │
│ type     │       │                 │       │ subject_id   │
│ reason   │       │                 │       │ score        │
│ decided  │       │                 │       │ dimensions   │
│ decided  │       │                 │       │ evaluated_at │
└──────────┘       └─────────────────┘       └──────────────┘

┌──────────┐
│AuditLog  │
│          │
│ id       │
│ user_id  │
│ action   │
│ resource │
│ old_val  │
│ new_val  │
│ created  │
└──────────┘
```

## 实体详细说明

### User（用户）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键，自增 |
| username | VARCHAR(64) UNIQUE | 用户名 |
| email | VARCHAR(128) UNIQUE | 邮箱 |
| hashed_password | VARCHAR(255) | BCrypt 哈希密码 |
| role | VARCHAR(32) | admin / risk_analyst / operator |
| is_active | TINYINT(1) | 是否启用 |
| created_at | DATETIME(3) | 创建时间 |
| updated_at | DATETIME(3) | 更新时间 |

### RiskRule（风控规则）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键，自增 |
| name | VARCHAR(128) UNIQUE | 规则名称 |
| description | VARCHAR(512) | 规则描述 |
| conditions | JSON | 规则条件表达式 |
| actions | JSON | 触发动作（告警/拒绝/标记等） |
| priority | INT | 优先级（数值越小优先级越高，默认 100） |
| enabled | TINYINT(1) | 是否启用 |
| creator_id | BIGINT FK | 创建者 |
| created_at | DATETIME(3) | 创建时间 |
| updated_at | DATETIME(3) | 更新时间 |

conditions 字段 JSON 结构示例：
```json
{
  "operator": "AND",
  "conditions": [
    { "field": "transaction_amount", "op": ">", "value": 10000 },
    { "field": "country", "op": "IN", "value": ["XX", "YY"] }
  ]
}
```

actions 字段 JSON 结构示例：
```json
{
  "type": "alert",
  "risk_level": "high",
  "notify_users": [1, 2]
}
```

### RiskEvent（风险事件）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键，自增 |
| rule_id | BIGINT FK | 触发的规则 |
| subject_type | VARCHAR(64) | 主体类型（user/transaction/account 等） |
| subject_id | VARCHAR(128) | 主体 ID |
| risk_level | VARCHAR(32) | low / medium / high / critical |
| status | VARCHAR(32) | pending / investigating / resolved / false_positive |
| description | VARCHAR(1024) | 事件描述 |
| triggered_at | DATETIME(3) | 触发时间 |
| resolved_at | DATETIME(3) | 解决时间 |
| resolved_by | BIGINT FK | 解决人 |
| created_at | DATETIME(3) | 创建时间 |
| updated_at | DATETIME(3) | 更新时间 |

### RiskScore（风险评分）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int PK | 主键 |
| subject_type | str | 主体类型 |
| subject_id | str | 主体 ID |
| score | float | 风险评分（0-100） |
| max_score | float | 满分值（100） |
| dimensions | jsonb | 各维度评分明细 |
| evaluated_at | datetime | 评估时间 |
| evaluator_id | int FK | 评估人/系统 |

dimensions 字段 JSON 结构示例：
```json
{
  "transaction_pattern": 30,
  "geolocation": 20,
  "behavior_anomaly": 40,
  "history": 10
}
```

### Decision（决策记录）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int PK | 主键 |
| event_id | int FK (UNIQUE) | 关联的风险事件 |
| decision_type | enum | approve / reject / manual_review / escalate |
| reason | str | 决策原因 |
| notes | str | 备注 |
| decided_by | int FK | 决策人 |
| decided_at | datetime | 决策时间 |

状态枚举：
- `approve`：通过（非风险）
- `reject`：拒绝/拦截
- `manual_review`：转人工审核
- `escalate`：升级处理

### AuditLog（审计日志）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int PK | 主键 |
| user_id | int FK | 操作用户 |
| action | str | 操作类型（create/update/delete/enable/disable） |
| resource_type | str | 资源类型（rule/event/score/decision/user） |
| resource_id | int | 资源 ID |
| old_values | jsonb | 修改前值 |
| new_values | jsonb | 修改后值 |
| created_at | datetime | 操作时间 |
| ip_address | str | 操作 IP |

## 业务规则

1. 规则启用前必须经过验证（至少一条测试事件通过）
2. 规则变更（创建/修改/启停）必须记录审计日志
3. 同一风险事件的决策只能有一个，决策后不可直接修改
4. 风险评分由系统自动计算，人工不可直接修改评分值
5. 审计日志为只读追加，不可修改或删除
6. 高风险（high/critical）事件必须在 24 小时内处理
7. 规则优先级相同时，按创建时间先后顺序执行
