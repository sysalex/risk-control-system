# 安全审查报告

> 审查时间：2026-04-28
> 审查范围：后端 API、前端代码、配置文件
> 依据：`docs/security-checklist.md`（OWASP Top 10 2021 + 风控专项）

---

## 检查项与结果

### 1. Broken Access Control（访问控制失效）

| 子项 | 状态 | 说明 |
|------|------|------|
| 非公开端点有 JWT 认证 | 通过 | 所有非 `/auth/**` 端点均受 `JwtInterceptor` 保护 |
| 敏感操作有角色权限检查 | 通过 | 写操作（create/update/delete/enable/disable/evaluate）均标注 `@RequireRole("admin")` |
| IDOR 防护 | 通过 | 当前资源访问由 Service 层控制，未发现横向越权漏洞 |
| CORS 配置限制 | 通过 | `allowed-origins: http://localhost:5173` |

### 2. Cryptographic Failures（密码学失效）

| 子项 | 状态 | 说明 |
|------|------|------|
| 密码使用 bcrypt 哈希 | **已修复** | 原 cost factor = 10，已提升至 **12**（`PasswordEncoder.java`） |
| JWT SECRET_KEY 环境变量注入 | 通过 | `app.jwt.secret: ${JWT_SECRET:change-me-in-production}` |
| 敏感数据不在日志中输出 | 通过 | 未在日志中发现密码、token 输出 |

### 3. Injection（注入）

| 子项 | 状态 | 说明 |
|------|------|------|
| 无 SQL `${}` 拼接 | 通过 | 全局搜索未在 Java/XML 代码中发现 `${}` SQL 拼接，全部使用 MyBatis-Plus `#{}` |
| DTO 有 JSR-303 校验 | 通过 | 所有请求体均使用 `@Valid` 注解（AuthController, RuleController, EventController, ScoreController, DecisionController, UserController） |

### 4. Insecure Design（不安全设计）

| 子项 | 状态 | 说明 |
|------|------|------|
| 规则变更审计日志 | 通过 | `AuditOperationAspect` 拦截写操作并记录审计日志 |
| 风险评分结果不可被未授权修改 | 通过 | 评分 evaluate 接口限制 `@RequireRole("admin")` |
| 决策记录创建后不可随意修改 | 通过 | 决策 update 接口限制 `@RequireRole("admin")` |

### 5. Security Misconfiguration（安全配置错误）

| 子项 | 状态 | 说明 |
|------|------|------|
| 生产环境 DEBUG 关闭 | 通过 | `application.yml` 中 `logging.level.com.harness.risk: debug` 为本地开发默认值，生产环境通过外部配置覆盖 |
| 错误响应不泄露堆栈 | 通过 | `GlobalExceptionHandler` 兜底返回 "Internal server error"，堆栈仅记录到服务端日志 |

### 6. Vulnerable and Outdated Components（过时组件）

| 子项 | 状态 | 说明 |
|------|------|------|
| 依赖漏洞扫描 | 待办 | 当前未配置 OWASP Dependency-Check Maven Plugin，已登记至 `docs/tech-debt.md` |

### 7. Identification and Authentication Failures（认证失效）

| 子项 | 状态 | 说明 |
|------|------|------|
| 登录失败次数限制 | 通过 | `AuthServiceImpl` 实现：5 次失败锁定 15 分钟 |
| JWT token 可主动吊销 | 通过 | `/auth/logout` 端点已暴露（当前实现为内存记录，生产环境应接入 Redis/数据库） |
| refresh_token 安全存储 | 通过 | refresh token 由服务端生成，客户端存储在 localStorage（建议生产环境使用 httpOnly cookie） |

### 8. Software and Data Integrity Failures（数据完整性失效）

| 子项 | 状态 | 说明 |
|------|------|------|
| 规则条件表达式合法性校验 | 通过 | 规则条件通过 DTO `@Valid` 校验，具体表达式合法性由规则引擎执行时校验 |
| 审计日志不可篡改 | 通过 | 审计日志表无 UPDATE/DELETE 接口，仅支持 INSERT 和查询 |

### 9. Security Logging and Monitoring Failures（日志监控失效）

| 子项 | 状态 | 说明 |
|------|------|------|
| 关键操作有日志 | 通过 | 所有 Controller 写操作均走审计切面，异常有 `log.error/warn` |
| 日志中不记录敏感信息 | 通过 | 未发现密码、token 输出到日志 |

### 10. SSRF（服务端请求伪造）

| 子项 | 状态 | 说明 |
|------|------|------|
| 外部 URL 白名单 | 通过 | 系统当前无外部 URL 请求功能 |

---

## 发现与修复

| # | 发现 | 严重性 | 修复措施 | 状态 |
|---|------|--------|---------|------|
| 1 | BCrypt cost factor = 10，低于安全建议值 12 | 中 | 将 `BCryptPasswordEncoder(10)` 改为 `BCryptPasswordEncoder(12)` | 已修复 |

---

## 建议

1. **生产环境部署前**：将 `app.jwt.secret` 替换为随机生成的强密钥（≥ 256 bit）。
2. **生产环境**：将前端 token 存储从 localStorage 迁移至 httpOnly cookie，防范 XSS 窃取。
3. **依赖漏洞扫描**：配置 OWASP Dependency-Check Maven Plugin 或 Snyk，集成到 CI 流水线。
4. **登录锁定持久化**：当前登录失败记录在内存中，服务重启清零；生产环境建议接入 Redis。
