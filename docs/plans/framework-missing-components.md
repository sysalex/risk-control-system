# 框架缺失组件实现计划

## 范围

实现阶段 0.4 的框架基础设施补齐。

## 文件计划

- `scripts/check.ps1`：新增 Windows PowerShell 质量门禁入口。
- `scripts/check.sh`：修正后端多模块测试检查和前端命令。
- `frontend/src/utils/logger.ts`：前端日志工具。
- `frontend/src/utils/error-reporting.ts`：前端错误上报入口。
- `frontend/src/utils/*.spec.ts`：日志和错误上报测试。
- `frontend/src/main.ts`：注册前端错误上报。
- `docs/specs/framework-missing-components.md`、`docs/plans/framework-missing-components.md`：SDD 产物。
- `docs/task-list.md`、`CHANGELOG.md`、`session-handoff.md`、`.harness/learnings.md`：收尾文档。

## TDD 步骤

1. 写 `logger` 测试，验证 debug/info/warn/error 调用边界。
2. 写 `error-reporting` 测试，验证 Vue error handler 和全局事件监听注册。
3. 写 `scripts/check.ps1` 静态测试，验证关键命令存在。
4. 运行测试确认失败。
5. 实现最小代码和脚本让测试通过。
6. 运行 `pnpm coverage`、`pnpm type-check`、`pnpm lint`、`pnpm build`、后端 `mvn test` 和 `scripts/check.ps1`。

## 风险与约束

- 不修改 API 契约。
- 不修改数据库 schema。
- 不引入远程日志依赖，避免阶段 0 过度设计。
- PowerShell 脚本只做本地质量门禁，不改变系统环境变量。

## 验证命令

```powershell
cd frontend
pnpm coverage
pnpm type-check
pnpm lint
pnpm build

cd ../backend
$env:JAVA_HOME="$env:USERPROFILE\.jdks\ms-21.0.10"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn test

cd ..
powershell -ExecutionPolicy Bypass -File scripts/check.ps1
```
