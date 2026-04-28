# User 模型实现计划

## 范围

实现阶段 1.1：User 模型 + Flyway 初始化迁移。

## 文件计划

- `backend/risk-domain/src/main/java/com/harness/risk/domain/user/UserRoleEnums.java`
- `backend/risk-domain/src/main/java/com/harness/risk/domain/user/User.java`
- `backend/risk-domain/src/test/java/com/harness/risk/domain/user/UserTest.java`
- `backend/risk-infrastructure/src/main/java/com/harness/risk/infrastructure/mapper/UserMapper.java`
- `backend/risk-infrastructure/src/test/java/com/harness/risk/infrastructure/mapper/UserMapperTest.java`
- `backend/risk-starter/src/main/resources/db/migration/V1__create_users_table.sql`
- `backend/risk-starter/src/test/java/com/harness/risk/starter/migration/UserMigrationTest.java`
- Maven POM：补 MyBatis annotation、Flyway、测试依赖。

## TDD 步骤

1. 先写 User 模型测试，验证默认启用、角色存储值和注解映射。
2. 写 UserMapper 测试，验证继承 `BaseMapper<User>` 和泛型边界。
3. 写迁移脚本测试，验证表名、字段、唯一索引、注释和默认值。
4. 运行后端目标测试确认 RED。
5. 实现模型、Mapper、迁移和依赖配置。
6. 运行 `mvn test` 和 `scripts/check.ps1`。

## 风险与约束

- 数据库 schema 变更必须通过 Flyway，不手写运行 SQL。
- 迁移不插入业务数据。
- Domain 层为符合当前项目 MyBatis-Plus 约定会使用表/字段注解；后续如要保持纯 Domain，需要另起 ADR 调整模型边界。

## 验证命令

```powershell
cd backend
$env:JAVA_HOME="$env:USERPROFILE\.jdks\ms-21.0.10"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn test

cd ..
powershell -ExecutionPolicy Bypass -File scripts/check.ps1
```
