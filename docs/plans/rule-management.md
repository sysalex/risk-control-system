# 计划：规则管理 API

> 轻量 SDD 产物，对应规格 `docs/specs/rule-management.md`

---

## 文件计划

### 新增文件

| 文件路径 | 说明 |
|---------|------|
| `backend/risk-infrastructure/src/main/java/com/harness/risk/infrastructure/mapper/RiskRuleMapper.java` | MyBatis-Plus Mapper，继承 BaseMapper |
| `backend/risk-infrastructure/src/main/resources/mapper/RiskRuleMapper.xml` | 复杂查询 XML（如需要分页+排序） |
| `backend/risk-application/src/main/java/com/harness/risk/application/service/RiskRuleService.java` | Service 接口 |
| `backend/risk-application/src/main/java/com/harness/risk/application/service/impl/RiskRuleServiceImpl.java` | Service 实现 |
| `backend/risk-application/src/test/java/com/harness/risk/application/service/RiskRuleServiceTest.java` | Application 层单元测试（Mockito，不启动 Spring） |
| `backend/risk-interfaces/src/main/java/com/harness/risk/interfaces/controller/RuleController.java` | REST Controller |
| `backend/risk-interfaces/src/test/java/com/harness/risk/interfaces/controller/RuleControllerTest.java` | Interfaces 层集成测试（@SpringBootTest + MockMvc + H2） |
| `backend/risk-common/.../dto/rule/CreateRuleRequest.java` | 创建规则请求 DTO |
| `backend/risk-common/.../dto/rule/UpdateRuleRequest.java` | 更新规则请求 DTO |
| `backend/risk-common/.../dto/rule/RuleResponse.java` | 规则响应 DTO |

### 修改文件

| 文件路径 | 修改内容 |
|---------|---------|
| `docs/api-spec.md` | 补充 `/rules` 端点请求/响应体示例 |

---

## TDD 步骤

### Step 1：RiskRuleRepository（Mapper）

- **[RED]** 编写 `RiskRuleMapperTest`：验证 Mapper 继承 BaseMapper 后可正常 CRUD
- **[GREEN]** 创建 `RiskRuleMapper.java` 接口，继承 `BaseMapper<RiskRule>`
- 若 BaseMapper 无法满足分页排序，补充 XML

### Step 2：RiskRuleService（Application 层）

- **[RED]** 编写 `RiskRuleServiceTest`（Mockito，mock Mapper）：
  - `createRule`：正常创建，返回规则对象
  - `createRule_nameExists`：名称已存在时抛 `AppException`
  - `updateRule`：正常更新
  - `updateRule_notFound`：规则不存在时抛 `AppException`
  - `updateRule_nameConflict`：改名时目标名称已存在抛异常
  - `getRuleById`：正常查询
  - `getRuleById_notFound`：不存在时抛 `AppException`
  - `listRules`：分页查询，验证 Page 返回
  - `deleteRule`：正常删除
  - `deleteRule_notFound`：不存在时抛异常
  - `enableRule` / `disableRule`：状态切换，重复操作幂等
- **[GREEN]** 实现 `RiskRuleServiceImpl`

### Step 3：RuleController（Interfaces 层）

- **[RED]** 编写 `RuleControllerTest`（`@SpringBootTest` + `MockMvc` + H2）：
  - `GET /api/v1/rules`：列表分页，验证 200 + 响应结构
  - `POST /api/v1/rules`：ADMIN 创建规则，验证 201
  - `POST /api/v1/rules`：非 ADMIN 创建返回 403
  - `GET /api/v1/rules/{id}`：详情查询 200
  - `GET /api/v1/rules/{id}`：不存在返回 404
  - `PUT /api/v1/rules/{id}`：更新规则 200
  - `DELETE /api/v1/rules/{id}`：删除规则 204
  - `POST /api/v1/rules/{id}/enable`：启用规则 200
  - `POST /api/v1/rules/{id}/disable`：停用规则 200
- **[GREEN]** 实现 `RuleController`

### Step 4：REFACTOR & REVIEW

- 检查 Service 层是否有跨层调用（禁止直接调用 Mapper 以外的 Infrastructure）
- 检查 Controller 层是否有业务逻辑（只做参数校验 + 响应包装）
- 检查权限注解覆盖完整

---

## 风险与应对

| 风险 | 影响 | 应对 |
|------|------|------|
| RiskRule 实体 conditions/actions 为 String，DTO 若用 Map 会引入序列化复杂度 | 中等 | DTO 统一使用 String，与实体保持一致，前端自行解析 JSON |
| 规则名称唯一性依赖数据库 UNIQUE 约束，异常可读性差 | 低 | Service 层先显式查询校验，冲突时抛业务异常而非数据库异常 |
| `@RequireRole` 当前仅支持单一角色，api-spec 要求 admin/analyst 均可创建 | 低 | 当前阶段按 ADMIN 实现，如需 analyst 则在后续阶段扩展注解或权限体系 |

---

## 验证命令

```bash
cd backend
# Application 层单元测试
mvn test -Dtest=RiskRuleServiceTest

# Infrastructure 层集成测试
mvn test -Dtest=RiskRuleMapperTest

# Interfaces 层集成测试
mvn test -Dtest=RuleControllerTest

# 全量验证
mvn verify
```
