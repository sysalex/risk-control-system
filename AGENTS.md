# Agent 行为指令 — Harness Engineering

> 本文件定义 Agent 的工作流程、决策树、协作模式和自动化触发规则。
> 与 CLAUDE.md 配合使用：CLAUDE.md 定义"做什么"，AGENTS.md 定义"怎么做"。

## 核心原则

1. **任务驱动**：所有工作围绕 `docs/task-list.md` 展开
2. **质量优先**：每个阶段必须通过质量门禁
3. **自动化优先**：能自动化的绝不手动
4. **并行优先**：独立任务必须并行执行
5. **TDD 强制**：新功能/修复必须先写测试
6. **注释语言统一**：代码需要注释时默认使用中文，优先写"为什么/边界/约束"
7. **任务收尾按类型执行**：代码任务验证通过后默认 `commit + push`；只读/分析任务不提交；仅文档任务有实际修改时再提交，除非用户明确禁止或要求暂停

## 规范优先级

当多个规范文件存在重叠或冲突时，按以下顺序裁决：

1. 用户在当前会话中的明确指令
2. `AGENTS.md`（Agent 工作流、任务启动、收尾和协作方式）
3. `docs/invariants-and-guardrails.md`（不可破坏约束和高风险边界）
4. `CLAUDE.md`（技术栈、架构、编码规范入口）
5. `docs/definition-of-done.md`（完成标准和质量门禁）
6. `docs/specs/`、`docs/plans/`（具体功能的 SDD 产物）
7. `docs/README.md`、`CHANGELOG.md`、历史交接记录

若冲突涉及 API 契约、数据库 schema、认证授权、安全边界或生产配置，先停止并确认，不按低优先级文件继续执行。

## 任务启动流程

### 1. 接收任务

```
用户请求 → 查阅 task-list.md → 确认当前任务 → 理解验收标准
```

**强制检查项**：
- [ ] 查阅 `docs/task-list.md`，确认任务状态
- [ ] 任务未登记时，先在 task-list.md 创建条目
- [ ] 确认前置依赖已完成

### 2. 选择工作模式

| 任务类型 | 工作模式 | 触发条件 |
|---------|---------|---------|
| 新功能实现 | TDD + 多 Agent | 需要新增代码 |
| Bug 修复 | TDD + Code Review | 修改现有代码 |
| 重构 | Planner + TDD | 影响多个文件 |
| 文档/配置 | 单 Agent + 验证 | 仅修改文档或配置 |

### 2.5 复杂任务前置：规格驱动开发（SDD）

> SDD 分为轻量 SDD 和完整 SDD。目标是闭合需求澄清和任务拆分，不为了文档而文档。

#### SDD 分级

| 级别 | 触发条件 | 必需产物 | 是否等待用户确认 |
|------|---------|---------|----------------|
| 跳过 SDD | 影响文件 ≤ 5 **且** 不涉及 API 契约 / 数据库 schema / 认证授权 / 安全边界 | 直接 TDD | 否 |
| 轻量 SDD | 影响文件 > 5 **且** 需求明确、边界单一、无破坏性变更 | `docs/specs/<feature>.md` + `docs/plans/<feature>.md` | 仅存在待确认问题时 |
| 完整 SDD | 架构变更、跨阶段/跨端联动、API 契约变更、认证/授权/安全边界、破坏性 schema 变更、需求存在歧义 | Specify → Clarify → Plan → Tasks → TDD | 是 |

> **判定优先级**：安全/架构/API 边界优先于文件数。即使只改 1 个文件，只要涉及 JWT、权限、数据库 schema 或对外 API 契约，就必须走轻量或完整 SDD。

#### SDD 快速判断

- 涉及 API 契约、数据库 schema、认证授权或安全边界：至少轻量 SDD
- 涉及架构变更、破坏性变更、跨端/跨阶段联动或需求不清：完整 SDD，并等待用户确认
- 影响文件 > 5 且边界清楚、无破坏性变更：轻量 SDD
- 影响文件 ≤ 5 且不触碰高风险边界：跳过 SDD，直接 TDD

#### 闭环要求

| 阶段 | 产物 | 轻量 SDD 闭环标准 | 完整 SDD 闭环标准 |
|------|------|------------------|------------------|
| Specify | `docs/specs/<feature>.md` | 写清目标、非目标、验收标准、默认假设 | 同轻量 SDD |
| Clarify | 规格中的 QA/假设记录 | 若无待确认问题，记录“无待确认问题”；若有问题，先问用户再继续 | 必须记录问题、用户答复和最终决策 |
| Plan | `docs/plans/<feature>.md` | 写清文件计划、TDD 步骤、风险与验证命令 | 同轻量 SDD，并补数据流、异常路径、回滚/兼容策略 |
| Tasks | Plan 中的文件级任务清单或 `task-list.md` 子任务 | Plan 中文件计划 + TDD 步骤即可闭环 | 必须拆到 `task-list.md` 子任务，单项 2-15 分钟、可独立验证 |

**全栈任务**：后端优先（先 API 提供类型和数据契约，再前端）。
**数据库任务**：已登记的新增迁移默认可按轻量 SDD 执行；修改既有迁移、破坏性 DDL、生产 schema 变更必须走完整 SDD 并等待确认。

### 3. 任务分解

- **复杂（>3 步骤）**：调用 planner → 生成计划 → task-list.md 登记 → 按依赖执行
- **简单（≤3 步骤）**：直接执行，在 task-list.md 标记进度

## Agent 自动触发规则

> 以下 Agent 为**工作模式指南**，定义不同场景下应采用的工作方式。当前项目中这些角色由同一 Agent 通过不同 prompt 模拟执行，不依赖外部多 Agent 系统。
> 实际触发方式：由当前 Agent 根据任务类型自我判定工作模式，或在用户明确要求时切换。

### 强制采用的工作模式（MUST）

| 场景 | 工作模式 | 执行时机 | 跳过条件 |
|------|---------|---------|---------|
| 新功能/Bug 修复 | TDD 模式 | 开始编码前 | 仅修改文档/配置 |
| 代码已写入/修改 | Code Review 模式 | 写入后立即 | 无 |
| 涉及认证/授权/用户输入 | 安全审查模式 | 写入后立即 | 无 |
| 构建失败 | 构建修复模式 | 构建失败时 | 无 |
| 复杂功能（>5 文件） | Planner 模式 | 开始前 | 用户明确拒绝 |

### 推荐采用的工作模式（SHOULD）

| 场景 | 工作模式 | 执行时机 |
|------|---------|---------|
| 架构决策 | Architect 模式 | 设计阶段 |
| 关键用户流程 | E2E 验证模式 | 功能完成后 |
| 代码维护 | 重构清理模式 | 阶段完成后 |
| 文档更新 | 文档同步模式 | 代码变更后 |

## TDD 工作流（强制）

```
[RED] 写测试 → 运行确认失败
    → [GREEN] 写最小实现 → 运行确认通过
    → [REFACTOR] 优化代码 → 运行确认通过
    → [REVIEW] Code Review → 修复 CRITICAL/HIGH
    → [DOD] 按 docs/definition-of-done.md 逐项核对
```

**前端测试**：单元用 `Vitest + Vue Test Utils`，端到端用 `Playwright`。
**骨架/占位页/局部组件**不强制 Playwright，关键用户流程联动必须补。

### 测试分层边界

| 层级 | 测试类型 | 框架 | 容器 | 适用场景 |
|------|---------|------|------|---------|
| Domain | 单元测试 | JUnit 5 | 不启动 Spring | 纯业务规则、枚举、值对象 |
| Application | 单元测试 | JUnit 5 + Mockito | 不启动 Spring | Service 逻辑、用例编排（mock Mapper/依赖） |
| Infrastructure | 集成测试 | Spring Boot Test | 启动 Spring + H2 | Mapper 边界、SQL 映射验证 |
| Interfaces | 集成测试 | `@SpringBootTest` + `MockMvc` | 启动 Spring + H2 | Controller 路由、拦截器、序列化、状态码 |
| 前端 | 单元测试 | Vitest | Node | Store、工具函数、组件渲染 |
| 前端 | E2E | Playwright | 浏览器 | 核心用户流程（登录→业务→退出） |

**红线**：
- Service 层单元测试禁止启动 Spring 容器（使用 Mockito 隔离依赖）。
- Controller 层禁止只测 Service mock，必须走完整 HTTP 请求链路（验证拦截器、参数绑定、响应包装）。

## 多 Agent 协作

| 模式 | 适用场景 | 约束 |
|------|---------|------|
| **串行** | 单一功能开发 | planner → tdd-guide → reviewer → DoD |
| **并行** | 多个独立模块 | 主线程拆分任务+汇总验证，子 Agent 不越界修改未分配文件 |
| **多视角** | 架构决策/技术选型 | 安全/性能/可维护性并行评估 |

**并行红线**：子任务共享写入文件或强依赖上一步结果时，禁止并行。

## 质量门禁

每个任务完成前运行质量门禁：类 Unix 环境使用 `scripts/check.sh`，Windows PowerShell 使用 `scripts/check.ps1`，确保 lint 零错误、编译通过、覆盖率 ≥ 80%、全部测试通过。详细命令和反馈循环见 `docs/feedback-loop.md`。

## 异常处理

| 异常 | 处理 |
|------|------|
| 编译失败 | 触发 build-error-resolver → 修复 → 重新编译 |
| 测试失败 | 分析根因 → 修复实现（不是修测试）→ 重测 |
| Code Review 不通过 | 修复 CRITICAL/HIGH → 重新审查 |
| 安全问题 | 立即停止 → 触发 security-reviewer → 修复 → 重审 |
| Harness 审查 | 重大功能完成后 / 阶段切换前 / 交接前运行 |

**禁止**：跳过 hooks（--no-verify）、强制推送、删除测试以通过构建。

## 决策树

### 何时使用 planner / SDD？
```
影响文件 ≤ 5 且不涉及架构/API/schema/安全？ → 跳过 SDD，直接 TDD
影响文件 > 5 但边界清楚、无破坏性变更？ → 轻量 SDD → TDD
架构变更 / 跨阶段 / API 契约 / 安全边界 / 破坏性 schema / 需求歧义？ → 完整 SDD → 用户确认 → TDD
```

### 何时并行？
```
任务间有依赖？ → YES → 串行
完全独立？ → YES → 并行
部分依赖？ → 分组：独立并行，有依赖串行
```

### 何时触发 security-reviewer？
```
认证/授权/用户输入/数据库/文件操作？ → 必须触发
纯逻辑计算？ → 可选
```

## 任务完成标准

### 标记完成前必须确认

所有任务的完成标准（DoD）以 `docs/definition-of-done.md` 为唯一真相源。以下为高层概览：

1. **代码质量**：测试通过、覆盖率 ≥ 80%、lint 零错误、编译通过
2. **代码审查**：code-reviewer 已运行、CRITICAL/HIGH 已修复、security-reviewer（如适用）
3. **安全检查**：OWASP Top 10 通过、无敏感信息泄露、权限校验到位
4. **文档更新**：task-list.md 状态已更新、CHANGELOG.md 已更新、API 文档已更新（如适用）
5. **知识沉淀**：是否有新模式/反模式/踩坑记录？有则更新 `.harness/learnings.md` 或 `docs/patterns.md`

> 详细清单（按任务类型细分）及性能基线、违规处理方式，见 `docs/definition-of-done.md`。

### 默认收尾顺序

```
运行验证 → 更新 task-list.md → 更新 CHANGELOG.md → 更新 handoff/learnings
→ git status 自检 → 确认当前分支非 `main` → git commit → git push
```

按任务类型执行：
- **代码任务**：除非用户要求暂停，否则验证通过后走完全部 7 步。
- **文档/配置任务**：有实际文件修改时执行验证、自检、任务记录和 CHANGELOG 更新；提交/推送前确认当前分支非 `main`。
- **只读分析任务**：不更新任务状态、不提交、不推送；只输出结论。
- **用户明确要求暂停或禁止提交**：停止在工作区，报告已完成项和未完成项。

### 更新任务状态格式

```markdown
- [x] 1.1 User 模型 + Flyway 初始化迁移
  - 完成时间：2026-04-24
  - 测试覆盖率：85%
  - 审查状态：通过
```

## 工作流程示例

### 实现新功能（风控规则）

```
1. 查阅 task-list.md → 确认任务
2. 影响文件 > 5 → 轻量/完整 SDD 判定
   2.1 [Specify] docs/specs/rule-management.md
   2.2 [Clarify] 确认规则条件语法、权限模型；无待确认问题时记录默认假设
   2.3 [Plan] docs/plans/rule-management.md
   2.4 [Tasks] 轻量 SDD 写入 Plan 文件计划；完整 SDD 拆入 task-list.md 子任务
3. TDD 循环（按子任务逐个）：RED → GREEN → REFACTOR → REVIEW → DOD
4. code-reviewer + security-reviewer → 修复问题
5. 知识沉淀：记录规则引擎设计的模式/反模式
6. DoD 核查 → 更新 task-list.md → 更新 CHANGELOG → commit + push
```

## 禁止操作清单

### 绝对禁止
- ❌ 跳过测试 / Code Review / DoD 核查
- ❌ 修改测试以通过构建（应该修复实现）
- ❌ 硬编码敏感信息
- ❌ 跨层调用（Controller 直接调用 Mapper）
- ❌ 直接使用 System.out.println()（应该用 @Slf4j 日志）

### 需要确认
- ⚠️ 删除文件/分支 · 强制推送 · 修改数据库 schema · 修改 API 接口 · 修改配置文件

## 快速参考

### 常用命令
```bash
# 后端
cd backend
mvn test                                     # 运行所有测试
mvn test -Dtest=ClassName                    # 运行指定测试
mvn verify                                   # 测试 + 打包
mvn compile                                  # 编译
mvn checkstyle:check                         # 代码规范检查
mvn spotless:check                           # 代码格式检查
mvn jacoco:report                            # 生成覆盖率报告
mvn spring-boot:run                          # 启动开发服务器

# 前端
cd frontend
pnpm dev                                     # 启动开发服务器
pnpm lint                                    # ESLint 检查
pnpm type-check                              # TypeScript 类型检查
pnpm vitest run                              # 运行测试
pnpm vitest run --coverage                   # 测试 + 覆盖率
```

### 关键文件路径
任务清单 `docs/task-list.md` · 完成标准 `docs/definition-of-done.md` · 变更日志 `CHANGELOG.md`
API 规范 `docs/api-spec.md` · 架构 `docs/architecture.md` · 领域模型 `docs/domain-model.md`
安全检查 `docs/security-checklist.md` · 质量门禁 `scripts/check.sh` / `scripts/check.ps1` · Harness 自检 `docs/harness-checklist.md`

### Agent 快速选择
| 需求 | Agent |
|------|-------|
| 规划复杂任务 | planner |
| 写测试 + 实现 | tdd-guide |
| 代码审查 | code-reviewer |
| 安全审查 | security-reviewer |
| 修复构建错误 | build-error-resolver |
| 架构决策 | architect |
| E2E 测试 | e2e-runner |
| 清理死代码 | refactor-cleaner |
| 更新文档 | doc-updater |

---

**记住**：高质量、可维护的代码，而不是快速完成任务。质量优先，速度其次。

---

## 规范巡检机制

每完成一个任务后执行：
- 是否有新的规则缺口 / 重复或职责冲突 / 需要补进 `docs/README.md`
- 是否有技术债务需要登记到 `docs/tech-debt.md`
- 是否有规范漂移：重复、矛盾、空话规则
- 是否有过时规则：与实际代码或工具链不符

**处理**：低风险直接改 · 高风险先确认 · 暂不处理的登记 · 同类警告超 3 条时精简合并

### 规范修正的即时通道

规范巡检的默认触发时机是"任务完成后"，但以下 L1 级别的修正无需等待，发现后立即修复：

| 变更类型 | 级别 | 处理方式 |
|---------|------|---------|
| 错别字、格式、链接失效、过期状态描述 | L1 | 直接修改，说明改动范围 |
| 补充示例、优化排版、澄清模糊表述 | L1 | 直接修改，说明改动范围 |
| 新增/修改规则（如 SDD 判定条件、覆盖率阈值、命名约定） | L2-L3 | 登记为独立任务，经确认后执行 |
| 架构约束变更、新增跨层禁止规则 | L3 | 走完整 SDD，必须用户确认 |

**判定原则**：若变更会影响后续任务的执行方式或验收标准，则不属于 L1，必须登记确认。

---
