# 系统架构设计

## 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                      前端 (Vue 3)                        │
│  ┌──────────┐  ┌──────────  ┌──────────┐  ┌────────┐  │
│  │  管理端   │  │ 分析师端  │  │ 操作员端  │  │ 公共组件│  │
│  └──────────  └──────────┘  └──────────┘  └────────┘  │
│                    Pinia Store + Vue Router               │
│                    Axios HTTP Client                      │
└─────────────────────────┬───────────────────────────────┘
                          │ HTTPS / REST API
┌─────────────────────────▼───────────────────────────────┐
│                   后端 (Spring Boot 3)                     │
│                                                         │
│  ┌─────────────────────────────────────────────────┐    │
│  │  Starter 层 (启动入口)                              │    │
│  │  RiskApplication  @MapperScan  配置类               │    │
│  └─────────────────────┬───────────────────────────┘    │
│  ┌─────────────────────▼───────────────────────────┐    │
│  │  Interfaces 层 (REST 入口)                         │    │
│  │  AuthController  UserController  RuleController  │    │
│  │  EventController  ScoreController  ...           │    │
│  └─────────────────────┬───────────────────────────┘    │
│  ┌─────────────────────▼───────────────────────────┐    │
│  │  Application 层 (用例编排)                          │    │
│  │  AuthService  UserService  RuleService           │    │
│  │  EventService  ScoreService  DecisionService     │    │
│  └──────────────────────┬──────────────┬───────────┘    │
│           │              │              │                │
│  ┌────────▼──────┐  ┌───▼──────────┐  ┌▼─────────────┐  │
│  │  Domain 层     │  │Domain 层      │  │Infra 层       │  │
│  │  (实体/规则)    │  │(领域服务)     │  │(数据访问)      │  │
│  │  UserEntity   │  │RuleDomainSvc │  │UserMapper    │  │
│  │  RuleEntity   │  │EventDomainSvc│  │RuleMapper    │  │
│  │  EventEntity  │  │              │  │EventMapper   │  │
│  └───────────────┘  └──────────────┘  └──────┬───────┘  │
│                                               │          │
│  ┌───────────────────────────────────────────┐│         │
│  │  Common (跨层共享: 异常/响应/配置/DTO)      │         │
│  └───────────────────────────────────────────┘         │
└───────────────────────────────────────────┼─────────────┘
                                            │ JDBC
┌───────────────────────────────────────────▼─────────────┐
│                  MySQL 8.0                                │
└─────────────────────────────────────────────────────────┘
```

## 角色权限模型

| 角色 | 权限 |
|------|------|
| admin | 全部操作，用户管理，系统配置 |
| risk_analyst | 查看/创建规则，查看风险事件，执行风险评估，查看审计报告 |
| operator | 查看风险事件，处理待审核决策 |

## 核心业务流程

### 风控规则流程

```
管理员创建规则 → 配置规则条件与动作 → 启用规则
→ 规则引擎监听数据流 → 匹配规则条件
→ 触发风险事件 → 生成风险评分 → 记录审计日志
→ 操作员/分析师处理风险事件
```

### 认证流程

```
POST /api/v1/auth/login → JWT access_token (30min) + refresh_token (7d)
→ 前端存储 token → 请求头携带 Authorization: Bearer <token>
→ 过期后用 refresh_token 换新 access_token
```

## 数据库 ER 图（核心表）

```
users
  id, username, email, hashed_password, role, created_at

risk_rules
  id, name, description, conditions(jsonb), actions(jsonb),
  priority, enabled, creator_id(→users), created_at

risk_events
  id, rule_id(→risk_rules), subject_type, subject_id,
  risk_level, status, triggered_at, resolved_at,
  resolved_by(→users), description

risk_scores
  id, subject_type, subject_id, score, max_score,
  dimensions(jsonb), evaluated_at, evaluator_id(→users)

decisions
  id, event_id(→risk_events), decision_type,
  reason, decided_by(→users), decided_at, notes

audit_logs
  id, user_id(→users), action, resource_type,
  resource_id, old_values(jsonb), new_values(jsonb),
  created_at, ip_address
```

## API 版本策略

- 当前版本：`/api/v1/`
- 版本通过 URL 路径区分
- 旧版本保持 6 个月兼容期

## 错误码规范

| HTTP 状态码 | 场景 |
|-------------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 409 | 资源冲突（如规则名重复） |
| 422 | 数据验证失败 |
| 500 | 服务器内部错误 |

## SLO（服务级别目标）

| 指标 | 目标 | 告警阈值 |
|------|------|---------|
| API p99 响应时间 | < 500ms | > 800ms 持续 5 分钟 |
| 规则引擎匹配延迟 | < 100ms | > 200ms |
| API 错误率 | < 0.1% | > 1% 持续 5 分钟 |
| 数据库连接池使用率 | < 80% | > 90% |
| 高风险事件 24h 处理率 | > 95% | < 90% |
| 系统可用性 | > 99.9% | < 99.5% |

## 安全设计

- JWT 密钥通过环境变量注入，不硬编码
- 规则变更必须记录审计日志
- 风险评分结果不可被非授权用户修改
- 密码 bcrypt 哈希，cost factor = 12
- 敏感操作（规则启停、决策变更）二次确认
