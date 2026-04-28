# 阶段 10：质量保障 — 实现计划

## TDD 步骤

1. 创建 `BaseApiIntegrationTest` 基类 → 重构现有 7 个 Controller 测试继承基类 → 运行 `mvn test` 验证无回归
2. 新增 `EndToEndApiTest`（端到端链路）→ 运行 `mvn test` 验证
3. 新增 `ApiResponseTest` + `AppExceptionTest` → 运行 `mvn test` 验证 risk-common 覆盖率
4. 配置 Playwright（`channel: 'chrome'`）+ `e2e/core-flow.spec.ts` → 运行 `pnpm test:e2e` 验证
5. 安全审查（OWASP Top 10 逐项检查）→ 输出 `docs/security-audit-report.md`
6. 性能基准 → 输出 `docs/performance-baseline.md`

## 文件计划

### 新增
- `backend/risk-starter/src/test/java/com/harness/risk/BaseApiIntegrationTest.java`
- `backend/risk-starter/src/test/java/com/harness/risk/interfaces/controller/EndToEndApiTest.java`
- `backend/risk-common/src/test/java/com/harness/risk/common/response/ApiResponseTest.java`
- `backend/risk-common/src/test/java/com/harness/risk/common/exception/AppExceptionTest.java`
- `frontend/playwright.config.ts`
- `frontend/e2e/auth.setup.ts`
- `frontend/e2e/core-flow.spec.ts`
- `docs/security-audit-report.md`
- `docs/performance-baseline.md`

### 修改
- 7 个 `*ControllerTest.java` → 继承 `BaseApiIntegrationTest`
- `frontend/package.json` → 添加 `test:e2e` 脚本
- `frontend/vite.config.ts` → 排除 `e2e/**`
- `backend/risk-starter/src/test/resources/application-test.yml` → MySQL + Flyway 禁用
- `docs/task-list.md` / `CHANGELOG.md` → 状态更新

## 验证命令

```bash
# 后端
cd backend && JAVA_HOME="/e/JetBrains/DataGrip/jbr" && export JAVA_HOME && mvn test -pl risk-starter -am

# 前端单元
cd frontend && pnpm test

# 前端 E2E（需前后端同时运行）
cd frontend && pnpm test:e2e
```

## DoD 核查清单

- [ ] 后端 `mvn test` 全量通过
- [ ] 前端 `pnpm test` 全量通过
- [ ] `docs/task-list.md` 状态更新为 `[x]`
- [ ] `CHANGELOG.md` 已记录
- [ ] `docs/security-checklist.md` 逐项核对
