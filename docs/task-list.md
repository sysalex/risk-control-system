# 任务计划清单

> Harness Engineering 规范要求：所有开发工作在开始前必须在此登记，完成后更新状态。
> Agent 每次开始工作前先查阅此文件，确认当前任务和依赖关系。

## 状态说明

- `[ ]` 待开始
- `[~]` 进行中
- `[x]` 已完成
- `[!]` 阻塞中（注明原因）

---

## 阶段 0：框架搭建

- [x] 0.1 搭建 Harness Engineering 框架（CLAUDE.md、settings.json、docs/）
- [x] 0.2 后端骨架（Spring Boot 3 入口、config 配置、统一响应格式、全局异常处理）
- [x] 0.3 前端骨架（Vue 3、Pinia、Router、Axios 封装）
  - 完成时间：2026-04-27
  - 测试覆盖率：100%
  - 审查状态：本地代码/安全自检通过
- [x] 0.4 补全框架缺失组件（日志、异常体系、任务清单、质量门禁）
  - 完成时间：2026-04-27
  - 测试覆盖率：前端 Statements 100% / Branches 90.9% / Functions 100% / Lines 100%
  - 审查状态：本地代码/安全自检通过

---

## 阶段 1：数据库模型

- [x] 1.1 User 模型 + Flyway 初始化迁移
  - 完成时间：2026-04-27
  - 测试覆盖率：新增领域模型、Mapper、迁移 SQL 关键路径测试；全量质量门禁通过
  - 审查状态：本地代码/安全自检通过
- [x] 1.2 RiskRule / RiskEvent 模型 + 迁移
  - 完成时间：2026-04-27
  - 测试覆盖率：新增规则/事件模型、Mapper、迁移 SQL 关键路径测试；全量质量门禁通过
  - 审查状态：本地代码/安全自检通过
- [x] 1.3 RiskScore / Decision 模型 + 迁移
  - 完成时间：2026-04-27
  - 测试覆盖率：新增评分/决策模型、Mapper、迁移 SQL 关键路径测试；全量质量门禁通过
  - 审查状态：本地代码/安全自检通过
- [x] 1.4 AuditLog 模型 + 迁移
  - 完成时间：2026-04-27
  - 测试覆盖率：risk-domain 100%（Lombok 生成方法已排除）；新增领域模型、Mapper、迁移 SQL 关键路径测试；全量质量门禁通过
  - 审查状态：本地代码/安全自检通过

依赖：阶段 0 全部完成

---

## 阶段 2：后端 API — 认证与用户

- [x] 2.1 UserRepository（CRUD）
- [x] 2.2 AuthService（登录、注册、token 刷新）
  - 完成时间：2026-04-27
  - 包含登录失败 5 次锁定（15 分钟）、注册冲突检测、token 刷新
- [x] 2.3 UserService（用户管理）
  - 完成时间：2026-04-27
  - 包含 me / list（分页）/ create / update / delete
- [x] 2.4 /auth 路由（login、refresh、logout、register）
- [x] 2.5 /users 路由（me、列表、创建、删除）
- [x] 2.6 认证依赖注入（JwtInterceptor、RequireRole 注解、RoleAspect AOP）
- [x] 2.7 认证模块测试（覆盖率 ≥ 80%）
  - 完成时间：2026-04-27
  - 后端全量测试：61 个用例，0 失败；Jacoco 全模块报告通过
  - risk-common：JwtUtilTest (4) + PasswordEncoderTest (3)
  - risk-application：AuthServiceTest (8) + UserServiceTest (9)
  - risk-starter：AuthControllerTest (4) + UserControllerTest (5) + JwtInterceptorTest (4) + 迁移测试 (4)
  - 审查状态：本地代码/安全自检通过

依赖：阶段 1 全部完成

---

## 阶段 3：后端 API — 规则管理

- [x] 3.1 RiskRuleRepository
  - 完成时间：2026-04-27
  - 新增 `RiskRuleMapper` 继承 `BaseMapper<RiskRule>`
- [x] 3.2 RiskRuleService（CRUD、启停规则）
  - 完成时间：2026-04-27
  - 新增 `RiskRuleService` 接口 + `RiskRuleServiceImpl`
  - 包含：create / getById / list（按 priority asc, createdAt desc 分页）/ update / delete / enableRule / disableRule
  - 名称唯一性校验、不存在时抛 404 业务异常
- [x] 3.3 /rules 路由
  - 完成时间：2026-04-27
  - 新增 `RuleController`：GET /rules、GET /rules/{id}、POST /rules、PUT /rules/{id}、DELETE /rules/{id}、POST /rules/{id}/enable、POST /rules/{id}/disable
  - 权限：list/get 对所有认证用户开放；写操作仅限 ADMIN
- [x] 3.4 规则模块测试
  - 完成时间：2026-04-27
  - 后端全量测试：73 个用例，0 失败
  - risk-application：`RiskRuleServiceTest` (12) — Mockito 单元测试
  - risk-starter：`RuleControllerTest` (9) — `@SpringBootTest` + `MockMvc` 集成测试
  - 审查状态：代码审查通过

依赖：阶段 2 完成

---

## 阶段 4：后端 API — 风险事件

- [x] 4.1 RiskEventRepository
  - 完成时间：2026-04-28
  - 新增 `RiskEventMapper` 继承 `BaseMapper<RiskEvent>`
- [x] 4.2 RiskEventService（创建、查询、解决事件）
  - 完成时间：2026-04-28
  - 新增 `RiskEventService` 接口 + `RiskEventServiceImpl`
  - 包含：create / getById / list（支持 riskLevel + status 筛选）/ update / resolveEvent
  - 解决事件幂等：已解决的事件不重复修改 resolvedBy
- [x] 4.3 /events 路由
  - 完成时间：2026-04-28
  - 新增 `EventController`：GET /events、GET /events/{id}、POST /events、PUT /events/{id}、POST /events/{id}/resolve
  - 权限：list/get 对所有认证用户开放；写操作仅限 ADMIN
- [x] 4.4 事件模块测试
  - 完成时间：2026-04-28
  - 后端全量测试：91 个用例，0 失败
  - risk-application：`RiskEventServiceTest` (10) — Mockito 单元测试
  - risk-starter：`EventControllerTest` (8) — `@SpringBootTest` + `MockMvc` 集成测试
  - 审查状态：代码审查通过

依赖：阶段 3 完成

---

## 阶段 5：后端 API — 评分与决策

- [ ] 5.1 RiskScoreRepository + DecisionRepository
- [ ] 5.2 RiskScoreService + DecisionService
- [ ] 5.3 /scores 路由
- [ ] 5.4 /decisions 路由
- [ ] 5.5 评分决策模块测试

依赖：阶段 4 完成

---

## 阶段 6：后端 API — 审计日志

- [ ] 6.1 AuditLogRepository
- [ ] 6.2 AuditLogService
- [ ] 6.3 /audit-logs 路由
- [ ] 6.4 审计日志自动注入（所有写操作自动记录）
- [ ] 6.5 审计模块测试

依赖：阶段 5 完成

---

## 阶段 7：前端 — 公共基础

- [ ] 7.1 登录页面（LoginView）
- [ ] 7.2 主布局（MainLayout：侧边栏 + 顶栏）
- [ ] 7.3 Dashboard 首页
- [ ] 7.4 前端 API 模块（auth、user、rule、event、score、decision、audit）

依赖：阶段 2 完成（可并行开发）

---

## 阶段 8：前端 — 管理员功能

- [ ] 8.1 用户管理页面
- [ ] 8.2 规则管理页面（CRUD、启停）

依赖：阶段 7 完成

---

## 阶段 9：前端 — 分析师功能

- [ ] 9.1 风险事件列表/详情/处理页面
- [ ] 9.2 风险评分/决策页面
- [ ] 9.3 审计日志查看页面

依赖：阶段 8 完成

---

## 阶段 10：质量保障

- [ ] 10.1 后端集成测试（全量 API）
- [ ] 10.2 前端单元测试（Store、工具函数）
- [ ] 10.3 E2E 测试（核心流程：登录→规则→事件→决策→审计）
- [ ] 10.4 安全审查（OWASP Top 10 检查清单）
- [ ] 10.5 性能基准测试

依赖：阶段 9 完成

---

## 规范维护

- [x] N.1 Harness Engineering 规范优化
  - 范围：修正规范漂移、补充优先级、简化 SDD 快速判断、按任务类型明确收尾策略
  - 类型：文档任务，不涉及代码、API 契约、数据库 schema、认证授权
  - 完成时间：2026-04-28
  - 审查状态：文档差异自检通过
- [x] N.2 Harness Engineering 规范可信度优化
  - 范围：对齐文档声明与实际自动化能力，澄清 Claude/Codex 环境差异，收紧 DoD 到当前可执行边界
  - 类型：文档任务，不涉及代码、API 契约、数据库 schema、认证授权
  - 完成时间：2026-04-28
  - 审查状态：文档差异自检通过

---

## 已知技术债务

详见 [`docs/tech-debt.md`](./tech-debt.md)。
