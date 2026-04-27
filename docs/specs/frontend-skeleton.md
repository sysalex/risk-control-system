# 前端骨架规格

## 背景

阶段 0.3 需要补齐前端基础工程，使后续登录、布局、Dashboard 和业务页面可以在统一约束下开发。

## 目标

- 建立 Vue 3 + TypeScript + Vite 工程入口。
- 接入 Vue Router，提供基础路由结构和首页占位。
- 接入 Pinia，提供可复用的状态管理入口。
- 封装 Axios HTTP 客户端，统一读取 `VITE_API_BASE_URL`，保留响应信封类型。
- 建立 Vitest + Vue Test Utils 测试基线，后续组件和 Store 可直接补测试。

## 非目标

- 不实现登录页面、主布局、Dashboard 业务内容和权限路由；这些属于阶段 7。
- 不接入真实后端认证 token 刷新逻辑；本阶段只提供 API 客户端骨架。
- 不引入复杂 UI 主题或页面设计。

## 验收标准

- `pnpm install` 后可运行 `pnpm type-check`、`pnpm lint`、`pnpm vitest run`。
- `pnpm build` 可以生成生产构建。
- 根页面可以挂载 Vue 应用，并通过 Router 渲染首页。
- Pinia Store 可被测试调用。
- API 客户端导出统一 HTTP 实例和信封类型。

## 澄清记录

- 当前任务来自 `docs/task-list.md` 的 `0.3 前端骨架（Vue 3、Pinia、Router、Axios 封装）`。
- 本阶段仅做基础骨架，不提前实现阶段 7 的页面功能。
