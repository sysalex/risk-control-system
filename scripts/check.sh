#!/bin/bash
# scripts/check.sh - manual quality gate checks

set -e

PASS=true
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "========================================"
echo " 质量门禁检查"
echo "========================================"

java_major_version() {
    local output
    output="$(java -version 2>&1 || true)"
    if [[ "$output" =~ version\ \"1\.([0-9]+) ]]; then
        echo "${BASH_REMATCH[1]}"
        return
    fi
    if [[ "$output" =~ version\ \"([0-9]+) ]]; then
        echo "${BASH_REMATCH[1]}"
        return
    fi
    echo "0"
}

use_compatible_jdk() {
    local major
    major="$(java_major_version)"
    if [ "$major" -ge 17 ]; then
        return
    fi

    local idea_jdk="$HOME/.jdks/ms-21.0.10"
    if [ -x "$idea_jdk/bin/java" ]; then
        export JAVA_HOME="$idea_jdk"
        export PATH="$JAVA_HOME/bin:$PATH"
    fi
}

run_step() {
    local name="$1"
    local workdir="$2"
    shift 2

    echo ""
    echo ">>> $name"
    if (cd "$workdir" && "$@"); then
        echo "[OK] $name"
    else
        echo "[FAIL] $name"
        PASS=false
    fi
}

use_compatible_jdk

if [ -f "$ROOT/backend/pom.xml" ]; then
    run_step "Java 测试 (Maven)" "$ROOT/backend" mvn test
else
    echo "[SKIP] backend/pom.xml not found"
fi

if [ -f "$ROOT/frontend/package.json" ]; then
    run_step "TypeScript 类型检查" "$ROOT/frontend" pnpm type-check
    run_step "前端 Lint" "$ROOT/frontend" pnpm lint
    run_step "前端测试覆盖率" "$ROOT/frontend" pnpm coverage
    run_step "前端生产构建" "$ROOT/frontend" pnpm build
else
    echo "[SKIP] frontend/package.json not found"
fi

echo ""
echo "========================================"
if [ "$PASS" = true ]; then
    echo " 质量门禁通过"
    echo "========================================"
else
    echo " 质量门禁未通过，请修复后再提交"
    echo "========================================"
    exit 1
fi
