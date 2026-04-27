# 框架缺失组件规格

## 背景

阶段 0.4 用于补齐框架级基础设施，使后续数据库、认证和前端业务页面可以复用统一日志、异常和质量门禁能力。

## 目标

- 提供跨平台质量门禁入口，解决 Windows PowerShell 环境无法直接运行 `scripts/check.sh` 的问题。
- 修正现有质量脚本的检查范围，确保后端多模块测试、前端类型检查、lint、测试和覆盖率被真实执行。
- 建立前端日志工具，禁止业务代码直接使用 `console.log`。
- 建立前端全局错误上报入口，捕获 Vue error handler、unhandled rejection 和 window error。
- 为新增日志、异常上报和质量脚本配置补测试。

## 非目标

- 不实现后端业务异常子类；后端通用 `AppException` 和 `GlobalExceptionHandler` 已在阶段 0.2 存在。
- 不接入远程日志服务或监控平台；本阶段只保留可替换的上报适配点。
- 不实现认证、数据库迁移或业务 API。

## 验收标准

- Windows 下可运行 `powershell -ExecutionPolicy Bypass -File scripts/check.ps1`。
- `scripts/check.sh` 和 `scripts/check.ps1` 均覆盖后端 `mvn test`、前端 `pnpm type-check`、`pnpm lint`、`pnpm coverage`、`pnpm build`。
- 前端 logger 和 error reporting 有 Vitest 覆盖。
- `docs/task-list.md`、`CHANGELOG.md`、`session-handoff.md` 同步更新。

## 澄清记录

- 当前任务来自 `docs/task-list.md` 的 `0.4 补全框架缺失组件（日志、异常体系、任务清单、质量门禁）`。
- 质量门禁脚本将优先使用当前环境变量；如检测到 Java 版本低于 17，则尝试使用 IDEA 已配置的 `C:\Users\Administrator\.jdks\ms-21.0.10`。
