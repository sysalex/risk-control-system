# 规格：风险事件 API

> 轻量 SDD 产物，阶段 4 风险事件模块

---

## 目标

提供风险事件的 CRUD 管理能力，支持事件的创建、查询、状态更新、解决。

## 非目标

- 规则引擎自动触发事件的逻辑（规则引擎在后续阶段实现）
- 事件与风险评分的关联计算（阶段 5 实现）
- 审计日志的自动注入机制（阶段 6 统一实现）

## 验收标准

1. `/events` 端点支持分页列表查询，支持按 `riskLevel` 和 `status` 筛选
2. `POST /events` 创建事件
3. `GET /events/{id}` 查询事件详情
4. `PUT /events/{id}` 更新事件状态（PENDING / INVESTIGATING / RESOLVED / FALSE_POSITIVE）
5. `POST /events/{id}/resolve` 解决事件，记录解决时间和解决人
6. 测试覆盖率满足分层要求：`risk-application` ≥ 85%，`risk-interfaces` ≥ 70%

## 默认假设

1. 创建事件时必需字段：`ruleId`、`subjectType`、`subjectId`、`riskLevel`、`description`
2. 事件创建时状态默认为 `PENDING`，`triggeredAt` 默认为当前时间
3. 解决事件时通过 JWT 提取 `resolvedBy`，并设置 `resolvedAt` 为当前时间
4. 列表查询的 `riskLevel` 和 `status` 筛选参数为可选，不传则返回全部
5. 分页参数统一为 `page` / `limit`，默认 `page=1`，`limit=20`
6. 权限：list / get 对所有认证用户开放；create / update / resolve 仅限 ADMIN
7. 更新事件时只允许修改 `status` 和 `description`，其他字段不可变更

## 待确认问题

**无待确认问题。** 所有边界均可从现有规范（`domain-model.md`、`api-spec.md`、实体代码）推断出合理默认值。
