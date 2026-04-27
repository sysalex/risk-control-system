# 实现计划：认证与用户 API

> SDD Plan 阶段产物，对应 `docs/specs/auth-api.md`。

---

## 文件计划

### 新建文件

| # | 文件路径 | 职责 |
|---|---------|------|
| 1 | `risk-application/src/main/java/.../service/AuthService.java` | 登录、注册、刷新 token、登出逻辑 |
| 2 | `risk-application/src/main/java/.../service/UserService.java` | 用户 CRUD、查询 |
| 3 | `risk-application/src/main/java/.../dto/LoginRequest.java` | 登录请求 DTO（record） |
| 4 | `risk-application/src/main/java/.../dto/RegisterRequest.java` | 注册请求 DTO（record） |
| 5 | `risk-application/src/main/java/.../dto/RefreshRequest.java` | 刷新请求 DTO（record） |
| 6 | `risk-application/src/main/java/.../dto/UserResponse.java` | 用户响应 DTO（record，不含密码） |
| 7 | `risk-application/src/main/java/.../dto/TokenResponse.java` | Token 响应 DTO（record） |
| 8 | `risk-application/src/main/java/.../dto/CreateUserRequest.java` | 管理员创建用户 DTO（record） |
| 9 | `risk-application/src/main/java/.../dto/UpdateUserRequest.java` | 管理员更新用户 DTO（record） |
| 10 | `risk-common/src/main/java/.../security/JwtUtil.java` | JWT 生成、解析、验证工具 |
| 11 | `risk-common/src/main/java/.../security/PasswordEncoder.java` | BCrypt 包装（便于单元测试 mock） |
| 12 | `risk-common/src/main/java/.../annotation/RequireRole.java` | 方法级角色注解 |
| 13 | `risk-interfaces/src/main/java/.../controller/AuthController.java` | /auth 路由 |
| 14 | `risk-interfaces/src/main/java/.../controller/UserController.java` | /users 路由 |
| 15 | `risk-interfaces/src/main/java/.../aspect/RoleAspect.java` | @RequireRole AOP 切面 |
| 16 | `risk-starter/src/test/java/.../AuthApiTest.java` | 认证接口集成测试 |
| 17 | `risk-starter/src/test/java/.../UserApiTest.java` | 用户接口集成测试 |
| 18 | `risk-application/src/test/java/.../AuthServiceTest.java` | AuthService 单元测试 |
| 19 | `risk-application/src/test/java/.../UserServiceTest.java` | UserService 单元测试 |
| 20 | `risk-common/src/test/java/.../JwtUtilTest.java` | JWT 工具单元测试 |

### 修改文件

| # | 文件路径 | 修改内容 |
|---|---------|---------|
| 21 | `JwtInterceptor.java` | 接入 JwtUtil 真正解析 token，将 userId/username/role 写入 request attribute |
| 22 | `WebMvcConfig.java` | 注册 JwtInterceptor，配置公开路径（/auth/**, /health）免认证 |
| 23 | `pom.xml` (risk-common) | 添加 jjwt-api/impl/jackson 依赖 |
| 24 | `pom.xml` (risk-interfaces) | 添加 spring-boot-starter-aop 依赖 |

---

## TDD 步骤

按依赖顺序执行，每个步骤：RED → GREEN → REFACTOR。

### Step 1：JWT 工具 + 密码编码器（risk-common）
- `JwtUtilTest`：生成/解析/过期/非法签名验证
- `PasswordEncoderTest`：编码/匹配验证
- 实现 `JwtUtil`、`PasswordEncoder`

### Step 2：AuthService（risk-application）
- `AuthServiceTest`：登录成功、登录失败（密码错误、用户不存在）、注册成功、注册冲突、刷新 token
- 实现 `AuthService`

### Step 3：UserService（risk-application）
- `UserServiceTest`：查询 me、列表分页、创建、更新、删除
- 实现 `UserService`

### Step 4：JwtInterceptor + RoleAspect（risk-interfaces / risk-starter）
- `AuthApiTest`：无 token 访问受保护接口返回 401
- 实现 JwtInterceptor 解析逻辑、RoleAspect 权限拦截
- 修改 WebMvcConfig 注册拦截器

### Step 5：Controller 集成测试（risk-starter）
- `AuthApiTest`：login / refresh / logout / register 完整链路
- `UserApiTest`：me / list / create / update / delete 完整链路，含角色越权 403

---

## 数据流

```
[Client] → AuthController / UserController
    → DTO 参数校验 (@Valid)
    → AuthService / UserService
        → UserMapper (MyBatis-Plus)
        → JwtUtil / PasswordEncoder
    → ApiResponse.ok() / 异常
        → GlobalExceptionHandler
    → [Client]
```

## 异常路径

| 场景 | 抛出异常 | HTTP 状态码 | 响应 |
|------|---------|------------|------|
| 用户名/密码错误 | AppException(401) | 401 | 用户名或密码错误 |
| 用户不存在 | AppException.notFound | 404 | User not found |
| 用户名/邮箱已存在 | AppException.conflict | 409 | 用户名或邮箱已存在 |
| 无权限访问 | AppException.forbidden | 403 | Access denied |
| token 过期/无效 | AppException(401) | 401 | Token expired or invalid |
| 参数校验失败 | MethodArgumentNotValidException | 400 | 字段错误拼接 |
| 登录次数超限 | AppException(429) | 429 | 账号已锁定，请 15 分钟后重试 |

## 回滚/兼容策略

- 本阶段不修改数据库 schema，仅新增 Java 代码，回滚只需回退代码
- JWT secret 可通过环境变量覆盖，不影响已有 token（阶段 1 尚未投产）
- 若 `RequireRole` 注解有 bug，可先注释掉 Aspect 注册，降级为仅依赖 JwtInterceptor

## 验证命令

```bash
cd backend
mvn test -pl risk-common,risk-application,risk-starter -am
mvn verify -pl risk-starter
```

## 风险

| 风险 | 缓解措施 |
|------|---------|
| jjwt 0.12.x API 与旧版差异大 | 严格按官方文档使用 `Jwts.parser().verifyWith(key).build()` |
| BCrypt strength=10 在单元测试中较慢 | 测试中 mock PasswordEncoder，不实际哈希 |
| 登录次数存储在内存，服务重启清零 | 文档明确标记为临时方案，阶段 6 前迁 Redis |
