# 计划：风险事件 API

> 轻量 SDD 产物，对应规格 `docs/specs/event-management.md`

---

## 文件计划

### 新增文件

| 文件路径 | 说明 |
|---------|------|
| `backend/risk-infrastructure/src/main/java/com/harness/risk/infrastructure/mapper/RiskEventMapper.java` | MyBatis-Plus Mapper，继承 BaseMapper |
| `backend/risk-application/src/main/java/com/harness/risk/application/service/RiskEventService.java` | Service 接口 |
| `backend/risk-application/src/main/java/com/harness/risk/application/service/impl/RiskEventServiceImpl.java` | Service 实现 |
| `backend/risk-application/src/test/java/com/harness/risk/application/service/RiskEventServiceTest.java` | Application 层单元测试（Mockito） |
| `backend/risk-interfaces/src/main/java/com/harness/risk/interfaces/controller/EventController.java` | REST Controller |
| `backend/risk-starter/src/test/java/com/harness/risk/interfaces/controller/EventControllerTest.java` | Interfaces 层集成测试（@SpringBootTest + MockMvc + H2） |
| `backend/risk-application/src/main/java/com/harness/risk/application/dto/event/CreateEventRequest.java` | 创建事件请求 DTO |
| `backend/risk-application/src/main/java/com/harness/risk/application/dto/event/UpdateEventRequest.java` | 更新事件请求 DTO |
| `backend/risk-application/src/main/java/com/harness/risk/application/dto/event/EventResponse.java` | 事件响应 DTO |

### 修改文件

| 文件路径 | 修改内容 |
|---------|---------|
| `docs/api-spec.md` | 补充 `/events` 端点请求/响应体示例 |

---

## TDD 步骤

### Step 1：RiskEventRepository（Mapper）

- **[RED]** 编写 `RiskEventMapperTest`：验证 Mapper 继承 BaseMapper 后可正常 CRUD
- **[GREEN]** 创建 `RiskEventMapper.java` 接口，继承 `BaseMapper<RiskEvent>`

### Step 2：RiskEventService（Application 层）

- **[RED]** 编写 `RiskEventServiceTest`（Mockito，mock Mapper）：
  - `createEvent`：正常创建，返回事件对象
  - `getById`：正常查询
  - `getById_notFound`：不存在时抛 `AppException`
  - `listEvents`：分页查询，验证 Page 返回
  - `listEvents_withFilters`：带 riskLevel / status 筛选的分页查询
  - `updateEvent`：更新状态和描述
  - `updateEvent_notFound`：不存在时抛异常
  - `resolveEvent`：解决事件，设置 resolvedAt 和 resolvedBy
  - `resolveEvent_notFound`：不存在时抛异常
  - `resolveEvent_alreadyResolved`：重复解决幂等（返回已解决的事件）
- **[GREEN]** 实现 `RiskEventServiceImpl`

### Step 3：EventController（Interfaces 层）

- **[RED]** 编写 `EventControllerTest`（`@SpringBootTest` + `MockMvc` + H2）：
  - `GET /api/v1/events`：列表分页，验证 200 + 响应结构
  - `GET /api/v1/events?riskLevel=high`：按风险等级筛选
  - `POST /api/v1/events`：ADMIN 创建事件，验证 201
  - `POST /api/v1/events`：非 ADMIN 创建返回 403
  - `GET /api/v1/events/{id}`：详情查询 200
  - `PUT /api/v1/events/{id}`：更新事件 200
  - `POST /api/v1/events/{id}/resolve`：解决事件 200
- **[GREEN]** 实现 `EventController`

### Step 4：REFACTOR & REVIEW

- 检查 Service 层是否有跨层调用
- 检查 Controller 层是否有业务逻辑
- 检查权限注解覆盖完整

---

## 风险与应对

| 风险 | 影响 | 应对 |
|------|------|------|
| RiskLevelEnums / RiskEventStatusEnums 枚举在 DTO 中的序列化 | 低 | 使用枚举类型，MyBatis-Plus @EnumValue 已配置，Spring 默认支持枚举序列化 |
| 列表查询带多个可选筛选参数，Wrapper 构建复杂 | 低 | 使用条件链式构造，null 参数跳过条件拼接 |

---

## 验证命令

```bash
cd backend
# Application 层单元测试
mvn test -Dtest=RiskEventServiceTest

# Infrastructure 层集成测试
mvn test -Dtest=RiskEventMapperTest

# Interfaces 层集成测试
mvn test -Dtest=EventControllerTest

# 全量验证
mvn verify
```
