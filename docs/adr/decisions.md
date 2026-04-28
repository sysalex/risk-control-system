# ADR-001: 技术栈选型

日期: 2026-04-24
状态: 已修改（2026-04-24 后端改为 Spring Boot + MySQL + MyBatis-Plus）

## 决策

- 前端: Vue 3 + TypeScript + Vite + Pinia
- 后端: Java 17 + Spring Boot 3.x + MyBatis-Plus 3.5.x
- 数据库: MySQL 8.0
- 构建工具: Maven 3.9+

## 理由

- Spring Boot 3.x 生态成熟，企业级开发标准框架
- MyBatis-Plus 简化 CRUD 操作，条件构造器灵活，代码生成器提升开发效率
- Java 17 LTS 是项目编译目标，生态稳定；本地开发脚本可使用 IDEA JDK 21 作为 Maven 运行时
- Vue 3 Composition API 更好 TypeScript 支持和逻辑复用
- Pinia 是 Vue 官方推荐的状态管理，比 Vuex 更简洁
- MySQL 8.0 在国内社区资源丰富，团队熟悉度高

---

# ADR-002: 分层架构

日期: 2026-04-24
状态: 已修改（2026-04-24 改为 COLA 四层架构）

## 决策

后端采用 COLA 分层架构，依赖方向严格单向：

```
starter → interfaces → application → infrastructure → domain
```

## 理由

- COLA 通过依赖方向强制解耦，Domain 层不依赖任何技术实现
- Application 层专注用例编排，Domain 层专注业务规则，避免 Service 膨胀
- 关注点分离，每层职责单一
- Domain Service 可独立单元测试（不依赖 Spring 容器）
- 风控系统业务逻辑复杂，分层有助于控制复杂度

---

# ADR-003: 认证方案

日期: 2026-04-24
状态: 已接受

## 决策

使用 JWT，access_token 30分钟过期，refresh_token 7天过期。

## 理由

- 无状态，适合前后端分离架构
- refresh_token 机制平衡安全性和用户体验
- 风控系统对安全要求高，短过期时间降低 token 泄露风险

---

# ADR-004: 审计日志不可变

日期: 2026-04-24
状态: 已接受

## 决策

审计日志表只允许 INSERT，不允许 UPDATE 和 DELETE。

## 理由

- 风控系统需要完整的操作追溯链
- 任何日志修改本身就是需要审计的行为
- 存储成本可控，审计日志数据量不大

---

# ADR-005: 后端分层架构 — COLA 模式

日期: 2026-04-24
状态: 已接受

## 决策

后端采用 COLA 分层架构，Maven 多模块实现，依赖方向严格单向：

```
starter → interfaces → application → infrastructure → domain
                └───────────── Common（跨层共享）
```

**模块划分**：

| 模块 | artifactId | 职责 | 依赖 |
|------|-----------|------|------|
| Starter | `risk-starter` | 入口、全局配置 | interfaces |
| Interfaces | `risk-interfaces` | Controller、拦截器 | application |
| Application | `risk-application` | 用例编排、事务管理 | infrastructure |
| Domain | `risk-domain` | 实体、纯业务规则 | common |
| Infrastructure | `risk-infrastructure` | Mapper、XML 映射 | domain + MyBatis-Plus |
| Common | `risk-common` | 异常、响应、DTO | web + validation |

**父 POM**：`backend/pom.xml` 通过 `<modules>` 声明 6 个子模块，`<dependencyManagement>` 统一版本管理。
每个子模块有独立 `pom.xml`，只能声明对下一层的依赖，跨层引用会在编译时报错。

## 理由

- COLA 架构通过依赖方向强制解耦，Domain 层不依赖任何技术实现
- Application 层专注用例编排，Domain 层专注业务规则，职责清晰
- 与传统的 `Controller → Service → Mapper` 扁平结构相比，增加了 Application/Domain 的分离，避免 Service 层膨胀成"上帝类"
- 采用 Maven 多模块固化依赖边界，避免接口层、应用层、领域层之间发生隐式跨层引用
- 若未来需要拆分为微服务，现有模块边界可作为服务拆分和发布单元划分的基础

## 与传统分层的对比

| 维度 | 传统三层 | COLA 四层 |
|------|---------|-----------|
| 业务逻辑位置 | Service 层（混杂编排和规则） | Application（编排）+ Domain（规则） |
| Domain 独立性 | 无，与 Service 耦合 | 纯业务规则，不依赖技术实现 |
| 依赖方向 | 单向：Controller → Service → Mapper | 单向：starter → interfaces → application → infrastructure → domain |
| 可测试性 | Service 难独立测试 | Domain Service 可独立单元测试 |
| 适用场景 | 简单 CRUD 项目 | 业务逻辑复杂的中大型项目 |
