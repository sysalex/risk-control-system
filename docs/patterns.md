# 模式/反模式库

> 每次工程工作应该让下一次更容易。这里记录从实际开发中提炼的可复用经验。
> 来源：每个任务完成后的知识沉淀环节（Compound Engineering）。

---

## 模式（Patterns）

<!-- 已验证有效的做法，格式：标题 + 场景 + 做法 + 为什么有效 -->

### 前端骨架阶段避免全量注册 UI 组件库

- **场景**：Vue/Vite 骨架阶段还没有实际使用 Element Plus 组件
- **做法**：保留依赖但不在 `main.ts` 全局 `app.use(ElementPlus)`，后续页面按需引入
- **为什么有效**：避免首屏构建包被未使用组件库放大，减少 Vite chunk 体积警告

### [示例] 统一响应信封拦截器

- **场景**：所有 API 返回 `{ success, data, message, meta }` 格式
- **做法**：Axios 响应拦截器自动解包 `data.data`，错误拦截器统一处理 `data.message`
- **为什么有效**：前端业务代码无需关心信封结构，直接拿到业务数据

---

## 反模式（Anti-Patterns）

<!-- 踩过的坑，格式：标题 + 症状 + 根因 + 正确做法 -->

### [示例] 在 Controller 层写业务逻辑

- **症状**：Controller 方法直接调 Mapper，包含条件判断和数据转换
- **根因**：贪图方便，跳过了 Application/Domain 层
- **正确做法**：Controller 只做参数校验和响应格式化，业务逻辑放入 Application 或 Domain Service

---

## 编码规范

### Domain Service 命名与包结构

- **场景**：COLA 架构中 Application Service 和 Domain Service 并存，容易混淆
- **做法**：
  - Domain Service 接口必须以 `DomainService` 结尾，如 `RuleDomainService`、`RiskScoreDomainService`
  - 接口放在 `com.harness.risk.domain.service` 包下
  - 实现类放在 `com.harness.risk.domain.service.impl` 子包中，如 `RuleDomainServiceImpl`
- **为什么有效**：命名上严格区分 Application Service（`AuthService`、`UserService`）和 Domain Service（`RuleDomainService`），避免注入时混淆

---

## 决策经验

<!-- 关键技术决策的记录，格式：决策 + 备选 + 选择理由 -->

---

> **维护规则**：阶段回顾（L4 反馈循环）时，负责 Agent 检查 `docs/patterns.md` 中同类模式/反模式是否超过 3 条；超过则合并同类项或提炼为更通用的规则，避免文档膨胀。合并记录写入该阶段回顾文档（`docs/retrospectives/`）。
