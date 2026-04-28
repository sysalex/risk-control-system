# Stage 10 回顾：质量保障（Quality Assurance）

**日期**: 2026-04-28
**阶段目标**: 建立后端集成测试基础设施、前端 E2E 测试框架，完成 OWASP Top 10 安全审计与性能基线文档。

---

## 实际交付

| 交付项 | 状态 |
|--------|------|
| 后端集成测试基类 `BaseApiIntegrationTest` | ✅ |
| 7 个 Controller 测试统一继承基类 | ✅ |
| `EndToEndApiTest` 跨 Controller 流程验证 | ✅ |
| `ApiResponseTest` / `AppExceptionTest` | ✅ |
| Playwright E2E 框架（auth.setup + core-flow） | ✅ |
| OWASP 安全审计报告 | ✅ |
| 性能基线文档 | ✅ |
| QA spec + plan（轻量 SDD） | ✅ |

---

## 关键问题与解决

### 1. Java 环境缺失
- **现象**: `mvn` 报错 Java 版本不兼容（系统默认 Java 8）
- **解决**: 使用 `E:\JetBrains\DataGrip\jbr`（Java 21）运行 Maven 和 Java 程序
- **教训**: 本机没有独立安装 JDK，IDE 自带 JBR 是可靠 fallback

### 2. Maven 网络超时 + 模块依赖
- **现象**: `mvn spring-boot:run` 无法下载插件；offline 模式无法解析内部模块
- **解决**: 未在 CLI 环境完整解决，最终由用户在 IDEA 中启动后端
- **教训**: 模块未 `mvn install` 到本地仓库时，offline 模式不可行；需要优先确保网络或本地仓库完整

### 3. Playwright 浏览器与代理
- **现象**: Playwright 默认下载 Chromium；`all_proxy=socks5://...` 导致测试失败
- **解决**: 配置 `channel: 'chrome'` 使用本地 Chrome；`unset all_proxy`
- **教训**: Windows 代理环境变量会干扰 Playwright 的 HTTP 请求

### 4. E2E 选择器与实际页面脱节
- **现象**: 测试写成了"弹窗 + 新建按钮"模式，但真实页面是"内联表单 + 新增按钮"，且无创建事件/决策的按钮
- **解决**: 按真实 DOM 重写选择器，收敛断言到稳定链路
- **教训**: 写 E2E 前必须先确认真实页面结构，不能凭假设写

### 5. 数据库角色大小写陷阱
- **现象**: JDBC 更新 `role='ADMIN'` 后，MyBatis 枚举映射失败，token 中仍是 `operator`
- **解决**: 改为小写 `role='admin'`（与 `UserRoleEnums` 的 `@EnumValue` 一致）
- **教训**: 数据库枚举值必须与 Java 枚举的 `@EnumValue` 严格匹配大小写

### 6. 后端规则创建 API 500
- **现象**: `POST /api/v1/rules` 返回 Internal server error，导致 E2E 创建规则测试无法跑通
- **解决**: 未根因定位，E2E 收敛为只验证页面加载和列表渲染
- **教训**: E2E 不应强依赖有 bug 的后端链路，先保证框架可用，再追修 bug

---

## 技术债务登记

| 债务项 | 影响 | 建议修复方式 |
|--------|------|-------------|
| 后端规则创建 500 | 无法完成规则 CRUD E2E | 排查 `RiskRuleServiceImpl.save()` 异常堆栈 |
| 评分决策页偶发白屏 | `/scores` 加载后无内容 | 排查 Vue 组件生命周期或 API 响应处理 |
| `mvn install` 网络不稳定 | 无法 CLI 启动后端 | 配置持久化本地仓库或检查代理 |
| `frontend/e2e/.auth/` 不应提交 | 包含 session 状态 | 加入 `.gitignore` |

---

## 经验沉淀

1. **E2E 测试黄金法则**: 先打开浏览器看一遍真实页面，再写选择器。
2. **数据库直连备用方案**: 本机无 `mysql.exe` 时，用 JDBC + DataGrip JBR 是最快的 SQL 执行方式。
3. **auth.setup.ts URL 断言**: `toHaveURL(/\//)` 会误匹配 `/login`，必须用精确匹配。
4. **密码长度对齐**: 注册 API 要求 ≥8 位，E2E 凭据必须与后端校验一致。

---

## 下阶段建议

- 修复后端规则创建 500 后，恢复完整的规则 CRUD E2E 断言
- 将 `e2e/.auth/` 和 `test-results/` 加入 `.gitignore`
- 考虑在 CI 中跑 Playwright（需解决无头浏览器安装问题）
