# 不可变约束与护栏

本文件记录项目中的硬约束。与普通建议不同，这些规则默认不能破坏。

## 架构不变量

### 后端

必须遵守 COLA 分层依赖方向（Maven 模块依赖强制）：

```
starter → interfaces → application → infrastructure → domain
                └───────────── Common（跨层共享）
```

| 模块 | 职责 | 禁止 |
|----|------|------|
| **Starter** | 启动入口、全局配置、`@MapperScan` | 不含业务逻辑，不处理请求 |
| **Interfaces** | Controller 接收请求、参数校验、响应格式化 | 不含业务逻辑，不调用 Mapper |
| **Application** | 用例编排、事务管理、缓存策略 | 不直接操作数据库，不写纯业务规则 |
| **Domain** | 实体定义、纯业务规则、领域服务 | 不依赖外部技术（数据库、缓存、MQ） |
| **Infrastructure** | Mapper、XML 映射、外部服务集成 | 不含业务逻辑 |
| **Common** | 异常、响应、配置、DTO | 不含业务逻辑 |

禁止：
- 跨层调用（Controller → Mapper、Controller → Domain Service）
- Service 直接操作数据库（绕过 Mapper）
- 在 Mapper 中写业务逻辑
- Domain 层依赖数据库/缓存/消息队列等技术实现
- 任何模块跨越 Maven 依赖链引用（如 interfaces 直接依赖 domain）

### 前端

必须遵守：

```
View → Store → API Layer → Backend
```

禁止：
- View 直接发 HTTP 请求
- 业务状态绕过 Pinia Store
- API 调用散落在页面组件中

## 质量护栏

禁止：
- 跳过测试后再补
- 跳过 Code Review
- 跳过 DoD 核查
- 通过删测试来让构建通过

## 安全护栏

禁止：
- 硬编码密码、密钥、Token
- 拼接 SQL（必须使用 MyBatis-Plus，`#{}` 参数化，禁止 `${}`）
- 绕过权限校验
- 在日志中打印敏感信息
- 修改或删除审计日志

## 日志与异常

必须：
- 关键操作有日志
- 异常有统一处理（继承 `AppException`，`@ControllerAdvice` 处理）
- 日志尽可能带 `request_id`

禁止：
- `System.out.println()` 直接输出
- `console.log` 直接输出
- 空 `catch` 块

## 变更护栏

以下事项默认需要确认：
- 删除文件
- 修改数据库 schema（必须通过 Flyway 迁移）
- 修改 API 契约
- 修改认证授权逻辑
- 修改生产相关配置

## 故障与降级

### 服务依赖故障
- 下游 HTTP 服务调用必须设置超时（默认 3s）和重试上限（默认 2 次）
- 核心路径（如风控决策）下游故障时，必须走降级策略（如返回"人工审核"而非抛异常阻断）
- 非核心路径（如审计日志上报）下游故障时，允许异步降级 + 本地队列缓冲

### 数据库与迁移
- Flyway 迁移失败时，禁止手动修改 `flyway_schema_history` 表绕过；必须回滚至上一稳定版本或修复迁移脚本
- 生产环境数据订正必须：备份 → 在 staging 验证 → 双人 review → 低峰期执行 → 事后验证

### 运行时异常
- 未预期异常不得直接暴露给客户端；必须映射为统一错误码，同时记录完整堆栈
- 内存/线程池耗尽前必须触发告警；禁止无限制创建线程或使用无界队列

## 使用方式

- 如果某项改动与本文件冲突，先停下，不要继续写
- 如果确实需要突破护栏，必须在任务记录中注明原因和回滚方式
