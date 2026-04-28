# 前端分析师功能实现计划

## 文件计划

- `frontend/src/views/EventAnalysisView.vue`：风险事件列表与处理动作。
- `frontend/src/views/ScoreDecisionView.vue`：评分列表、决策列表、重新评分表单。
- `frontend/src/views/AuditLogView.vue`：审计日志列表。
- `frontend/src/router/index.ts`：注册 `/events`、`/decisions`、`/audit-logs`。
- `frontend/src/views/*spec.ts` 与 `frontend/src/router/index.spec.ts`：补齐页面和路由测试。
- `docs/task-list.md`、`CHANGELOG.md`：更新任务状态和变更记录。

## TDD 步骤

1. RED：先补页面和路由测试，确认缺失组件/路由导致失败。
2. GREEN：实现三个页面和路由注册，使定向测试通过。
3. REFACTOR：保持页面布局与阶段 8 管理页一致，避免引入新抽象。
4. VERIFY：运行前端测试、类型检查、lint、build 和全量质量门禁。

## 风险与验证命令

- 风险：前端接口类型与测试注入 mock 不一致，使用窄类型 `Pick` 限定页面依赖。
- 风险：覆盖率阈值下降，新增页面必须配套测试。

验证命令：

```bash
pnpm vitest run src/views/EventAnalysisView.spec.ts src/views/ScoreDecisionView.spec.ts src/views/AuditLogView.spec.ts src/router/index.spec.ts
pnpm vitest run --coverage
pnpm type-check
pnpm lint
pnpm build
./scripts/check.ps1
```
