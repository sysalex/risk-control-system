# 风控系统 — Harness Engineering 规范

## 项目概述

Web 版风控系统，支持规则引擎、风险事件管理、风险评分、决策流程、审计追踪。

## 技术栈

| 层次 | 技术 |
|------|------|
| 前端 | Vue 3 + TypeScript + Vite + Pinia + Vue Router |
| UI 组件 | Element Plus |
| 后端 | Java 17 + Spring Boot 3.x + MyBatis-Plus 3.5.x |
| 数据库 | MySQL 8.0 |
| ORM | MyBatis-Plus（代码生成器 + 条件构造器） |
| 构建工具 | Maven 3.9+ |
| 认证 | JWT (jjwt) |
| 测试 - 前端 | Vitest + Vue Test Utils |
| 测试 - 后端 | JUnit 5 + Spring Boot Test |
| 包管理 - 前端 | pnpm / npm |
| 包管理 - 后端 | Maven |

## 本地开发环境配置

**前端端口**: `5173`（固定）
**后端端口**: `8080`（固定）

详细配置（端口清理、镜像源、MySQL、本机特殊路径、种子数据）见 `docs/dev-setup.md`。

```env
# frontend/.env（不提交到 Git）
VITE_API_BASE_URL=http://localhost:8080
```

## 目录结构

```
harness-agent/
├── CLAUDE.md                    # 本文件：Harness 核心规范
├── AGENTS.md                    # Agent 行为指令（工作流程、协作模式）
├── session-handoff.md           # 会话交接记录
├── CHANGELOG.md                 # 版本变更记录
├── .claude/
│   ├── settings.json            # Hooks 和权限配置
│   └── settings.local.json      # 本地覆盖（不提交）
├── .harness/
│   └── learnings.md             # 经验与踩坑记录
├── .github/
│   └── pull_request_template.md # PR 模板
├── docs/
│   ├── README.md                # 文档导航
│   ├── architecture.md          # 系统架构设计
│   ├── api-spec.md              # API 接口规范
│   ├── domain-model.md          # 领域模型
│   ├── definition-of-done.md    # 完成标准
│   ├── feedback-loop.md         # 四层反馈循环
│   ├── task-list.md             # 开发任务清单
│   ├── security-checklist.md    # OWASP 安全检查清单
│   ├── harness-checklist.md     # Harness 自检清单
│   ├── autonomy-levels.md       # Agent 自治边界
│   ├── invariants-and-guardrails.md  # 不可破坏约束
│   ├── tech-debt.md             # 技术债务追踪
│   ├── patterns.md              # 模式/反模式库（知识沉淀）
│   ├── specs/                   # 功能规格文档（SDD 产物）
│   ├── plans/                   # 实现计划文档（SDD 产物）
│   ├── adr/                     # 架构决策记录
│   ├── checklists/              # 检查清单
│   │   └── change-preflight.md  # 变更前预检
│   ├── retrospectives/          # 阶段回顾
│   └── coverage/                # 测试覆盖率报告
├── frontend/                    # Vue 3 前端
│   ├── src/
│   │   ├── api/                 # API 请求层
│   │   ├── components/          # 通用组件
│   │   ├── views/               # 页面视图
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── router/              # 路由配置
│   │   ├── types/               # TypeScript 类型定义
│   │   └── utils/               # 工具函数
│   ├── tests/                   # 前端测试
│   └── vite.config.ts
├── backend/                     # Java Spring Boot 后端（Maven 多模块）
│   ├── pom.xml                  # 父 POM（dependencyManagement + modules）
│   ├── risk-common/             # 公共组件（异常、响应、配置、DTO）
│   │   ├── pom.xml
│   │   └── src/main/java/com/harness/risk/common/
│   ├── risk-domain/             # 领域层（实体、领域服务、值对象）
│   │   ├── pom.xml
│   │   └── src/main/java/com/harness/risk/domain/
│   ├── risk-infrastructure/     # 基础设施层（Mapper、XML 映射）
│   │   ├── pom.xml
│   │   └── src/main/java/com/harness/risk/infrastructure/
│   ├── risk-application/        # 应用层（用例编排、事务管理）
│   │   ├── pom.xml
│   │   └── src/main/java/com/harness/risk/application/
│   ├── risk-interfaces/         # 接口层（Controller、拦截器）
│   │   ├── pom.xml
│   │   └── src/main/java/com/harness/risk/interfaces/
│   ├── risk-starter/            # 启动层（入口、配置、resources）
│   │   ├── pom.xml
│   │   └── src/main/java/com/harness/risk/starter/
│   └── .env.example
├── scripts/
│   ├── check.sh                 # Bash 质量门禁脚本
│   └── check.ps1                # Windows PowerShell 质量门禁脚本
└── docker-compose.yml           # 本地开发环境
```

## 架构约束（Agent 必须遵守）

### 分层架构（后端）— COLA 模式

依赖方向严格单向，由 Maven 模块依赖强制：

```
starter → interfaces → application → infrastructure → domain
                └───────────── Common（跨层共享）
```

**Maven 模块结构**：

| 模块 | artifactId | 职责 | 依赖 |
|------|-----------|------|------|
| **Starter** | `risk-starter` | 入口、全局配置、资源配置 | interfaces |
| **Interfaces** | `risk-interfaces` | Controller、拦截器 | application + web |
| **Application** | `risk-application` | 用例编排、事务管理 | infrastructure |
| **Domain** | `risk-domain` | 实体、纯业务规则、领域服务 | common |
| **Infrastructure** | `risk-infrastructure` | Mapper、XML 映射、外部服务 | domain + MyBatis-Plus |
| **Common** | `risk-common` | 异常、响应、DTO | web + validation |

### MyBatis-Plus 约定

- 实体类使用 `@TableName`、`@TableId`、`@TableField` 注解
- Mapper 接口继承 `BaseMapper<T>`，复杂查询使用 `QueryWrapper` / `LambdaQueryWrapper`
- 禁止在 Service 层直接使用 `SqlSession`，必须通过 Mapper
- 分页查询使用 `Page<T>` + `IPage<T>`

### 前端架构

```
View → Store (Pinia) → API Layer → Backend
```

- View 只负责渲染和用户交互
- 所有状态通过 Pinia Store 管理
- API 调用统一封装在 `src/api/` 目录

### 数据对象原则

- 后端：DTO 使用 `class + @Data + @NoArgsConstructor + @AllArgsConstructor`，字段注释和校验注解写在字段上
- 前端：Store 中的状态通过 action 更新，不直接修改

### API 响应格式（统一信封）

```json
{
  "success": true,
  "data": {},
  "message": null,
  "meta": { "total": 0, "page": 1, "limit": 20 }
}
```

## 编码规范

### Java / Spring Boot

- Java 版本：Java 17（`backend/pom.xml` 的 `<java.version>` 为 17；本机脚本可使用 IDEA JDK 21 运行 Maven）
- Spring Boot 版本：3.x
- 类型：所有方法必须有完整类型注解
- 命名：变量/方法 camelCase，类名/接口名 PascalCase，常量 UPPER_SNAKE_CASE
- 实体类统一放在 `domain.model.entity` 包下，命名格式为 `XxxEntity`，例如 `UserEntity`、`RiskRuleEntity`
- 枚举类统一放在 `enums` 包下，命名格式为 `XxxEnums`，例如 `UserRoleEnums`、`RiskLevelEnums`
- **Starter 层**：`@SpringBootApplication` 入口、`@MapperScan`、配置类（Cors、Mvc、Redis 等）
- **Interfaces 层**：`@RestController` + `@RequestMapping`，只做参数校验和响应格式化
- **Application 层**：`@Service`，用例编排、`@Transactional` 事务管理
- **Domain 层**：`@Service`（Domain Service），纯业务规则；Entity 使用 `@TableName`、`@TableId`
- **Infrastructure 层**：Mapper 继承 `BaseMapper<T>`，XML 映射文件放 `resources/mapper/`
- 异常处理：`@ControllerAdvice` + `@ExceptionHandler` 全局处理
- 日志：`@Slf4j`（Lombok），禁止 `System.out.println()`
- 异步：`@Async` + `CompletableFuture`，禁止阻塞主线程

### Java 注释规范（Javadoc）

- 领域模型、领域服务、Application Service、Controller 和对外公共工具类必须有 `/** */` 类级 Javadoc，包含职责描述、`@author harness-agent`、`@since YYYY-MM-DD`
- public 方法在以下场景必须有 Javadoc：对外 API、复杂业务规则、非显然边界条件、被跨模块复用的工具方法
- “实体类”特指 `risk-domain` 下 `domain.model.entity` 包中的 `XxxEntity` 类；实体类类级 Javadoc 必须写明职责，字段必须逐项添加 `/** */` 块级 Javadoc
- `application.dto` 下的 `*Request` / `*Response` DTO 对外承载接口契约，类级 Javadoc 必须包含职责描述、`@author harness-agent`、`@since YYYY-MM-DD`
- DTO 字段必须逐项添加 `/** */` 块级 Javadoc，格式与实体字段注释保持一致；复杂字段的示例和值域同步写入 `docs/api-spec.md`
- DTO 使用 `class + @Data + @NoArgsConstructor + @AllArgsConstructor`；如确需不可变对象，必须先更新规范并说明原因
- 简单测试类、配置类、私有辅助方法可不写 Javadoc，避免低信息量注释
- 内部类/嵌套类按其可见性和职责适用上述规则
- 方法内部的代码注释使用 `//` 单行格式，仅在解释原因、边界、约束或非显然决策时添加
- 注释使用中文，描述职责和用途，不描述实现细节
- 禁止无信息量的注释（如 `// 构造函数`、`// 设置值`）

### TypeScript / Vue

- 严格模式：`strict: true`
- Composition API + `<script setup>` 语法
- 组件命名：PascalCase
- 文件组织：按功能模块，不按文件类型

### 通用编码约束

- 方法长度 ≤ 50 行，文件长度 ≤ 800 行
- 禁止无信息量的注释（如 `// 构造函数`、`// 设置值`）

## 安全规范

- 所有 API 端点（除登录/注册）必须 JWT 认证（`@JwtInterceptor`）
- JWT 拦截器必须对 `OPTIONS` 预检请求直接放行，否则浏览器 CORS 机制会失效
- 密码使用 BCrypt 哈希，禁止明文存储
- SQL 操作全部通过 MyBatis-Plus，禁止 `${}` 拼接 SQL（使用 `#{}` 参数化）
- 敏感配置通过环境变量或 `@Value` 注入，禁止硬编码
- 风控规则变更需审计日志记录（谁、何时、改了什么）
- 风险评分/决策结果不可被未授权用户修改

## 测试要求

- 后端覆盖率目标 ≥ 80%（当前 Jacoco 生成报告，阈值需人工核对；自动阈值见技术债务）
- 前端覆盖率 ≥ 80%（vitest --coverage）
- E2E 测试覆盖核心用户流程（Playwright）
- 新功能必须先写测试（TDD）

### 测试框架

| 测试类型 | 框架 | 配置 |
|---------|------|------|
| 后端单元测试 | JUnit 5 + Mockito | `backend/pom.xml` |
| 后端集成测试 | Spring Boot Test + @SpringBootTest | `backend/pom.xml` |
| 前端组件测试 | Vitest + Vue Test Utils | `frontend/vite.config.ts` |
| E2E 测试 | Playwright | `frontend/playwright.config.ts` |

## 领域模型（核心实体）

- **User**：用户（管理员/风控分析师/操作员）
- **RiskRule**：风控规则（规则条件、动作、优先级）
- **RuleEngine**：规则引擎（执行规则匹配）
- **RiskEvent**：风险事件（触发记录、级别、状态）
- **RiskScore**：风险评分（评分维度、结果）
- **Decision**：决策记录（通过/拒绝/人工审核）
- **AuditLog**：审计日志（所有关键操作记录）

## 日志规范

详见 `AGENTS.md`。核心要求：
- 后端：使用 `@Slf4j` 注解，`log.info()` / `log.error()`，禁止 `System.out.println()`
- 前端：`import { logger } from '@/utils/logger'`，禁止 `console.log()`
- 生产环境：后端 INFO / 前端 warn+error

## 异常处理规范

详见 `AGENTS.md`。核心要求：
- 所有业务异常继承 `common.exception.AppException`
- 使用 `@ControllerAdvice` + `@ExceptionHandler` 全局处理，禁止在 Controller 层重新包装异常
- 禁止吞掉异常（空 catch 块）

## 完成标准（DoD）

**任何任务在标记 `[x]` 之前，必须逐项核对 `docs/definition-of-done.md` 对应清单。**

核心要求：
- 测试覆盖率目标 ≥ 80%，所有测试通过；当前后端覆盖率阈值未自动 fail，必须人工核对报告
- lint + 编译零错误
- 关键操作有日志（含 requestId）
- `docs/task-list.md` 状态已更新，`CHANGELOG.md` 已更新

## 可观测性

详见 `AGENTS.md` 可观测性基线。核心要求：
- 每个请求通过 `HandlerInterceptor` 注入 `requestId`，响应头返回 `X-Request-ID`
- 慢请求（> 1s）自动记录 warning 日志
- 运行时指标：`GET /api/v1/metrics`（请求数、错误率、平均响应时间）
- 前端全局错误捕获并上报（`setupErrorReporting`）

## 循环机制

四层反馈循环，详见 `docs/feedback-loop.md`：
- **L1 工具级**：Claude 环境按 `.claude/settings.json` 触发 Hook；Codex/PowerShell 环境以手动验证命令和 `scripts/check.ps1` 为准
- **L2 任务级**：写测试 → 实现 → 测试通过 → DoD 核查 → 标记完成
- **L3 会话级**：Claude Stop Hook 运行轻量检查和 DoD 提醒；非 Claude 环境需要手动运行质量门禁
- **L4 阶段级**：阶段完成后在 `docs/retrospectives/` 创建回顾文档

## AI Agent 行为规范

详见 `AGENTS.md`。核心要点：
- 操作纪律：不猜测文件路径、不写 TODO 注释、修改后必须验证
- 编码纪律：禁止未通过测试的代码、禁止 `@SuppressWarnings` 掩盖错误
- 决策透明：架构变更必须记录 ADR，技术债务必须登记

## 分支策略

```
main (稳定)
 └── stage/0-framework
 └── stage/1-database-models
 └── stage/2-auth-users
 └── stage/3-rule-management
 └── ...
```

- `main`: 稳定分支，只能通过 PR 合并，禁止直接 push
- `stage/N-xxx`: 每个阶段一个特性分支
- 分支命名格式：`stage/<阶段号>-<简短描述>`
- 合并前必须通过质量门禁（lint + 测试 + DoD 核查）
- 提交信息格式：`<type>(scope): <description>`（Conventional Commits）
  - type: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
  - scope: 模块名，如 `auth`, `rules`, `events`, `frontend`
  - 示例: `feat(rules): add rule enable/disable endpoint`

## 任务追踪

- 所有开发任务在 `docs/task-list.md` 中登记
- Agent 开始工作前必须查阅任务清单，确认当前任务
- 完成任务后更新状态为 `[x]`，同步更新 `CHANGELOG.md`

## Hooks 自动化

`.claude/settings.json` 仅适用于 Claude Code 环境；Codex/PowerShell 环境不保证触发这些 Hook。

当前可依赖的验证入口：
- Windows：`powershell -ExecutionPolicy Bypass -File scripts/check.ps1`
- 类 Unix：`bash scripts/check.sh`
- 后端：`cd backend && mvn test`
- 前端：`cd frontend && pnpm type-check && pnpm lint && pnpm coverage && pnpm build`

当前自动化边界：
- Claude Hook 可拦截部分危险 Bash 命令和不合规提交信息
- Claude Hook 可在会话结束时运行轻量编译/类型检查提醒
- Java 的 checkstyle/spotless 尚未在 Maven 中配置，不应视为已自动执行

---

# CLAUDE Quick Start

首读顺序：
1. 本文件前 20 行
2. `docs/task-list.md`
3. `docs/invariants-and-guardrails.md`
4. `docs/definition-of-done.md`
5. `docs/feedback-loop.md`
6. `docs/README.md`

建议把本文件当作入口，而不是把所有执行细节都放在这里。

专项文档：
- `AGENTS.md` — Agent 工作流和协作规范
- `docs/autonomy-levels.md` — Agent 自治边界
- `docs/checklists/change-preflight.md` — 变更前预检
- `docs/invariants-and-guardrails.md` — 不可破坏约束
- `docs/README.md` — 文档导航
- `docs/security-checklist.md` — 安全检查
- `docs/harness-checklist.md` — Harness 自检
