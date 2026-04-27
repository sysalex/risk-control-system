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
- [ ] 1.4 AuditLog 模型 + 迁移

依赖：阶段 0 全部完成

---

## 阶段 2：后端 API — 认证与用户

- [ ] 2.1 UserRepository（CRUD）
- [ ] 2.2 AuthService（登录、注册、token 刷新）
- [ ] 2.3 UserService（用户管理）
- [ ] 2.4 /auth 路由（login、refresh、logout、register）
- [ ] 2.5 /users 路由（me、列表、创建、删除）
- [ ] 2.6 认证依赖注入（JwtInterceptor、RequireRole 注解）
- [ ] 2.7 认证模块测试（覆盖率 ≥ 80%）

依赖：阶段 1 全部完成

---

## 阶段 3：后端 API — 规则管理

- [ ] 3.1 RiskRuleRepository
- [ ] 3.2 RiskRuleService（CRUD、启停规则）
- [ ] 3.3 /rules 路由
- [ ] 3.4 规则模块测试

依赖：阶段 2 完成

---

## 阶段 4：后端 API — 风险事件

- [ ] 4.1 RiskEventRepository
- [ ] 4.2 RiskEventService（创建、查询、解决事件）
- [ ] 4.3 /events 路由
- [ ] 4.4 事件模块测试

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

## 已知技术债务

详见 [`docs/tech-debt.md`](./tech-debt.md)。
