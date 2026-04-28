# 前端公共基础规格

## 目标

- 提供登录页、主布局和 Dashboard 首页，作为后续管理端页面的统一入口。
- 提供前端 API 模块，覆盖 auth、user、rule、event、score、decision、audit。
- 登录成功后保存 token，后续 HTTP 请求自动携带 `Authorization: Bearer <token>`。

## 非目标

- 本阶段不实现用户管理、规则管理、事件处理、决策和审计列表页面。
- 本阶段不实现刷新 token、权限菜单动态裁剪和 E2E 测试。
- Dashboard 使用静态运营指标占位，真实统计数据留给后续任务。

## 页面与路由

- `/login`：登录页面。
- `/`：主布局下的 Dashboard。
- 主布局包含侧边导航、顶部工作区标题和内容出口。

## API 模块

- `authApi`：登录、刷新、登出。
- `userApi`：当前用户、列表、创建、更新、删除。
- `ruleApi`：列表、创建、更新、删除、启用、停用。
- `eventApi`：列表、创建、更新、解决。
- `scoreApi`：列表、评分、主体最新评分。
- `decisionApi`：列表、创建、更新。
- `auditApi`：审计日志列表。

## 验收标准

- 登录表单可以提交用户名和密码。
- 主布局可以渲染导航和当前路由内容。
- Dashboard 展示关键风险运营指标。
- API 模块映射到后端既有路由。
- `pnpm vitest run`、`pnpm type-check`、`pnpm lint`、`pnpm build` 通过。

## 待确认问题

无待确认问题。
