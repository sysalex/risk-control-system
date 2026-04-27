# Harness 经验与踩坑记录

> 记录可复用经验、重复错误和重要决策。
> 不替代 `docs/task-list.md` 和 `CHANGELOG.md`，仅补充上下文。

## 经验教训

### 环境配置

- IDEA 项目 JDK `ms-21` 位于 `C:\Users\Administrator\.jdks\ms-21.0.10`；命令行默认 `JAVA_HOME` 仍可能指向 Java 8，运行 Maven 前需显式切到 JDK 21。

### 测试相关

- Vitest 覆盖率阈值建议直接写入 `vite.config.ts`，让 `pnpm coverage` 自动阻断低于 80% 的改动。
- Vue/Vite 构建脚本使用 `vue-tsc --noEmit -p tsconfig.app.json && vite build`，避免 `vue-tsc -b` 在根目录生成配置文件产物。
- Windows 环境的质量门禁应提供 `.ps1` 入口；仅有 Bash 脚本会导致 PowerShell 用户无法闭合验证循环。

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
