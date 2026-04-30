# Changelog

所有重要变更记录在此文件，格式遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)。

版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

---

## [Unreleased]

### Security
- 修复 BCrypt cost factor 从 10 提升至 12（`PasswordEncoder.java`）

### Added
- 质量保障（Stage 10）：
  - 后端测试基础设施：`BaseApiIntegrationTest` 基类统一提供 MockMvc、ObjectMapper、JwtUtil 和常用 helper
  - 端到端 API 链路测试：`EndToEndApiTest` 覆盖登录→me→规则列表、未认证 401、无权限 403、规则 CRUD 生命周期、事件创建→查询→解决、评分 evaluate→subject 查询、操作员无法访问审计日志
  - risk-common 覆盖率提升：`ApiResponseTest`（6 用例）+ `AppExceptionTest`（6 用例）
  - 前端 E2E 测试框架：Playwright + Chromium 配置，`playwright.config.ts` + `e2e/auth.setup.ts` + `e2e/core-flow.spec.ts`
  - E2E 核心流程覆盖：登录（成功/失败）、规则管理增删改查启停、风险事件创建与解决、评分决策查看与创建、审计日志列表、未登录重定向
  - 安全审查报告：`docs/security-audit-report.md`，按 OWASP Top 10 逐项核对，发现并修复 BCrypt cost 不足问题
  - 性能基准报告：`docs/performance-baseline.md`，记录后端 API P99、前端 LCP/FCP/TTI、构建耗时基线

### Changed
- 重构 7 个 Controller 集成测试继承 `BaseApiIntegrationTest`，消除重复注解和注入字段

### Docs
- 补充上下文防规避规则：
  - 在 `AGENTS.md` 明确必须重载的上下文、会话恢复规则和禁止的上下文规避行为
  - 在 DoD、Harness 自检和变更前预检中加入防止选择性忽略长期状态的检查项
- 登记 `TD-005`：`scripts/check.ps1` 在 Maven 后端测试失败时仍可能误报整体通过。
- 调整配置文件提交规范：`backend/risk-starter/src/main/resources/application.yml` 纳入 Git，但只允许保留本地开发默认值和环境变量占位符。
- 补充 Java 命名规范：
  - DTO 统一为 `class + @Data + @NoArgsConstructor + @AllArgsConstructor`，字段使用块级 Javadoc
  - Entity 统一放在 `domain.model.entity` 包下，命名格式为 `XxxEntity`
  - Enum 统一放在 `domain.enums` 包下，命名格式为 `XxxEnums`
- 深度优化 Harness Engineering 规范可信度：
  - 区分 Claude Code Hook 自动化与 Codex/PowerShell 手动验证路径
  - 将 Java checkstyle/spotless 从“已自动执行”修正为“尚未配置的目标自动化”
  - 明确后端 Jacoco 当前只生成报告，覆盖率阈值需要人工核对
  - 放宽 Javadoc 规则，避免 DTO、测试、简单配置类产生低价值注释
  - 新增技术债务 TD-002、TD-003 跟踪覆盖率阈值和 Java 代码风格自动化
- 优化 Harness Engineering 规范：
  - 明确规范优先级，减少 `AGENTS.md`、`CLAUDE.md`、DoD、历史记录之间的冲突判定成本
  - 将任务收尾从“一律默认 commit + push”调整为按代码、文档/配置、只读分析任务分流
  - 补充 SDD 快速判断规则，保留原有轻量/完整 SDD 闭环要求
  - 修正 ADR 中 COLA Maven 多模块描述漂移，并明确项目编译目标为 Java 17
  - 更新会话交接状态到阶段 4 完成、阶段 5 待开始

### Added
- 分析师前端功能（Stage 9）：
  - `EventAnalysisView`：风险事件列表和未处理事件处理动作
  - `ScoreDecisionView`：风险评分列表、决策记录列表和用户重新评分入口
  - `AuditLogView`：审计日志列表
  - 路由接入：`/events`、`/decisions`、`/audit-logs`
  - 测试：`EventAnalysisView.spec`、`ScoreDecisionView.spec`、`AuditLogView.spec`、分析师路由断言
  - SDD 产物：`docs/specs/analyst-frontend.md`、`docs/plans/analyst-frontend.md`
- 管理员前端功能（Stage 8）：
  - `UserManagementView`：用户列表、新增用户、删除用户
  - `RuleManagementView`：规则列表、新增规则、删除规则、启用/停用规则
  - 路由接入：`/users`、`/rules`
  - 测试：`UserManagementView.spec`、`RuleManagementView.spec`、管理员路由断言
  - SDD 产物：`docs/specs/admin-frontend.md`、`docs/plans/admin-frontend.md`
- 前端公共基础（Stage 7）：
  - `LoginView`：登录表单，成功后保存 token 并进入工作台
  - `MainLayout`：侧边导航 + 顶部栏 + 路由内容出口
  - `DashboardView`：风险运营指标概览
  - 前端 API 模块：`authApi`、`userApi`、`ruleApi`、`eventApi`、`scoreApi`、`decisionApi`、`auditApi`
  - HTTP token 支持：本地保存 access/refresh token，并自动注入 Bearer 请求头
  - 测试：`LoginView.spec`、`MainLayout.spec`、`DashboardView.spec`、`modules.spec`、路由测试更新
  - SDD 产物：`docs/specs/frontend-foundation.md`、`docs/plans/frontend-foundation.md`
- 审计日志 API（Stage 6）：
  - `AuditLogService` + `AuditLogServiceImpl`（risk-application）：审计日志记录、详情、分页查询，支持 userId/action/resourceType 筛选
  - `AuditLogController`（risk-interfaces）：`/api/v1/audit-logs` 查询端点，仅 ADMIN 可访问
  - `AuditOperation` 注解 + `AuditOperationAspect`：写操作成功后自动记录审计日志，审计失败不影响主业务响应
  - 在用户、规则、事件、评分、决策和登出写接口补充自动审计标记
  - DTO（risk-application）：`CreateAuditLogRequest`、`AuditLogResponse`
  - 测试：`AuditLogServiceTest` (4) + `AuditLogControllerTest` (4) + `AuditOperationAspectTest` (1)
  - SDD 产物：`docs/specs/audit-log-api.md`、`docs/plans/audit-log-api.md`
- 风险评分与决策 API（Stage 5）：
  - `RiskScoreService` + `RiskScoreServiceImpl`（risk-application）：评分创建、详情、分页、主体最新评分查询
  - `DecisionService` + `DecisionServiceImpl`（risk-application）：决策创建、详情、分页、更新
  - `ScoreController`（risk-interfaces）：`/api/v1/scores` REST 端点，list/get 对认证用户开放，evaluate 仅限 ADMIN
  - `DecisionController`（risk-interfaces）：`/api/v1/decisions` REST 端点，list/get 对认证用户开放，create/update 仅限 ADMIN
  - DTO（risk-application）：`CreateScoreRequest`、`ScoreResponse`、`CreateDecisionRequest`、`UpdateDecisionRequest`、`DecisionResponse`
  - 测试：`RiskScoreServiceTest` (7) + `DecisionServiceTest` (7) Mockito 单元测试，`ScoreControllerTest` (5) + `DecisionControllerTest` (5) MockMvc 集成测试
  - SDD 产物：`docs/specs/score-decision-api.md`、`docs/plans/score-decision-api.md`
- 风险事件 API（Stage 4）：
  - `RiskEventMapper`（risk-infrastructure）：MyBatis-Plus `BaseMapper<RiskEvent>` 基础数据访问
  - `RiskEventService` + `RiskEventServiceImpl`（risk-application）：事件 CRUD、状态更新、解决事件（幂等处理）
  - `EventController`（risk-interfaces）：`/api/v1/events` 完整 REST 端点，支持 riskLevel / status 筛选，list/get 对所有认证用户开放，写操作仅限 ADMIN
  - DTO（risk-application）：`CreateEventRequest`、`UpdateEventRequest`、`EventResponse`
  - 测试：`RiskEventServiceTest` (10) Mockito 单元测试 + `EventControllerTest` (8) MockMvc 集成测试
  - SDD 产物：`docs/specs/event-management.md`、`docs/plans/event-management.md`
- 规则管理 API（Stage 3）：
  - `RiskRuleMapper`（risk-infrastructure）：MyBatis-Plus `BaseMapper<RiskRule>` 基础数据访问
  - `RiskRuleService` + `RiskRuleServiceImpl`（risk-application）：规则 CRUD、启停、分页查询（按 priority 升序 + createdAt 降序）
  - `RuleController`（risk-interfaces）：`/api/v1/rules` 完整 REST 端点，list/get 对所有认证用户开放，写操作仅限 ADMIN
  - DTO（risk-application）：`CreateRuleRequest`、`UpdateRuleRequest`、`RuleResponse`
  - 测试：`RiskRuleServiceTest` (12) Mockito 单元测试 + `RuleControllerTest` (9) MockMvc 集成测试
  - SDD 产物：`docs/specs/rule-management.md`、`docs/plans/rule-management.md`
- 认证与用户 API（Stage 2）：
  - `JwtUtil`（risk-common）：HS256 JWT 生成/解析，支持 access token / refresh token，密钥通过 SHA-256 派生固定为 32 字节
  - `PasswordEncoder`（risk-common）：BCrypt 哈希包装器（strength=10）
  - `AuthConstants`（risk-common）：跨层共享的 request attribute 常量（ATTR_USER_ID / ATTR_USERNAME / ATTR_ROLE），消除 common→domain 循环依赖
  - `RequireRole` 注解 + `RoleAspect` AOP（risk-interfaces）：基于角色字符串的权限控制，避免 common 层依赖 domain 层
  - `AuthService`（risk-application）：登录（5 次失败锁定 15 分钟）、注册（用户名/邮箱冲突检测）、token 刷新、登出占位
  - `UserService`（risk-application）：me / list 分页 / create / update / delete
  - `AuthController`（risk-interfaces）：`/api/v1/auth/login`、`/refresh`、`/logout`、`/register`
  - `UserController`（risk-interfaces）：`/api/v1/users/me`、`/users`（列表/创建）、`/users/{id}`（更新/删除）
  - `JwtInterceptor` + `RequestIdInterceptor`（risk-starter）：JWT Bearer 校验、requestId 注入；`WebMvcConfig` 注册拦截器并排除 `/api/v1/health` 和 `/api/v1/auth/**`
  - H2 内存数据库（risk-starter test scope）+ `application-test.yml`：支持 `@SpringBootTest` + `@AutoConfigureMockMvc` 控制器测试，无需本地 MySQL
  - 测试：后端全量 61 个用例全部通过
    - risk-common：JwtUtilTest (4) + PasswordEncoderTest (3)
    - risk-application：AuthServiceTest (8) + UserServiceTest (9)
    - risk-starter：AuthControllerTest (4) + UserControllerTest (5) + JwtInterceptorTest (4) + Flyway 迁移测试 (4)
  - SDD 产物：`docs/specs/auth-api.md`、`docs/plans/auth-api.md`

- AuditLog 模型与迁移：
  - 新增 `AuditLog` 领域模型，记录操作类型、资源类型/ID、前后值快照、操作者 IP 等审计字段
  - 新增 `AuditLogMapper` 基础 Mapper，为后续审计日志自动注入提供持久化边界
  - 新增 Flyway `V4__create_audit_log_table.sql`，创建 `audit_logs` 表、外键、复合查询索引
  - 新增审计日志模型、Mapper 注解、迁移 SQL 关键路径测试
  - 补充 `backend/lombok.config`，启用 `addLombokGeneratedAnnotation = true`，解决 Lombok 生成方法导致 Jacoco 覆盖率失真问题
- RiskScore / Decision 模型与迁移：
  - 新增 `RiskScore`、`Decision` 领域模型，以及 `DecisionType` 枚举
  - 新增 `RiskScoreMapper`、`DecisionMapper` 基础 Mapper，为评分和决策 API 提供持久化边界
  - 新增 Flyway `V3__create_risk_score_decision_tables.sql`，创建 `risk_scores`、`decisions` 表、外键、唯一约束和查询索引
  - 新增评分/决策模型、Mapper 注解、迁移 SQL 关键路径测试，并补充 `docs/specs/risk-score-decision-model.md`、`docs/plans/risk-score-decision-model.md`
- RiskRule / RiskEvent 模型与迁移：
  - 新增 `RiskRule`、`RiskEvent` 领域模型，以及 `RiskLevel`、`RiskEventStatus` 枚举
  - 新增 `RiskRuleMapper`、`RiskEventMapper` 基础 Mapper，为规则管理和风险事件 API 提供持久化边界
  - 新增 Flyway `V2__create_risk_rule_event_tables.sql`，创建 `risk_rules`、`risk_events` 表、外键和查询索引
  - 新增规则/事件模型、Mapper 注解、迁移 SQL 关键路径测试，并补充 `docs/specs/risk-rule-event-model.md`、`docs/plans/risk-rule-event-model.md`
- User 模型与初始化迁移：
  - 新增 `User` 领域模型、`UserRole` 枚举与 MyBatis-Plus 表映射注解
  - 新增 `UserMapper` 基础 Mapper，为后续用户仓储和认证模块提供持久化边界
  - 新增 Flyway `V1__create_users_table.sql`，创建 `users` 表、用户名/邮箱唯一索引和时间戳字段
  - 新增领域模型、Mapper 注解、迁移 SQL 关键路径测试，并补充 `docs/specs/user-model.md`、`docs/plans/user-model.md`
- 框架缺失组件补齐：
  - `scripts/check.ps1`：Windows PowerShell 质量门禁入口，自动切换 IDEA JDK 21
  - `scripts/check.sh`：与前端/后端验证矩阵对齐
  - `frontend/src/utils/logger.ts`：前端日志门面，替代业务代码直接调用 `console.log`
  - `frontend/src/utils/error-reporting.ts`：Vue/global error/unhandled rejection 错误上报入口
  - 新增 3 个工具测试文件，前端测试总数提升到 8 个测试文件、16 个测试用例
- SDD 产物：`docs/specs/framework-missing-components.md`、`docs/plans/framework-missing-components.md`
- 前端骨架：Vue 3 + TypeScript + Vite + Pinia + Vue Router + Axios
  - `frontend/package.json`、Vite、TypeScript、ESLint、Vitest 配置
  - `src/main.ts`、`App.vue`、基础路由、首页占位、应用 Store、Axios HTTP 客户端
  - 前端测试基线：5 个测试文件，8 个测试用例，Vitest 覆盖率 100%
- SDD 产物：`docs/specs/frontend-skeleton.md`、`docs/plans/frontend-skeleton.md`
- 后端骨架：Maven 多模块 COLA 分层结构
  - `backend/pom.xml` 父 POM（dependencyManagement + 6 modules）
  - `risk-common`：ApiResponse 统一响应、AppException 业务异常、GlobalExceptionHandler 全局异常处理
  - `risk-domain`：领域层空模块（后续填实体/规则）
  - `risk-infrastructure`：基础设施层空模块（后续填 Mapper）
  - `risk-application`：应用层空模块（后续填用例编排）
  - `risk-interfaces`：HealthController 健康检查、RequestIdInterceptor、JwtInterceptor 骨架
  - `risk-starter`：RiskApplication 入口、WebMvcConfig 配置、application.yml
- 测试：HealthControllerTest 单元测试（Tests run: 1, Failures: 0）
- **Compound Engineering 知识沉淀环节**：任务完成后强制记录模式/反模式，更新 `docs/patterns.md`
- **规范漂移检测机制**：规范巡检增加重复/矛盾/空话检测，同类警告超 3 条时主动精简合并
- **agnix 规范自检集成**：质量门禁新增 `npx agnix . --strict` 检查 CLAUDE.md/AGENTS.md 格式合规
- `docs/patterns.md` — 模式/反模式库（知识沉淀产物）
- `docs/specs/` — 功能规格文档目录（SDD Specify 阶段产物）
- `docs/plans/` — 实现计划文档目录（SDD Plan 阶段产物）

### Optimized
- **SDD 闭环规则**：将 SDD 调整为“跳过 / 轻量 SDD / 完整 SDD”三级，明确 Clarify 和 Tasks 的闭环标准，避免小型多文件任务被过度流程化。
- 系统架构文档、API 规范、领域模型文档、ADR
- 定义完成标准：docs/definition-of-done.md（通用/后端/前端/迁移/阶段 DoD）
- 四层反馈循环：docs/feedback-loop.md
- 任务计划清单：docs/task-list.md
- OWASP Top 10 安全检查清单：docs/security-checklist.md
- 架构决策记录：docs/adr/decisions.md（技术栈选型、分层架构、认证方案、审计日志不可变）

### Changed
- 统一后端命名结构：
  - 领域实体迁移到 `com.harness.risk.domain.model.entity` 并改名为 `XxxEntity`
  - 枚举迁移到 `com.harness.risk.domain.enums` 并改名为 `XxxEnums`
  - DTO 从 record 调整为 Lombok JavaBean 风格，字段注释与实体字段格式保持一致
- **后端技术栈切换**：FastAPI + PostgreSQL + SQLAlchemy → Spring Boot 3 + MySQL + MyBatis-Plus
- **后端语言**：Python 3.12 → Java 17
- **构建工具**：uv/pip → Maven 3.9+
- **测试框架**：pytest + httpx → JUnit 5 + Mockito + Spring Boot Test
- **ORM 框架**：SQLAlchemy 2.0 → MyBatis-Plus 3.5.x（代码生成器 + 条件构造器）
- **数据库迁移**：Alembic → Flyway
- **端口调整**：后端 8000 → 8080
- **质量门禁**：ruff + mypy → checkstyle + spotless + javac
- **后端目录结构**：app/{api/core/models/services/repositories} → backend/ 下 6 个 Maven 子模块（starter/interfaces/application/infrastructure/domain/common）
- **编码规范**：Python snake_case → Java camelCase/PascalCase，`@Slf4j` 替代 `print()`
- **异常处理**：`AppError` → `AppException`，`@ControllerAdvice` 全局异常处理
- **日志规范**：`from app.core.logging import get_logger` → `@Slf4j` + `log.info()`
- **后端分层架构**：`Controller → Service → Mapper` 扁平三层 → COLA 四层（Interfaces → Application → Domain ← Infrastructure）
- **ADR-005**：新增 COLA 分层架构决策记录，含与传统分层对比表
- **AGENTS.md**：多 AI 代理兼容的上下文文件，Cursor/Codex 等工具可读取
- **AI Agent 行为规范**：操作纪律、编码纪律、决策透明三大类约束
- **分支策略**：`stage/N-xxx` 特性分支 + Conventional Commits 提交格式
- **Conventional Commits 自动化**：PreToolUse Hook 拦截格式不合规的 `git commit -m`
- **.gitignore**：Python + Node + IDE + Claude + 覆盖率报告完整排除
- **PR 模板**：`.github/pull_request_template.md` 含 DoD 核查清单
- **SLO 定义**：API p99 < 500ms、规则匹配延迟 < 100ms、错误率 < 0.1%、高风险事件 24h 处理率 > 95%
- **完整文档体系**：
  - `session-handoff.md` / `.harness/learnings.md` — 跨会话上下文
  - `docs/README.md` — 文档导航
  - `docs/autonomy-levels.md` — Agent 自治边界（L0-L4）
  - `docs/invariants-and-guardrails.md` — 不可破坏约束
  - `docs/checklists/change-preflight.md` — 变更前预检
  - `docs/harness-checklist.md` — Harness 自检清单
  - `docs/tech-debt.md` — 技术债务追踪
  - `scripts/check.sh` — 质量门禁脚本

### Restructured
- **消除跨文件重复**：CLAUDE.md 与 AGENTS.md 去重（任务状态、会话交接、可观测性基线）
- **职责清晰化**：CLAUDE.md 负责"做什么"，AGENTS.md 负责"怎么做"
- **精简日志/异常规范**：CLAUDE.md 保留简要引用，详细规则归 AGENTS.md
- **更新目录树**：CLAUDE.md 目录树与实际结构同步
- **补充前端环境变量**：`frontend/.env` 配置
- **技术债务独立化**：task-list.md 引用 `docs/tech-debt.md`
