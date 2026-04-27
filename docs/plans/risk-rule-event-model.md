# RiskRule / RiskEvent 模型实现计划

## 范围

实现阶段 1.2：RiskRule / RiskEvent 模型 + 迁移。

## 文件计划

- `backend/risk-domain/src/main/java/com/harness/risk/domain/rule/RiskRule.java`
- `backend/risk-domain/src/main/java/com/harness/risk/domain/event/RiskLevel.java`
- `backend/risk-domain/src/main/java/com/harness/risk/domain/event/RiskEventStatus.java`
- `backend/risk-domain/src/main/java/com/harness/risk/domain/event/RiskEvent.java`
- `backend/risk-domain/src/test/java/com/harness/risk/domain/rule/RiskRuleTest.java`
- `backend/risk-domain/src/test/java/com/harness/risk/domain/event/RiskEventTest.java`
- `backend/risk-infrastructure/src/main/java/com/harness/risk/infrastructure/mapper/RiskRuleMapper.java`
- `backend/risk-infrastructure/src/main/java/com/harness/risk/infrastructure/mapper/RiskEventMapper.java`
- `backend/risk-infrastructure/src/test/java/com/harness/risk/infrastructure/mapper/RiskModelMapperTest.java`
- `backend/risk-starter/src/main/resources/db/migration/V2__create_risk_rule_event_tables.sql`
- `backend/risk-starter/src/test/java/com/harness/risk/starter/migration/RiskRuleEventMigrationTest.java`

## TDD 步骤

1. 写 RiskRule 模型测试，验证表映射、字段映射和默认值。
2. 写 RiskEvent 模型测试，验证表映射、字段映射、默认状态和风险等级/状态枚举存储值。
3. 写 Mapper 测试，验证两个 Mapper 均继承对应实体的 `BaseMapper`。
4. 写迁移脚本测试，验证表名、核心字段、外键、唯一索引和查询索引。
5. 运行目标测试确认 RED。
6. 实现模型、Mapper、V2 迁移。
7. 运行后端测试和全量质量门禁。

## 风险与约束

- schema 变更只通过 Flyway 迁移进入项目，不直接连接数据库执行 SQL。
- `risk_events.rule_id` 关联 `risk_rules.id`，`creator_id` / `resolved_by` 关联 `users.id`，保证与 V1 顺序兼容。
- 规则条件和动作暂以 JSON 文本保存，避免在规则引擎尚未确定前过早固化表达式对象。
- 不插入业务数据，避免污染后续环境初始化。

## 验证命令

```powershell
cd backend
$env:JAVA_HOME="$env:USERPROFILE\.jdks\ms-21.0.10"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn test

cd ..
powershell -ExecutionPolicy Bypass -File scripts/check.ps1
```
