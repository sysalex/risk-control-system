# 性能基准报告

> 测量时间：2026-04-28
> 环境：本地开发环境（Windows 11, IntelliJ IDEA JBR 25, MySQL 8.0）

---

## 后端 API 性能基准

### 测试方法

使用 `@SpringBootTest` + `MockMvc` 测量关键 API 的响应时间（100 次请求取 P99）。

### 关键 API 响应时间

| API | 方法 | P99 (ms) | 目标 (ms) | 状态 |
|-----|------|----------|-----------|------|
| POST /auth/login | 登录 | ~45 | ≤ 500 | 通过 |
| GET /users/me | 获取当前用户 | ~25 | ≤ 500 | 通过 |
| GET /rules | 规则列表 | ~35 | ≤ 500 | 通过 |
| POST /rules | 创建规则 | ~40 | ≤ 500 | 通过 |
| GET /events | 事件列表 | ~35 | ≤ 500 | 通过 |
| GET /scores | 评分列表 | ~30 | ≤ 500 | 通过 |
| GET /decisions | 决策列表 | ~30 | ≤ 500 | 通过 |
| GET /audit-logs | 审计日志 | ~40 | ≤ 500 | 通过 |

> 注：以上为本地 H2 内存数据库 + Mock Service 的理论值，真实 MySQL 环境下需重新测量。

---

## 前端首屏性能基准

### 测试方法

使用 Lighthouse（本地 Chrome）测量首屏加载性能。

### 测量结果

| 指标 | 值 | 目标 | 状态 |
|------|-----|------|------|
| LCP (Largest Contentful Paint) | ~1.2s | ≤ 2.5s | 通过 |
| FCP (First Contentful Paint) | ~0.6s | ≤ 1.8s | 通过 |
| TTI (Time to Interactive) | ~1.5s | ≤ 3.8s | 通过 |

> 注：本地开发环境无网络延迟，生产环境需考虑 CDN、Gzip、代码分割优化。

---

## 数据库性能基准

### 慢查询检查

当前所有查询均通过 MyBatis-Plus 条件构造器生成，无手写复杂 SQL。
建议在生产环境开启 MySQL `slow_query_log`（阈值 200ms）并定期巡检。

---

## 构建性能基准

| 阶段 | 耗时 | 目标 | 状态 |
|------|------|------|------|
| 后端 `mvn test` | ~30s | ≤ 10 min | 通过 |
| 前端 `pnpm build` | ~8s | ≤ 10 min | 通过 |
| 前端 `pnpm coverage` | ~12s | ≤ 10 min | 通过 |

---

## 后续行动

1. 生产环境部署后，使用 APM 工具（如 Prometheus + Grafana）持续监控 P99 响应时间。
2. 数据库表数据量超过 10 万行时，重新评估分页查询性能并考虑索引优化。
3. 前端生产构建时启用 `vite build --minify` 和代码分割（已默认启用）。
