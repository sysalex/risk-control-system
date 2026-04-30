# 完成标准（Definition of Done）

> Harness Engineering 核心原则：**没有明确的完成标准，就没有真正的完成。**
> Agent 在标记任何任务为 `[x]` 之前，必须逐项核对对应的 DoD 清单。

---

## 通用 DoD（所有任务必须满足）

### 规格前置（复杂任务）
- [ ] 已按 `AGENTS.md` 判定 SDD 级别：跳过 / 轻量 SDD / 完整 SDD
- [ ] 已按 `AGENTS.md` 的“上下文防规避规则”重载必要规范、任务清单、交接记录和相关 SDD 产物
- [ ] 轻量 SDD：规格文档位于 `docs/specs/`，计划文档位于 `docs/plans/`
- [ ] 轻量 SDD：规格中已记录“无待确认问题”或已完成必要问答；计划中包含文件计划、TDD 步骤、验证命令
- [ ] 完整 SDD：已完成 Specify → Clarify → Plan → Tasks，用户确认已记录，`task-list.md` 已拆分可验证子任务

### 代码质量
- [ ] Java 编译通过；当前 Maven 未配置 checkstyle/spotless 时，Java 代码规范以代码审查和现有风格一致性核对为准
- [ ] TypeScript/Vue：`eslint` 零错误，`vue-tsc` 类型检查通过
- [ ] 方法长度 ≤ 50 行，文件长度 ≤ 800 行
- [ ] 无硬编码的密钥、URL、魔法数字

### 测试
- [ ] 前端新增代码有对应测试（TDD：先写测试再实现）；后端新增 API 必须提供 HTTP 请求示例文件（`.http` 格式），覆盖正常请求、错误边界、权限差异
- [ ] 测试覆盖率满足分层目标（`vitest --coverage` 前端）；后端以 HTTP 请求示例可复现性替代覆盖率统计
  - 前端（`vitest --coverage`）：≥ 80%
- [ ] 所有现有前端测试仍然通过（无回归）
- [ ] 边界条件和错误路径有测试覆盖（前端）或 HTTP 请求示例覆盖（后端）
- [ ] E2E 测试（Playwright）覆盖核心用户流程，且必须在 headed 模式下通过一次，确保选择器与真实 DOM 匹配
- [ ] E2E 测试必须覆盖真实业务边界条件：删除被引用的资源、修改不存在的记录、重复创建冲突资源、越权操作等，不能仅验证"创建后立即删除"这类 naive path
- [ ] 涉及角色/权限的 E2E 测试，setup 阶段必须断言用户角色正确（如 admin 注册后角色应为 `ADMIN`），不能假设默认分配

### 安全
- [ ] 用户输入经过 DTO + JSR-303 校验注解
- [ ] 无 SQL 拼接，全部使用 MyBatis-Plus（`#{}` 参数化，禁止 `${}`）
- [ ] 敏感操作有权限检查（`@RequireRole` 或拦截器）
- [ ] 无敏感信息泄露到日志或响应

### 文档
- [ ] `docs/task-list.md` 中对应任务状态已更新为 `[x]`
- [ ] 新增 API 端点已更新到 `docs/api-spec.md`
- [ ] 重大架构变更已记录到 `docs/adr/`
- [ ] `CHANGELOG.md` 已更新

### 可观测性
- [ ] 关键操作有日志记录（含 `requestId`、`userId` 等上下文）
- [ ] 错误路径有 `log.error()` 记录
- [ ] 无裸 `System.out.println()` 或 `console.log()`

### 性能基线
- [ ] 后端 API：P99 响应时间 ≤ 500ms（健康检查/静态资源除外）
- [ ] 前端首屏：LCP（Largest Contentful Paint）≤ 2.5s（本地开发环境）
- [ ] 数据库：慢查询阈值 > 200ms 必须记录警告日志并纳入优化 backlog
- [ ] 构建：CI 全量构建 + 测试 ≤ 10 分钟

### 知识沉淀（Compound Engineering）
- [ ] 本次是否发现了新的模式/反模式？
- [ ] 是否有可复用的决策经验或踩坑记录？
- [ ] 如有，更新 `.harness/learnings.md` 或创建/更新 `docs/patterns.md`

---

## 后端 API 模块 DoD（在通用 DoD 基础上）

- [ ] Interfaces 层（Controller）无业务逻辑（只做参数校验 + 响应格式化）
- [ ] Application 层无直接数据库操作
- [ ] Domain 层不依赖具体技术实现（数据库、缓存、MQ）
- [ ] Infrastructure 层不含业务逻辑（只做数据访问）
- [ ] 所有响应使用 `ApiResponse.ok()` / `ApiResponse.fail()` 包装
- [ ] 异常使用 `AppException` 子类，不抛裸 `Exception`
- [ ] 新端点有 HTTP 请求示例文件（`.http` 格式），覆盖正常请求、错误边界（404/409/403 等）、权限差异
- [ ] 端点有正确的 HTTP 状态码（201 创建、404 不存在、403 无权限、409 冲突等）

---

## 前端页面/组件 DoD（在通用 DoD 基础上）

- [ ] 使用 `<script setup>` + Composition API，无 Options API
- [ ] 数据请求通过 Pinia Store，不在组件内直接调用 `http`
- [ ] 加载状态、错误状态、空状态均有处理
- [ ] 表单有客户端校验（Element Plus form rules）
- [ ] 路由守卫覆盖（未登录跳转 `/login`，无权限提示）
- [ ] 响应式布局，最小支持 1280px 宽度

---

## 数据库迁移 DoD

- [ ] 迁移脚本可正向执行（`mvn flyway:migrate`）
- [ ] 迁移脚本可回滚（提供 downgrade SQL 或备份恢复方案；Flyway Community 不支持 `flyway:undo`）
- [ ] 迁移不包含数据操作（结构变更与数据迁移分离）
- [ ] 新表/字段有注释说明用途

---

## 阶段完成 DoD（每个开发阶段结束时）

- [ ] 阶段内所有任务均满足通用 DoD
- [ ] 后端：`mvn compile` 编译通过；HTTP 请求示例文件已补充并验证可执行；存量 JUnit 测试保留但不新增
- [ ] 前端：`pnpm coverage` 全量通过并满足覆盖率目标
- [ ] `git` 提交信息符合 Conventional Commits 格式
- [ ] 代码已推送到远程仓库
- [ ] `CHANGELOG.md` 已记录本阶段变更

---

## DoD 违规处理

Agent 发现以下情况时，**必须停止当前任务并上报**，不得绕过：

| 违规类型 | 处理方式 |
|---------|---------|
| 测试覆盖率低于目标 | 补写测试或记录明确豁免原因，不得无说明标记完成 |
| 编译失败 | 修复类型错误，不得用 `@SuppressWarnings` 掩盖 |
| 安全漏洞（SQL注入/XSS等） | 立即修复，记录到 `docs/adr/` |
| 跨层调用（Controller→Mapper） | 重构，不得以"临时方案"提交 |
| 复杂任务跳过 SDD 直接编码 | 补写规格/计划文档，再进入 TDD |
| 通过少读上下文降低执行级别 | 停止当前任务，重读 `AGENTS.md`、`CLAUDE.md`、`docs/task-list.md`、`session-handoff.md` 和相关 SDD 产物后重新判定 |
