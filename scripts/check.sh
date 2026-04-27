#!/bin/bash
# scripts/check.sh - manual quality gate checks

set -e

echo "========================================"
echo " 质量门禁检查"
echo "========================================"

PASS=true
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

# --- Java Lint/Format ---
echo ""
echo ">>> Java 代码规范 (checkstyle + spotless)..."
if [ -d "$ROOT/backend" ]; then
    cd "$ROOT/backend"
    if [ -f "pom.xml" ]; then
        mvn checkstyle:check spotless:check 2>/dev/null && echo "[OK] checkstyle + spotless" || { echo "[FAIL] checkstyle + spotless"; PASS=false; }
    else
        echo "[SKIP] backend/pom.xml not found"
    fi
else
    echo "[SKIP] backend/ not found"
fi

# --- Java Compile ---
echo ""
echo ">>> Java 编译..."
if [ -d "$ROOT/backend" ]; then
    cd "$ROOT/backend"
    if [ -f "pom.xml" ]; then
        mvn compile 2>/dev/null && echo "[OK] compile" || { echo "[FAIL] compile"; PASS=false; }
    else
        echo "[SKIP] backend/pom.xml not found"
    fi
else
    echo "[SKIP] backend/ not found"
fi

# --- Java Tests ---
echo ""
echo ">>> Java 测试 (JUnit 5)..."
if [ -d "$ROOT/backend" ]; then
    cd "$ROOT/backend"
    if [ -d "src/test" ]; then
        mvn test 2>/dev/null && echo "[OK] junit" || { echo "[FAIL] junit"; PASS=false; }
    else
        echo "[SKIP] backend/src/test not found"
    fi
fi

# --- TypeScript Type Check ---
echo ""
echo ">>> TypeScript 类型检查 (vue-tsc)..."
if [ -d "$ROOT/frontend" ]; then
    cd "$ROOT/frontend"
    if [ -f "node_modules/.bin/vue-tsc" ]; then
        pnpm vue-tsc --noEmit 2>/dev/null && echo "[OK] vue-tsc" || { echo "[FAIL] vue-tsc"; PASS=false; }
    else
        echo "[SKIP] 前端依赖未安装"
    fi
else
    echo "[SKIP] frontend/ not found"
fi

# --- Frontend Lint ---
echo ""
echo ">>> 前端 Lint (eslint)..."
if [ -d "$ROOT/frontend" ]; then
    cd "$ROOT/frontend"
    if [ -f "node_modules/.bin/eslint" ]; then
        pnpm eslint src/ 2>/dev/null && echo "[OK] eslint" || { echo "[FAIL] eslint"; PASS=false; }
    else
        echo "[SKIP] 前端依赖未安装"
    fi
fi

# --- Frontend Tests ---
echo ""
echo ">>> 前端测试 (vitest)..."
if [ -d "$ROOT/frontend" ]; then
    cd "$ROOT/frontend"
    if [ -f "node_modules/.bin/vitest" ]; then
        pnpm vitest run 2>/dev/null && echo "[OK] vitest" || { echo "[FAIL] vitest"; PASS=false; }
    else
        echo "[SKIP] 前端依赖未安装"
    fi
fi

# --- Result ---
echo ""
echo "========================================"
if [ "$PASS" = true ]; then
    echo " 质量门禁通过"
    echo "========================================"
else
    echo " 质量门禁未通过，请修复后再提交"
    echo " 提示：查阅 docs/definition-of-done.md"
    echo "========================================"
    exit 1
fi
