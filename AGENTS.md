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
7. **任务收尾默认提交**：验证通过后自动 `commit + push`，除非用户明确禁止

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
| 跳过 SDD | 影响文件 ≤ 5，且不涉及架构/API/schema/安全边界 | 直接 TDD | 否 |
| 轻量 SDD | 影响文件 > 5，但需求明确、边界单一、无破坏性变更 | `docs/specs/<feature>.md` + `docs/plans/<feature>.md` | 仅存在待确认问题时 |
| 完整 SDD | 架构变更、跨阶段/跨端联动、API 契约变更、认证/授权/安全边界、破坏性 schema 变更、需求存在歧义 | Specify → Clarify → Plan → Tasks → TDD | 是 |

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

### 强制触发（MUST）

| 场景 | Agent | 触发时机 | 跳过条件 |
|------|-------|---------|---------|
| 新功能/Bug 修复 | tdd-guide | 开始编码前 | 仅修改文档/配置 |
| 代码已写入/修改 | code-reviewer | 写入后立即 | 无 |
| 涉及认证/授权/输入 | security-reviewer | 写入后立即 | 无 |
| 构建失败 | build-error-resolver | 构建失败时 | 无 |
| 复杂功能（>5 文件） | planner | 开始前 | 用户明确拒绝 |

### 推荐触发（SHOULD）

| 场景 | Agent | 触发时机 |
|------|-------|---------|
| 架构决策 | architect | 设计阶段 |
| 关键用户流程 | e2e-runner | 功能完成后 |
| 代码维护 | refactor-cleaner | 阶段完成后 |
| 文档更新 | doc-updater | 代码变更后 |

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

1. **代码质量**：测试通过、覆盖率 ≥ 80%、lint 零错误、编译通过
2. **代码审查**：code-reviewer 已运行、CRITICAL/HIGH 已修复、security-reviewer（如适用）
3. **安全检查**：OWASP Top 10 通过、无敏感信息泄露、权限校验到位
4. **文档更新**：task-list.md 状态已更新、CHANGELOG.md 已更新、API 文档已更新（如适用）
5. **知识沉淀**：是否有新模式/反模式/踩坑记录？有则更新 `.harness/learnings.md` 或 `docs/patterns.md`

### 默认收尾顺序

```
运行验证 → 更新 task-list.md → 更新 CHANGELOG.md → 更新 handoff/learnings
→ git status 自检 → git commit → git push
```

除非用户要求暂停，否则必须走完全部 7 步。

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

---
