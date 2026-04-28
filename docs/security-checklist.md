# OWASP Top 10 安全检查清单

> 风控系统对安全性要求更高，以下检查清单需在每个阶段完成时核对。

## OWASP Top 10 (2021) 检查项

### 1. Broken Access Control（访问控制失效）
- [ ] 所有非公开端点有 JWT 认证
- [ ] 敏感操作有角色权限检查（`@RequireRole` / 拦截器）
- [ ] IDOR 防护：用户只能访问自己的资源或授权范围内的资源
- [ ] CORS 配置限制，只允许前端域名
- [ ] JWT 拦截器对 `OPTIONS` 预检请求直接放行（否则浏览器 CORS 机制失效）

### 2. Cryptographic Failures（密码学失效）
- [ ] 密码使用 bcrypt 哈希（cost factor >= 12）
- [ ] JWT SECRET_KEY 通过环境变量注入，不硬编码
- [ ] HTTPS 传输（生产环境）
- [ ] 敏感数据不在日志中明文输出

### 3. Injection（注入）
- [ ] 所有 SQL 操作通过 MyBatis-Plus，禁止 `${}` 拼接（使用 `#{}` 参数化）
- [ ] 用户输入经过 DTO + JSR-303 校验注解
- [ ] XML 映射文件中的动态 SQL 使用 `<if>` / `<where>` 标签

### 4. Insecure Design（不安全设计）
- [ ] 规则变更需审计日志记录
- [ ] 风险评分结果不可被未授权用户修改
- [ ] 决策记录创建后不可修改（仅可追加备注）

### 5. Security Misconfiguration（安全配置错误）
- [ ] DEBUG = False（生产环境）
- [ ] 默认账号/密码已修改
- [ ] 错误响应不泄露堆栈信息
- [ ] 依赖包版本无已知漏洞（定期 `npm audit` / `mvn org.owasp:dependency-check-maven:check`）

### 6. Vulnerable and Outdated Components（过时组件）
- [ ] 定期更新依赖包版本
- [ ] 使用 `npm audit` / `mvn dependency:check` 扫描漏洞

### 7. Identification and Authentication Failures（认证失效）
- [ ] 登录接口有失败次数限制（防爆破）
- [ ] JWT token 可主动吊销（logout）
- [ ] refresh_token 安全存储

### 8. Software and Data Integrity Failures（数据完整性失效）
- [ ] 规则条件表达式有合法性校验
- [ ] 评分维度数据有完整性验证
- [ ] 审计日志不可篡改

### 9. Security Logging and Monitoring Failures（日志监控失效）
- [ ] 关键操作有日志记录（含 request_id）
- [ ] 异常登录、权限越权等安全事件有告警
- [ ] 日志中不记录密码、token 等敏感信息

### 10. Server-Side Request Forgery (SSRF)（服务端请求伪造）
- [ ] 对外部 URL 请求有白名单限制
- [ ] 不直接转发用户提供的 URL 请求

---

## 风控系统专项安全要求

- [ ] 规则引擎执行有超时限制，防止无限循环
- [ ] 风险评分算法不被外部用户直接访问
- [ ] 审计日志表权限：只允许 INSERT，禁止 UPDATE/DELETE
- [ ] 高风险事件处理有双人复核机制（可选）
