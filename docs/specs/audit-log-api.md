# 审计日志 API 规格

## 目标

- 提供审计日志分页查询和详情查询接口。
- 对所有已标记的后端写操作自动记录审计日志。
- 审计日志查询仅允许管理员访问。

## 非目标

- 本阶段不实现审计日志导出、归档、清理策略。
- 本阶段不补全修改前快照，`oldValues` 暂保留为后续增强点。
- 登录、注册等匿名入口不纳入强制审计；已认证的登出操作纳入审计。

## API

- `GET /api/v1/audit-logs`
  - 查询参数：`page`、`limit`、`userId`、`action`、`resourceType`
  - 权限：`admin`
- `GET /api/v1/audit-logs/{id}`
  - 权限：`admin`

## 自动审计

- 新增 `@AuditOperation(action, resourceType)` 标记写接口。
- 写接口成功返回后由 `AuditOperationAspect` 统一写入 `audit_logs`。
- 审计字段：
  - `userId`：当前请求认证用户 ID
  - `action`：操作类型
  - `resourceType`：资源类型
  - `resourceId`：优先取路径变量 `id`，否则取响应对象 `getId()`
  - `newValues`：成功响应业务数据 JSON 快照
  - `ipAddress`：优先取 `X-Forwarded-For` 首个 IP，否则取 `remoteAddr`

## 验收标准

- 管理员可以分页查询审计日志。
- 非管理员访问审计日志接口返回 403。
- 审计日志详情不存在时返回 404。
- 规则、用户、事件、评分、决策及登出写操作成功后触发自动审计。
- 审计记录失败不影响原写操作响应。

## 待确认问题

无待确认问题。
