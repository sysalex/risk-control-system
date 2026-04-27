# 前端骨架实现计划

## 范围

实现 `docs/task-list.md` 中阶段 0.3 前端骨架。

## 文件计划

- `frontend/package.json`：定义脚本、依赖和开发依赖。
- `frontend/index.html`：Vite 应用入口 HTML。
- `frontend/vite.config.ts`：Vue 插件、Vitest 配置、路径别名。
- `frontend/tsconfig*.json`：TypeScript 配置。
- `frontend/.npmrc`：配置 npm 镜像。
- `frontend/src/main.ts`：创建 Vue 应用，注册 Pinia 和 Router。
- `frontend/src/App.vue`：根组件，只承载路由出口。
- `frontend/src/router/index.ts`：基础路由，先提供首页。
- `frontend/src/views/HomeView.vue`：首页占位，用于验证路由。
- `frontend/src/stores/app.ts`：基础应用 Store。
- `frontend/src/api/http.ts`：Axios 实例和统一响应类型。
- `frontend/src/api/index.ts`：API 层出口。
- `frontend/src/test/setup.ts` 与 `frontend/src/**/*.spec.ts`：测试基线。

## TDD 步骤

1. 先创建测试和测试配置，断言 App Store 默认状态、路由首页、HTTP baseURL。
2. 运行 `pnpm vitest run`，确认因实现缺失失败。
3. 补最小实现让测试通过。
4. 运行 `pnpm vitest run`、`pnpm type-check`、`pnpm lint`、`pnpm build`。

## 风险与约束

- 前端目录当前为空，新增文件较多，因此按 SDD 执行。
- 不修改 API 契约，不触碰后端认证逻辑。
- 后续阶段必须继续遵守 `View -> Store -> API Layer -> Backend`。

## 验证命令

```bash
cd frontend
pnpm install
pnpm vitest run
pnpm type-check
pnpm lint
pnpm build
```
