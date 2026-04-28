# 计划：评分与决策 API

> 轻量 SDD 产物，对应规格 `docs/specs/score-decision-api.md`。

## 文件计划

### 新增文件

| 文件 | 说明 |
|------|------|
| `backend/risk-application/src/main/java/com/harness/risk/application/dto/CreateScoreRequest.java` | 创建评分请求 |
| `backend/risk-application/src/main/java/com/harness/risk/application/dto/ScoreResponse.java` | 评分响应 |
| `backend/risk-application/src/main/java/com/harness/risk/application/dto/CreateDecisionRequest.java` | 创建决策请求 |
| `backend/risk-application/src/main/java/com/harness/risk/application/dto/UpdateDecisionRequest.java` | 更新决策请求 |
| `backend/risk-application/src/main/java/com/harness/risk/application/dto/DecisionResponse.java` | 决策响应 |
| `backend/risk-application/src/main/java/com/harness/risk/application/service/RiskScoreService.java` | 评分 Application Service 接口 |
| `backend/risk-application/src/main/java/com/harness/risk/application/service/DecisionService.java` | 决策 Application Service 接口 |
| `backend/risk-application/src/main/java/com/harness/risk/application/service/impl/RiskScoreServiceImpl.java` | 评分 Application Service 实现 |
| `backend/risk-application/src/main/java/com/harness/risk/application/service/impl/DecisionServiceImpl.java` | 决策 Application Service 实现 |
| `backend/risk-interfaces/src/main/java/com/harness/risk/interfaces/controller/ScoreController.java` | 评分 REST Controller |
| `backend/risk-interfaces/src/main/java/com/harness/risk/interfaces/controller/DecisionController.java` | 决策 REST Controller |
| `backend/risk-application/src/test/java/com/harness/risk/application/service/RiskScoreServiceTest.java` | 评分 Service 单元测试 |
| `backend/risk-application/src/test/java/com/harness/risk/application/service/DecisionServiceTest.java` | 决策 Service 单元测试 |
| `backend/risk-starter/src/test/java/com/harness/risk/interfaces/controller/ScoreControllerTest.java` | 评分 Controller 集成测试 |
| `backend/risk-starter/src/test/java/com/harness/risk/interfaces/controller/DecisionControllerTest.java` | 决策 Controller 集成测试 |

### 修改文件

| 文件 | 说明 |
|------|------|
| `docs/api-spec.md` | 补充评分与决策接口细节 |
| `docs/task-list.md` | 更新阶段 5 任务状态 |
| `CHANGELOG.md` | 记录阶段 5 变更 |

## TDD 步骤

### Step 1：RiskScoreService

- **[RED]** 编写 `RiskScoreServiceTest`：
  - create 创建评分并返回响应。
  - create 对同一 eventId 冲突抛 409。
  - getById 不存在抛 404。
  - list 返回分页响应。
  - getLatestBySubject 按 evaluatedAt desc 取第一条，不存在抛 404。
- **[GREEN]** 实现 DTO、接口、`RiskScoreServiceImpl`。

### Step 2：DecisionService

- **[RED]** 编写 `DecisionServiceTest`：
  - create 创建决策并返回响应。
  - create 对同一 eventId 冲突抛 409。
  - getById 不存在抛 404。
  - list 返回分页响应。
  - update 修改决策类型、原因、备注，不修改 decidedBy。
- **[GREEN]** 实现 DTO、接口、`DecisionServiceImpl`。

### Step 3：Controller

- **[RED]** 编写 `ScoreControllerTest` 和 `DecisionControllerTest`：
  - list/get 对 analyst 可访问。
  - create/update 对 analyst 返回 403，对 admin 返回 200。
  - `scores/subject/{type}/{id}` 返回最新评分。
- **[GREEN]** 实现 `ScoreController`、`DecisionController`。

### Step 4：REFACTOR & REVIEW

- 检查 Controller 是否只做参数绑定、权限和响应包装。
- 检查 Application Service 是否只通过 Mapper 持久化，不跨层调用 Controller/Domain 技术细节。
- 更新 API 文档、任务清单和变更日志。

## 风险与应对

| 风险 | 级别 | 应对 |
|------|------|------|
| `scores/evaluate` 名称容易被理解为真实算法 | 中 | 规格中明确本阶段为持久化评分结果，真实算法后续单独实现 |
| eventId 唯一约束冲突只靠数据库异常 | 中 | Service 创建前用 selectCount 做冲突校验并返回 409 |
| Decision 更新破坏“最终决策”语义 | 中 | 本阶段允许管理端更新决策字段；阶段 6 审计落地后记录变更 |

## 验证命令

```powershell
cd backend
mvn test -Dtest=RiskScoreServiceTest
mvn test -Dtest=DecisionServiceTest
mvn test -Dtest=ScoreControllerTest
mvn test -Dtest=DecisionControllerTest
mvn test

cd ..
powershell -ExecutionPolicy Bypass -File scripts/check.ps1
```
