# 循环机制（Feedback Loop）

> Harness Engineering 核心原则：**每个动作都必须有反馈，反馈必须闭合。**
> 没有反馈的循环是盲目执行，不是 Harness Engineering。

---

## 循环层次

```
┌─────────────────────────────────────────────────────────┐
│  L1：工具调用级（毫秒）                                   │
│  Write/Edit → PostToolUse Hook → checkstyle/spotless/prettier 自动修复  │
│  Bash → PreToolUse Hook → 危险命令拦截                   │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  L2：任务级（分钟）                                       │
│  实现代码 → 运行测试 → 覆盖率检查 → 满足 DoD → 标记完成  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  L3：会话级（小时）                                       │
│  Stop Hook → lint + 类型检查 + DoD 提醒 → 推送代码       │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  L4：阶段级（天）                                         │
│  阶段完成 → 回顾文档 → 更新 CHANGELOG → 更新任务清单      │
└─────────────────────────────────────────────────────────┘
```

---

## L1：工具调用级反馈（自动，已配置）

| 触发 | 反馈 | 配置位置 |
|------|------|---------|
| 写入/编辑 `.java` 文件 | checkstyle + spotless check | settings.json PostToolUse |
| 写入/编辑 `.vue/.ts` 文件 | prettier + eslint --fix | settings.json PostToolUse |
| 执行 `mvn test`/`vitest` | 提示检查覆盖率是否满足 DoD | settings.json PostToolUse |
| 执行危险命令 | 拦截并报错 | settings.json PreToolUse |
| 会话结束 | lint + 类型检查 + DoD 提醒 | settings.json Stop |

---

## L2：任务级反馈（Agent 执行规范）

每个任务的执行循环：

```
1. 查阅 docs/task-list.md，确认任务和依赖
2. 查阅 docs/definition-of-done.md，明确完成标准
3. 判定 SDD 级别：跳过 / 轻量 SDD / 完整 SDD
4. 需要 SDD 时先闭合 Specify / Clarify / Plan / Tasks；有待确认问题则先问用户
5. 写测试（RED）
6. 写实现（GREEN）
7. 运行测试，确认通过
8. 检查覆盖率 ≥ 80%
9. 逐项核对 DoD 清单
10. 运行 `scripts/check.sh`；Windows 使用 `powershell -ExecutionPolicy Bypass -File scripts/check.ps1`
11. 更新 docs/task-list.md 状态为 [x]
12. 更新 CHANGELOG.md
```

**禁止跳步**：不得在测试未通过时标记任务完成。

---

## L3：会话级反馈（Stop Hook 自动执行）

Stop Hook 在每次会话结束时自动运行：
- Java 编译（mvn compile）
- Java 代码规范（checkstyle）
- TypeScript 类型检查（vue-tsc）
- DoD 核查提醒
- 任务清单更新提醒

---

## L4：阶段级回顾（每阶段完成后手动执行）

每个开发阶段完成后，在 `docs/retrospectives/` 创建回顾文档：

```markdown
# 阶段 N 回顾 — YYYY-MM-DD

## 完成情况
- 计划任务：N 个
- 实际完成：N 个
- 未完成：N 个（原因）

## 质量指标
- 后端测试覆盖率：XX%
- 前端测试覆盖率：XX%
- 新增技术债务：N 条

## 遇到的问题
- 问题描述 → 解决方案

## 下阶段注意事项
- ...
```

---

## 可观测性端点（运行时反馈）

| 端点 | 用途 |
|------|------|
| `GET /health` | 存活检查，返回 `{"status": "ok"}` |
| `GET /api/v1/metrics` | 运行时指标（请求数、错误率、平均响应时间） |
| 响应头 `X-Request-ID` | 每个请求的唯一追踪 ID，关联前后端日志 |

---

## 反馈闭合检查清单

Agent 在每次工作结束前确认：

- [ ] 所有修改的文件已通过 lint/格式化
- [ ] 新增代码有测试，测试已通过
- [ ] 覆盖率未下降
- [ ] 日志中无未处理的异常
- [ ] DoD 清单已逐项核对
- [ ] 任务状态已更新
- [ ] CHANGELOG 已更新
