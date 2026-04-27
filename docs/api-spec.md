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
GET    /rules                       # 规则列表
POST   /rules                       # 创建规则（admin/analyst）
GET    /rules/{id}                  # 规则详情
PUT    /rules/{id}                  # 更新规则
DELETE /rules/{id}                  # 删除规则
POST   /rules/{id}/enable           # 启用规则
POST   /rules/{id}/disable          # 停用规则
```

## 风险事件接口

```
GET    /events                      # 事件列表（支持级别/状态筛选）
POST   /events                      # 创建事件（规则引擎自动触发）
GET    /events/{id}                 # 事件详情
PUT    /events/{id}                 # 更新事件状态
POST   /events/{id}/resolve         # 解决事件
```

## 风险评分接口

```
GET    /scores                      # 评分列表
POST   /scores/evaluate             # 执行风险评估
GET    /scores/{id}                 # 评分详情
GET    /scores/subject/{type}/{id}  # 某主体的最新评分
```

## 决策接口

```
GET    /decisions                   # 决策列表
POST   /decisions                   # 创建决策
GET    /decisions/{id}              # 决策详情
PUT    /decisions/{id}              # 更新决策
```

## 审计日志接口

```
GET    /audit-logs                  # 审计日志列表（支持时间/用户/操作筛选）
GET    /audit-logs/{id}             # 日志详情
```
