# 会话交接记录

> 每次会话结束或中断前更新本文件，用于跨会话恢复上下文。
> 不替代 `docs/task-list.md` 和 `CHANGELOG.md`，仅补充临时上下文。

## 当前会话

| 字段 | 值 |
|------|-----|
| 日期 | 2026-04-27 |
| Agent | Codex |
| 当前任务 | 1.3 RiskScore / Decision 模型 + 迁移 |
| 状态 | `[x]` 已完成 |

### 当前状态

- 已完成：0.3 前端骨架；0.4 框架缺失组件；1.1 User 模型和 `users` 初始化迁移；1.2 RiskRule/RiskEvent 模型、Mapper、Flyway V2 迁移；1.3 RiskScore/Decision 模型、Mapper、Flyway V3 迁移。
- 进行中：无。
- 阻塞项：无。

### 下一步

- 下一任务：1.4 AuditLog 模型 + 迁移。

### 验证结果

- 前端：`pnpm vitest run`、`pnpm coverage`、`pnpm type-check`、`pnpm lint`、`pnpm build` 均通过；覆盖率 Statements 100% / Branches 90.9% / Functions 100% / Lines 100%。
- 后端：使用 JDK 21 运行 `mvn test` 通过；新增 User、RiskRule、RiskEvent、RiskScore、Decision 领域模型、Mapper 注解、Flyway 迁移 SQL 关键路径测试。
- 全量门禁：`powershell -ExecutionPolicy Bypass -File scripts/check.ps1` 通过。

### 备注

- Maven 命令需临时设置 `JAVA_HOME=C:\Users\Administrator\.jdks\ms-21.0.10`，避免误用系统 Java 8。

---

## 历史会话

<!-- 旧会话记录在此 -->
