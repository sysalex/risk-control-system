# User 模型规格

## 背景

阶段 1.1 建立用户基础数据模型，为后续认证、用户管理、权限校验和审计日志提供数据基础。

## 目标

- 定义 User 领域模型，包含用户名、邮箱、密码哈希、角色、启用状态和审计时间字段。
- 定义用户角色枚举：`admin`、`risk_analyst`、`operator`。
- 提供 MyBatis-Plus Mapper 骨架，为阶段 2 的 UserRepository/Service 做准备。
- 新增 Flyway 初始化迁移，创建 `users` 表及唯一索引。
- 补充测试验证模型映射、默认值和迁移脚本关键约束。

## 非目标

- 不实现注册、登录、密码加密、JWT 或用户 API。
- 不创建默认用户或种子数据。
- 不创建 RiskRule/RiskEvent 等后续模型。

## 字段要求

| 字段 | 要求 |
|------|------|
| id | BIGINT 主键，自增 |
| username | 必填，唯一，最长 64 |
| email | 必填，唯一，最长 128 |
| hashed_password | 必填，最长 255 |
| role | 必填，枚举字符串 |
| is_active | 必填，默认 true |
| created_at | 创建时间 |
| updated_at | 更新时间 |

## 验收标准

- `User` 模型包含 MyBatis-Plus 表名和字段映射。
- `UserRole` 能输出数据库存储值。
- `UserMapper` 继承 `BaseMapper<User>`。
- Flyway 迁移文件创建 `users` 表、唯一索引和字段注释。
- 后端 `mvn test` 通过。

## 澄清记录

- 当前任务来自 `docs/task-list.md` 的 `1.1 User 模型 + Flyway 初始化迁移`。
- 本阶段只做结构和模型，不实现认证业务。
