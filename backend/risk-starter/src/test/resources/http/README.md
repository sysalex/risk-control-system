# HTTP 请求示例

本目录使用 IntelliJ HTTP Client (`.http`) 格式，为每个业务模块提供可直接运行的请求示例。

## 使用方式

1. 在 IDEA 中打开任意 `.http` 文件，点击左侧绿色箭头即可执行请求
2. 或者使用命令行：
   ```bash
   # 先登录获取 token
   curl -X POST http://localhost:8080/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin12345"}'

   # 复制返回的 accessToken，替换到其他请求中
   ```

## 变量说明

- `{{accessToken}}`：登录后获取的 JWT access_token
- `{{refreshToken}}`：登录后获取的 JWT refresh_token

在 `auth.http` 中执行登录请求后，会自动设置这两个变量到全局环境，其他文件可直接引用。

## 文件清单

| 文件 | 覆盖模块 | 端点数 |
|------|---------|--------|
| `auth.http` | 认证（登录/注册/刷新/登出） | 4 |
| `users.http` | 用户管理（me/列表/创建/更新/删除） | 4 |
| `rules.http` | 规则管理（CRUD/启用/停用） | 7 |
| `events.http` | 风险事件（创建/列表/详情/更新/解决） | 5 |
| `scores.http` | 风险评分（创建/列表/详情/主体评分） | 4 |
| `decisions.http` | 决策记录（创建/列表/详情/更新） | 4 |
| `audit-logs.http` | 审计日志（列表/详情） | 2 |

## 规范要求

每个新增 API 必须同步提供对应的 `.http` 请求示例，覆盖：
- **正常请求**：200/201 成功场景
- **错误边界**：404 不存在、409 冲突、422 业务校验失败、403 权限不足
- **权限差异**：ADMIN vs OPERATOR 可访问性差异

存量 JUnit 测试不删除，后续新增功能不再补充 JUnit/Mockito 单元测试或 `@SpringBootTest` 集成测试。
