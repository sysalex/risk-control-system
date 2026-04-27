# RiskRule / RiskEvent 模型规格

## 背景

阶段 1.2 建立风控规则和风险事件的数据模型，为后续规则管理 API、事件处理 API、评分和决策模块提供数据基础。

## 目标

- 定义 RiskRule 领域模型，包含规则名称、描述、条件 JSON、动作 JSON、优先级、启用状态、创建者和审计时间字段。
- 定义 RiskEvent 领域模型，包含触发规则、主体信息、风险等级、处理状态、描述、触发时间、解决时间和解决人字段。
- 定义风险等级枚举：`low`、`medium`、`high`、`critical`。
- 定义事件状态枚举：`pending`、`investigating`、`resolved`、`false_positive`。
- 提供 MyBatis-Plus Mapper 骨架，为后续 Repository/Service 做准备。
- 新增 Flyway V2 迁移，创建 `risk_rules`、`risk_events` 表、外键和查询索引。
- 补充测试验证模型映射、默认值、枚举存储值、Mapper 边界和迁移脚本关键约束。

## 非目标

- 不实现规则表达式解析、规则执行引擎或事件处理业务。
- 不实现 `/rules`、`/events` API。
- 不创建种子规则或事件数据。
- 不实现 RiskScore、Decision、AuditLog。

## 字段要求

### RiskRule

| 字段 | 要求 |
|------|------|
| id | BIGINT 主键，自增 |
| name | 必填，唯一，最长 128 |
| description | 可选，最长 512 |
| conditions | 必填，JSON |
| actions | 必填，JSON |
| priority | 必填，默认 100 |
| enabled | 必填，默认 true |
| creator_id | 必填，关联 users.id |
| created_at | 创建时间 |
| updated_at | 更新时间 |

### RiskEvent

| 字段 | 要求 |
|------|------|
| id | BIGINT 主键，自增 |
| rule_id | 必填，关联 risk_rules.id |
| subject_type | 必填，最长 64 |
| subject_id | 必填，最长 128 |
| risk_level | 必填，枚举字符串 |
| status | 必填，默认 pending |
| description | 可选，最长 1024 |
| triggered_at | 触发时间 |
| resolved_at | 解决时间，可空 |
| resolved_by | 解决人，关联 users.id，可空 |
| created_at | 创建时间 |
| updated_at | 更新时间 |

## 验收标准

- `RiskRule`、`RiskEvent` 包含 MyBatis-Plus 表名和字段映射。
- 枚举能输出数据库存储值。
- `RiskRuleMapper`、`RiskEventMapper` 继承对应实体的 `BaseMapper`。
- Flyway V2 迁移文件创建两张表、外键、唯一索引和查询索引。
- 后端 `mvn test` 通过，最终 `scripts/check.ps1` 通过。

## 澄清记录

- 当前任务来自 `docs/task-list.md` 的 `1.2 RiskRule / RiskEvent 模型 + 迁移`。
- MySQL 使用 `JSON` 类型承载规则条件和动作，领域模型暂以 `String` 保存 JSON 文本，后续规则引擎阶段再决定是否引入类型化表达式和值对象。
