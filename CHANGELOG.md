# Changelog

所有重要变更记录在此文件，格式遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)。

版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

---

## [Unreleased]

### Changed
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

### Added
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
- 系统架构文档、API 规范、领域模型文档、ADR
- 定义完成标准：docs/definition-of-done.md（通用/后端/前端/迁移/阶段 DoD）
- 四层反馈循环：docs/feedback-loop.md
- 任务计划清单：docs/task-list.md
- OWASP Top 10 安全检查清单：docs/security-checklist.md
- 架构决策记录：docs/adr/decisions.md（技术栈选型、分层架构、认证方案、审计日志不可变）

### Optimized
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
