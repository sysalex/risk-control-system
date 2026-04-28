# 风控系统

基于 Harness Engineering 规范开发的 Web 风控系统。

## 技术栈

- 前端：Vue 3 + TypeScript + Vite + Pinia + Element Plus
- 后端：Java 17 + Spring Boot 3.x + MyBatis-Plus 3.5.x
- 数据库：MySQL 8.0

## 快速开始

### 前置条件

- [Docker](https://docs.docker.com/get-docker/)
- [Maven 3.9+](https://maven.apache.org/download.cgi)（Java 包管理）
- [pnpm](https://pnpm.io/installation)（Node 包管理）
- JDK 17+

### 安装依赖（首次）

```bash
cd backend
mvn test

cd ../frontend
pnpm install
```

本地数据库和 `.env` 按 [CLAUDE.md](./CLAUDE.md) 中的开发环境配置准备。

### 启动开发服务器

```bash
cd backend
mvn spring-boot:run

cd ../frontend
pnpm dev
```

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:5173 |
| 后端 API | http://localhost:8080 |
| 健康检查 | http://localhost:8080/health |
| 运行指标 | http://localhost:8080/api/v1/metrics |

### 运行测试

```bash
cd backend
mvn test

cd ../frontend
pnpm vitest run
```

### 质量门禁检查

```bash
bash scripts/check.sh
```

Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/check.ps1
```

等同于 Stop Hook 自动执行的检查（lint + 类型检查），提交前手动运行。

---

## 开发规范

详见 [CLAUDE.md](./CLAUDE.md) — Harness Engineering 核心规范。

## 文档索引

| 文档 | 说明 |
|------|------|
| [CLAUDE.md](./CLAUDE.md) | Harness 核心规范（架构约束、编码规范、安全规范） |
| [docs/architecture.md](./docs/architecture.md) | 系统架构设计、ER 图、业务流程 |
| [docs/api-spec.md](./docs/api-spec.md) | REST API 接口规范 |
| [docs/domain-model.md](./docs/domain-model.md) | 领域模型与业务规则 |
| [docs/task-list.md](./docs/task-list.md) | 开发任务计划清单 |
| [docs/definition-of-done.md](./docs/definition-of-done.md) | 完成标准（DoD） |
| [docs/feedback-loop.md](./docs/feedback-loop.md) | 循环机制（四层反馈） |
| [docs/adr/decisions.md](./docs/adr/decisions.md) | 架构决策记录 |
| [docs/security-checklist.md](./docs/security-checklist.md) | OWASP Top 10 安全检查清单 |
| [CHANGELOG.md](./CHANGELOG.md) | 版本变更记录 |
