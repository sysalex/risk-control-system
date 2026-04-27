# 规格文档：认证与用户 API

> SDD Specify 阶段产物，对应 `task-list.md` 阶段 2。

---

## 目标

实现完整的认证与用户管理后端 API，包括：
- JWT 登录/注册/刷新/登出
- 当前用户信息查询
- 管理员用户管理（列表、创建、更新、删除）
- 基于角色（admin / risk_analyst / operator）的接口权限控制

## 非目标

- 前端登录页面 UI（阶段 7 处理）
- OAuth / SSO 第三方登录
- 密码找回/重置流程
- 用户头像/附件上传

## 验收标准

1. `/auth/login` 返回合法 JWT access token 和 refresh token
2. 除 `/auth/login`、`/auth/register`、`/api/v1/health` 外，所有接口需携带有效 Bearer token
3. admin 角色可访问用户管理接口，其他角色返回 403
4. 登录失败 5 次后锁定账号 15 分钟
5. 测试覆盖率 ≥ 80%，全量质量门禁通过

## 默认假设

- 密码策略：最小 8 位，至少包含字母和数字
- 注册接口默认开放（无邀请码机制），后续阶段 8 管理员页面可限制
- 删除用户为硬删除（本项目用户量可控，暂不做软删除）
- Token 刷新使用 refresh token rotation（每次刷新签发新 refresh token，旧 token 废弃）
- JWT secret 从 `app.jwt.secret` 读取，使用 HS256 签名

## 待确认问题

| 编号 | 问题 | 默认假设 | 是否需确认 |
|------|------|---------|-----------|
| Q1 | 密码策略是否需强制大小写+特殊字符？ | 最小 8 位，字母+数字即可 | 否 |
| Q2 | 注册是否仅 admin 可创建用户，还是开放注册？ | 开放注册（降低阶段 2 阻塞） | 否 |
| Q3 | 删除用户是硬删除还是软删除？ | 硬删除 | 否 |

**结论**：无待确认问题，按默认假设执行。

## 接口契约

### 认证接口

```
POST /api/v1/auth/login
Body: { "username": string, "password": string }
Resp: { "accessToken": string, "refreshToken": string, "expiresIn": number }
Error: 401 用户名或密码错误

POST /api/v1/auth/refresh
Body: { "refreshToken": string }
Resp: { "accessToken": string, "refreshToken": string, "expiresIn": number }
Error: 401 refresh token 无效或过期

POST /api/v1/auth/logout
Header: Authorization: Bearer <token>
Resp: { "message": "Logged out successfully" }

POST /api/v1/auth/register
Body: { "username": string, "email": string, "password": string }
Resp: { "id": number, "username": string, "email": string, "role": string }
Error: 409 用户名或邮箱已存在
```

### 用户接口

```
GET /api/v1/users/me
Resp: { "id", "username", "email", "role", "createdAt" }

GET /api/v1/users?page=1&limit=20
Role: admin only
Resp: { data: [...], meta: { total, page, limit } }

POST /api/v1/users
Role: admin only
Body: { "username", "email", "password", "role" }

PUT /api/v1/users/{id}
Role: admin only
Body: { "email"?, "role"?, "isActive"? }

DELETE /api/v1/users/{id}
Role: admin only
Resp: 204 No Content
```

## 安全约束

- 密码使用 BCrypt（`BCryptPasswordEncoder`，strength=10）哈希存储
- 响应中绝不返回 `hashedPassword` 字段
- 登录失败次数存储在内存（`ConcurrentHashMap`），服务重启清零（后续可迁 Redis）
- JWT payload 包含 `sub`（userId）、`username`、`role`、`iat`、`exp`
- 所有写操作（创建/更新/删除用户）触发审计日志（阶段 6 自动注入，本阶段预留扩展点）
