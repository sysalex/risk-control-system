# Harness 经验与踩坑记录

> 记录可复用经验、重复错误和重要决策。
> 不替代 `docs/task-list.md` 和 `CHANGELOG.md`，仅补充上下文。

## 经验教训

### 环境配置

- IDEA 项目 JDK `ms-21` 位于 `C:\Users\Administrator\.jdks\ms-21.0.10`；命令行默认 `JAVA_HOME` 仍可能指向 Java 8，运行 Maven 前需显式切到 JDK 21。建议 Windows 开发者在 `backend/.env`（不提交）中设置 `JAVA_HOME` 别名。
- Maven 命令行与 IDEA 内置 Maven 行为不一致：IDEA 使用项目配置的 JDK 21，命令行可能使用系统默认 JDK 1.8 导致编译失败。CI 脚本应显式指定 `JAVA_HOME`。

### 测试相关

- Vitest 覆盖率阈值建议直接写入 `vite.config.ts`，让 `pnpm coverage` 自动阻断低于 80% 的改动。
- Playwright E2E 测试在 Windows 环境下，若系统环境变量 `all_proxy` 指向 SOCKS5 代理，会导致 `TypeError: Protocol "socks5:" not supported`。运行测试前需 `unset all_proxy`（Git Bash）或临时移除该环境变量。
- E2E 测试覆盖率数字不能防止 "naive test"：32 个端点全部调通不代表测试有效，必须验证真实业务边界条件（如删除被引用的资源应返回 409 而非 500）。
- Vue/Vite 构建脚本使用 `vue-tsc --noEmit -p tsconfig.app.json && vite build`，避免 `vue-tsc -b` 在根目录生成配置文件产物。
- Windows 环境的质量门禁应提供 `.ps1` 入口；仅有 Bash 脚本会导致 PowerShell 用户无法闭合验证循环。
- Flyway 迁移 SQL 位于 `risk-starter/src/main/resources/db/migration` 时，`risk-starter` 的 Maven resources 需要包含 `**/*.sql`；否则迁移测试会因 `V1__*.sql` 未进入 classpath 失败。
- **Flyway 已执行迁移文件修改后**：必须 `mvn flyway:clean`（开发环境）或新建补偿迁移，不能直接改文件后重启，否则报 "Table already exists"。
- MySQL 8 可用 `JSON` 字段承载规则条件和动作；规则引擎语义未确定前，领域模型先保留 JSON 字符串，避免过早固化表达式对象。
- 风险评分这类小数边界使用 `BigDecimal` + MySQL `DECIMAL(5,2)`，不要用 `float/double` 把精度误差带入领域和数据库契约。
- **admin 角色初始化陷阱**：`AuthServiceImpl.register()` 中不能给所有用户默认 `OPERATOR` 角色；当用户名是 `admin` 时必须显式分配 `ADMIN` 角色，否则数据库清空后重新注册的管理员会 403。

### 部署相关

- _暂无_

## 重要决策

| 日期 | 决策内容 | 理由 |
|------|---------|------|
| 2026-04-24 | 建立 Harness Engineering 规范体系 | 为后续开发提供结构化约束和反馈机制 |

## 重复错误

- _暂无_

## 临时方案

- _暂无_
