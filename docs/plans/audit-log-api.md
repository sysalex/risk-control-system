# 审计日志 API 实施计划

## 文件计划

- `risk-application`
  - 新增 `AuditLogResponse`、`CreateAuditLogRequest`
  - 新增 `AuditLogService`、`AuditLogServiceImpl`
  - 新增 `AuditLogServiceTest`
- `risk-common`
  - 新增 `AuditOperation` 注解
- `risk-interfaces`
  - 新增 `AuditLogController`
  - 新增 `AuditOperationAspect`
  - 在现有写接口补充 `@AuditOperation`
- `risk-starter`
  - 新增 `AuditLogControllerTest`
  - 新增 `AuditOperationAspectTest`
  - 现有控制器测试 Mock 审计服务，避免审计落库干扰接口测试边界

## TDD 步骤

1. 编写 `AuditLogServiceTest`，覆盖记录、详情、404、分页筛选。
2. 实现审计 DTO、服务接口和服务实现。
3. 编写 `AuditLogControllerTest`，覆盖管理员查询、非管理员 403、详情 404。
4. 实现审计查询 Controller。
5. 编写 `AuditOperationAspectTest`，覆盖写操作成功后自动构造审计记录。
6. 实现审计注解和切面，并在写接口补充注解。
7. 运行后端测试和全量质量门禁。

## 风险与约束

- 审计切面不能影响主业务成功响应，因此切面内部记录失败只写日志。
- 审计查询属于敏感接口，必须强制管理员权限。
- 当前阶段只记录成功响应快照，修改前快照后续作为独立增强处理。

## 验证命令

```bash
cd backend
mvn -pl risk-starter -am test
```

```powershell
./scripts/check.ps1
```
