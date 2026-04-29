# 本地开发环境配置

> 本机特殊路径、网络配置和快速排障指南。

---

## 端口锁定

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端 dev server | `5173` | 固定，禁止自动切换 |
| 后端 Spring Boot | `8080` | 固定，禁止自动切换 |

端口被占用时必须先杀掉进程再启动：

```bash
# Windows - 查找并杀掉
netstat -ano | findstr ":5173"
taskkill /F /PID <PID>

# PowerShell 一键清理
Get-NetTCPConnection -LocalPort 5173,8080 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

---

## 本机环境速查

> **文件搜索优先用 `rg`（ripgrep）或 `fd`**，避免 `find` 在 Windows 下遍历慢、路径遗漏的问题。
>
> ```bash
> # 搜索可执行文件
> rg --files /e/ | rg -i mysql\.exe
> fd mysql.exe /e/
> ```

| 项目 | 路径 / 值 | 备注 |
|------|----------|------|
| Maven 本地仓库 | `E:\repository` | 非默认 `~/.m2/repository` |
| JDK (DataGrip JBR) | `E:\JetBrains\DataGrip\jbr` | 无独立 JDK 时直接用 IDE 自带的 |
| `javac` / `java` | `E:\JetBrains\DataGrip\jbr\bin\javac.exe` | 用于脚本编译或 JDBC 直连 |
| MySQL 客户端 | `E:\mysql-8.0.16-winx64\bin\mysql.exe` | 本机已安装，但不在 PATH 中 |
| MySQL JDBC 驱动 | `E:\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar` | 无客户端时的 fallback |
| 测试账号 | `admin` / `admin12345` | 与 `RegisterRequest @Size(min=8)` 对齐 |

### MySQL JDBC 直连（无 mysql.exe 时）

```bash
# 写 Java 文件
cat > E:\Fix.java <<'EOF'
import java.sql.*;
public class Fix {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/risk_db?useSSL=false&serverTimezone=Asia/Shanghai";
        try (Connection c = DriverManager.getConnection(url, "root", "")) {
            // 在这里写 SQL
            c.createStatement().executeUpdate("YOUR_SQL_HERE");
        }
    }
}
EOF

# 编译并运行
cmd //c "E:\JetBrains\DataGrip\jbr\bin\javac.exe -cp E:\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar E:\Fix.java"
cmd //c "E:\JetBrains\DataGrip\jbr\bin\java.exe -cp E:\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar;E:\ Fix"
```

---

## 镜像源配置

**后端 Maven**（`backend/pom.xml` 已配置阿里云镜像）：
```xml
<repositories>
    <repository>
        <id>aliyun</id>
        <url>https://maven.aliyun.com/repository/public</url>
    </repository>
</repositories>
```

**前端 npm**（`frontend/.npmrc` 已配置淘宝镜像）：
```ini
registry = https://registry.npmmirror.com
```

---

## 数据库配置

本地开发使用 root 用户，**密码为空**（不是 `root`）。

**生产环境必须创建独立用户并设置强密码**。

---

## 配置文件

`backend/risk-starter/src/main/resources/application.yml`（提交到 Git，只允许保留本地开发默认值）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/risk_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password:
```

`frontend/.env`（不提交到 Git）：
```env
VITE_API_BASE_URL=http://localhost:8080
```

---

## 网络超时 / 离线开发 Fallback

若 `mvn` 因网络问题无法下载插件：

1. 优先在 IDEA 中点击运行（IDEA 的 Maven 集成对本地仓库缓存更宽容）
2. CLI 环境需确保 `mvn install -DskipTests` 已完整执行过一次，使所有插件进入本地仓库
3. 避免使用 `-o`（offline）启动未 `install` 的多模块项目，内部模块依赖会解析失败

---

## 种子数据

### 开发环境默认账号

Flyway 迁移脚本只建表不插数据。首次启动后没有默认用户，需手动注册：

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@example.com","password":"admin12345"}'
```

**注册时若用户名为 `admin`，系统会自动分配 `ADMIN` 角色**（其他用户名默认 `OPERATOR`）。无需手动 UPDATE。

### 业务测试数据

`scripts/seed-data.sql` 包含规则、风险事件、评分、决策、审计日志的示例数据，用于开发和演示：

```bash
# 在 mysql.exe 中执行
source scripts/seed-data.sql;
```

**原则**：
- 种子数据不纳入 Flyway 迁移（避免污染生产环境）
- E2E 测试如需前置数据，应在 `auth.setup.ts` 或测试 `beforeAll` 中通过 API 创建，而非直接操作数据库
- 数据库清空后重新启动，`auth.setup.ts` 会自动重新注册 admin 账号
