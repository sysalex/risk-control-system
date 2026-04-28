# 前端公共基础实施计划

## 文件计划

- `frontend/src/views/LoginView.vue`：登录页。
- `frontend/src/layouts/MainLayout.vue`：主布局。
- `frontend/src/views/DashboardView.vue`：Dashboard 首页。
- `frontend/src/router/index.ts`：登录路由与主布局子路由。
- `frontend/src/api/http.ts`：token 存取和请求头注入。
- `frontend/src/api/modules.ts`：业务 API 模块。
- `frontend/src/**/*.spec.ts`：登录页、布局、Dashboard、API 模块和路由测试。
- `docs/specs/frontend-foundation.md`、`docs/plans/frontend-foundation.md`：轻量 SDD 产物。

## TDD 步骤

1. 编写登录页、主布局、Dashboard、API 模块和路由测试。
2. 运行 Vitest，确认缺失实现导致失败。
3. 补最小实现，让新增测试通过。
4. 补 token 存取和 HTTP 请求头注入。
5. 运行前端全量测试、类型检查、lint、构建。
6. 运行项目完整质量门禁。

## 风险与约束

- 当前不做复杂权限路由，避免在没有后端刷新 token 闭环时引入半成品状态。
- UI 风格保持操作台密度，避免营销页式大图和过度装饰。
- API 类型先覆盖页面调用所需字段，后续业务页按接口继续收敛。

## 验证命令

```bash
cd frontend
pnpm vitest run
pnpm type-check
pnpm lint
pnpm build
```

```powershell
./scripts/check.ps1
```
