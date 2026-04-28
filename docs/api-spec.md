# API 接口规范

## 基础规范

- Base URL: `http://localhost:8080/api/v1`
- 请求格式: `application/json`
- 认证方式: `Authorization: Bearer <JWT>`
- 响应格式: 统一信封结构

## 统一响应格式

```json
// 成功（单条）
{ "success": true, "data": {}, "message": null, "meta": null }

// 成功（列表）
{ "success": true, "data": [], "message": null, "meta": { "total": 100, "page": 1, "limit": 20 } }

// 失败
{ "success": false, "data": null, "message": "错误描述", "meta": null }
```

## 认证接口

```
POST   /auth/login          # 登录，返回 token
POST   /auth/refresh        # 刷新 token
POST   /auth/logout         # 登出
POST   /auth/register       # 注册
```

## 用户接口

```
GET    /users/me            # 获取当前用户信息
PUT    /users/me            # 更新当前用户信息
GET    /users               # 用户列表（admin）
POST   /users               # 创建用户（admin）
PUT    /users/{id}          # 更新用户（admin）
DELETE /users/{id}          # 删除用户（admin）
```

## 规则接口

```
GET    /rules                       # 规则列表（所有认证用户）
POST   /rules                       # 创建规则（admin）
GET    /rules/{id}                  # 规则详情（所有认证用户）
PUT    /rules/{id}                  # 更新规则（admin）
DELETE /rules/{id}                  # 删除规则（admin）
POST   /rules/{id}/enable           # 启用规则（admin）
POST   /rules/{id}/disable          # 停用规则（admin）
```

### 请求/响应示例

**创建规则**
```http
POST /api/v1/rules
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "name": "大额交易检测",
  "description": "检测单笔超过 1 万的交易",
  "conditions": "{\"operator\":\"AND\",\"conditions\":[]}",
  "actions": "{\"type\":\"alert\",\"risk_level\":\"high\"}",
  "priority": 10
}
```

**响应**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "大额交易检测",
    "description": "检测单笔超过 1 万的交易",
    "conditions": "{\"operator\":\"AND\",\"conditions\":[]}",
    "actions": "{\"type\":\"alert\",\"risk_level\":\"high\"}",
    "priority": 10,
    "enabled": true,
    "creatorId": 1,
    "createdAt": "2026-04-27T10:00:00",
    "updatedAt": "2026-04-27T10:00:00"
  },
  "message": null,
  "meta": null
}
```

**规则列表**
```http
GET /api/v1/rules?page=1&limit=20
Authorization: Bearer <JWT>
```

**响应**
```json
{
  "success": true,
  "data": {
    "records": [...],
    "total": 100,
    "size": 20,
    "current": 1
  },
  "message": null,
  "meta": { "total": 100, "page": 1, "limit": 20 }
}
```

## 风险事件接口

```
GET    /events                      # 事件列表（支持级别/状态筛选，所有认证用户）
POST   /events                      # 创建事件（admin）
GET    /events/{id}                 # 事件详情（所有认证用户）
PUT    /events/{id}                 # 更新事件状态（admin）
POST   /events/{id}/resolve         # 解决事件（admin）
```

### 请求/响应示例

**创建事件**
```http
POST /api/v1/events
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "ruleId": 1,
  "subjectType": "user",
  "subjectId": "user-123",
  "riskLevel": "HIGH",
  "description": "单笔交易超过 10 万元"
}
```

**响应**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "ruleId": 1,
    "subjectType": "user",
    "subjectId": "user-123",
    "riskLevel": "HIGH",
    "status": "PENDING",
    "description": "单笔交易超过 10 万元",
    "triggeredAt": "2026-04-28T09:00:00",
    "resolvedAt": null,
    "resolvedBy": null,
    "createdAt": "2026-04-28T09:00:00",
    "updatedAt": "2026-04-28T09:00:00"
  },
  "message": null,
  "meta": null
}
```

**事件列表（带筛选）**
```http
GET /api/v1/events?page=1&limit=20&riskLevel=HIGH&status=PENDING
Authorization: Bearer <JWT>
```

**解决事件**
```http
POST /api/v1/events/1/resolve
Authorization: Bearer <JWT>
```

**响应**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "status": "RESOLVED",
    "resolvedAt": "2026-04-28T10:00:00",
    "resolvedBy": 1
  },
  "message": null,
  "meta": null
}
```

## 风险评分接口

```
GET    /scores                      # 评分列表（所有认证用户）
POST   /scores/evaluate             # 创建/记录风险评分（admin）
GET    /scores/{id}                 # 评分详情（所有认证用户）
GET    /scores/subject/{type}/{id}  # 某主体的最新评分（所有认证用户）
```

### 请求/响应示例

**创建评分**
```http
POST /api/v1/scores/evaluate
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "eventId": 1,
  "subjectType": "user",
  "subjectId": "user-123",
  "score": 86.50,
  "maxScore": 100.00,
  "dimensions": "{\"behavior\":40,\"device\":30,\"geo\":16.5}",
  "evaluatorId": 1
}
```

**响应**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "eventId": 1,
    "subjectType": "user",
    "subjectId": "user-123",
    "score": 86.50,
    "maxScore": 100.00,
    "dimensions": "{\"behavior\":40,\"device\":30,\"geo\":16.5}",
    "evaluatedAt": "2026-04-28T11:00:00",
    "evaluatorId": 1,
    "createdAt": "2026-04-28T11:00:00",
    "updatedAt": "2026-04-28T11:00:00"
  },
  "message": null,
  "meta": null
}
```

**主体最新评分**
```http
GET /api/v1/scores/subject/user/user-123
Authorization: Bearer <JWT>
```

## 决策接口

```
GET    /decisions                   # 决策列表（所有认证用户）
POST   /decisions                   # 创建决策（admin）
GET    /decisions/{id}              # 决策详情（所有认证用户）
PUT    /decisions/{id}              # 更新决策（admin）
```

### 请求/响应示例

**创建决策**
```http
POST /api/v1/decisions
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "eventId": 1,
  "decisionType": "MANUAL_REVIEW",
  "reason": "风险评分过高",
  "notes": "需要人工复核",
  "decidedBy": 1
}
```

**响应**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "eventId": 1,
    "decisionType": "MANUAL_REVIEW",
    "reason": "风险评分过高",
    "notes": "需要人工复核",
    "decidedBy": 1,
    "decidedAt": "2026-04-28T11:05:00",
    "createdAt": "2026-04-28T11:05:00",
    "updatedAt": "2026-04-28T11:05:00"
  },
  "message": null,
  "meta": null
}
```

**更新决策**
```http
PUT /api/v1/decisions/1
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "decisionType": "ESCALATE",
  "reason": "升级处理",
  "notes": "转高级审核"
}
```

## 审计日志接口

```
GET    /audit-logs                  # 审计日志列表（支持时间/用户/操作筛选）
GET    /audit-logs/{id}             # 日志详情
```
