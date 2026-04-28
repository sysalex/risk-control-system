# 管理员前端功能实施计划

## 文件计划

- `frontend/src/views/UserManagementView.vue`：用户管理页面。
- `frontend/src/views/UserManagementView.spec.ts`：用户管理页面测试。
- `frontend/src/views/RuleManagementView.vue`：规则管理页面。
- `frontend/src/views/RuleManagementView.spec.ts`：规则管理页面测试。
- `frontend/src/router/index.ts`：新增 `/users`、`/rules` 路由。
- `frontend/src/router/index.spec.ts`：补充管理员功能页路由断言。
- `docs/specs/admin-frontend.md`、`docs/plans/admin-frontend.md`：轻量 SDD 产物。

## TDD 步骤

1. 编写用户管理、规则管理和路由测试，确认实现缺失导致失败。
2. 实现用户管理页面，接入 `userApi`。
3. 实现规则管理页面，接入 `ruleApi`。
4. 补充路由配置。
5. 运行前端测试、覆盖率、类型检查、lint、构建。
6. 运行项目完整质量门禁。

## 风险与约束

- 当前先使用原生表格和表单，保持页面可用且易扩展。
- 规则条件和动作本阶段以默认 JSON 占位，复杂配置器留到后续业务增强。
- 删除操作暂不加确认弹窗，后续可统一引入确认交互。

## 验证命令

```bash
cd frontend
pnpm vitest run
pnpm coverage
pnpm type-check
pnpm lint
pnpm build
```

```powershell
./scripts/check.ps1
```
