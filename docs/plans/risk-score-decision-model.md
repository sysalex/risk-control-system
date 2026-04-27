# RiskScore / Decision 模型实现计划

## 范围

实现阶段 1.3：RiskScore / Decision 模型 + 迁移。

## 文件计划

- `backend/risk-domain/src/main/java/com/harness/risk/domain/score/RiskScore.java`
- `backend/risk-domain/src/main/java/com/harness/risk/domain/decision/DecisionType.java`
- `backend/risk-domain/src/main/java/com/harness/risk/domain/decision/Decision.java`
- `backend/risk-domain/src/test/java/com/harness/risk/domain/score/RiskScoreTest.java`
- `backend/risk-domain/src/test/java/com/harness/risk/domain/decision/DecisionTest.java`
- `backend/risk-infrastructure/src/main/java/com/harness/risk/infrastructure/mapper/RiskScoreMapper.java`
- `backend/risk-infrastructure/src/main/java/com/harness/risk/infrastructure/mapper/DecisionMapper.java`
- `backend/risk-infrastructure/src/test/java/com/harness/risk/infrastructure/mapper/ScoreDecisionMapperTest.java`
- `backend/risk-starter/src/main/resources/db/migration/V3__create_risk_score_decision_tables.sql`
- `backend/risk-starter/src/test/java/com/harness/risk/starter/migration/RiskScoreDecisionMigrationTest.java`

## TDD 步骤

1. 写 RiskScore 模型测试，验证表映射、字段映射和默认满分值。
2. 写 Decision 模型测试，验证表映射、字段映射和决策类型枚举存储值。
3. 写 Mapper 测试，验证两个 Mapper 均继承对应实体的 `BaseMapper`。
4. 写迁移脚本测试，验证表名、核心字段、外键、唯一约束和查询索引。
5. 运行目标测试确认 RED。
6. 实现模型、Mapper、V3 迁移。
7. 运行后端测试和全量质量门禁。

## 风险与约束

- schema 变更只通过 Flyway 迁移进入项目，不直接连接数据库执行 SQL。
- V3 迁移依赖 V2 的 `risk_events` 表和 V1 的 `users` 表。
- 分数使用 `DECIMAL(5,2)`，避免浮点误差进入数据库边界。
- 维度明细暂以 JSON 文本保存，评分算法未落地前不引入类型化维度对象。

## 验证命令

```powershell
cd backend
$env:JAVA_HOME="$env:USERPROFILE\.jdks\ms-21.0.10"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn test

cd ..
powershell -ExecutionPolicy Bypass -File scripts/check.ps1
```
