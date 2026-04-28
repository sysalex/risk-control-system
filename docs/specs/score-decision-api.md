# 规格：评分与决策 API

> 轻量 SDD 产物，对应 `docs/task-list.md` 阶段 5。

## 目标

- 提供风险评分查询、创建和按主体查询最新评分 API。
- 提供决策记录查询、创建和更新 API。
- 保持 Controller 薄层，业务编排放在 Application Service。
- 复用现有 `RiskScore`、`Decision`、`RiskScoreMapper`、`DecisionMapper`。

## 非目标

- 不实现真实评分算法，`POST /scores/evaluate` 接收外部传入的评分结果并持久化。
- 不实现审计日志自动注入，阶段 6 处理。
- 不修改数据库 schema 或既有 Flyway 迁移。

## 验收标准

1. `RiskScoreService` 支持 create / getById / list / getLatestBySubject。
2. `DecisionService` 支持 create / getById / list / update。
3. `/scores` 端点：
   - `GET /scores`：所有认证用户可访问，分页返回评分列表。
   - `POST /scores/evaluate`：仅 ADMIN 可访问，创建评分。
   - `GET /scores/{id}`：所有认证用户可访问，返回评分详情。
   - `GET /scores/subject/{type}/{id}`：所有认证用户可访问，返回主体最新评分。
4. `/decisions` 端点：
   - `GET /decisions`：所有认证用户可访问，分页返回决策列表。
   - `POST /decisions`：仅 ADMIN 可访问，创建决策。
   - `GET /decisions/{id}`：所有认证用户可访问，返回决策详情。
   - `PUT /decisions/{id}`：仅 ADMIN 可访问，更新决策原因、备注或类型。
5. Service 层使用 Mockito 单元测试，Controller 层使用 `@SpringBootTest` + `MockMvc` 集成测试。
6. 新增 API 写入 `docs/api-spec.md`，任务完成后更新 `CHANGELOG.md` 和 `docs/task-list.md`。

## 默认假设

1. `scores/evaluate` 不是完整算法执行，只是创建评分记录；真实评分算法后续单独登记。
2. 同一事件只允许一条评分和一条决策，数据库唯一约束兜底；Service 创建前也做冲突校验并返回 409。
3. 评分列表默认按 `evaluatedAt desc` 排序，决策列表默认按 `decidedAt desc` 排序。
4. 分页参数统一为 `page` / `limit`，默认 `page=1`，`limit=20`。
5. 决策更新不改变 `decidedBy`，只更新时间和可编辑字段。

## 待确认问题

**无待确认问题。** 当前阶段边界可从现有 `api-spec.md`、`domain-model.md` 和阶段 3/4 API 模式推断。
