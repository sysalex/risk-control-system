<template>
  <section
    class="main-layout"
    data-test="main-layout"
  >
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark">RC</span>
        <span>风控系统</span>
      </div>

      <nav class="nav-list">
        <RouterLink
          to="/"
          data-test="nav-dashboard"
        >
          Dashboard
        </RouterLink>
        <RouterLink to="/rules">
          规则管理
        </RouterLink>
        <RouterLink to="/events">
          风险事件
        </RouterLink>
        <RouterLink to="/decisions">
          决策记录
        </RouterLink>
        <RouterLink
          to="/audit-logs"
          data-test="nav-audit"
        >
          审计日志
        </RouterLink>
      </nav>
    </aside>

    <div class="workspace">
      <header class="topbar">
        <div>
          <p class="topbar-kicker">
            Risk Operations
          </p>
          <h1>运营工作台</h1>
        </div>
        <RouterLink
          class="logout-link"
          to="/login"
        >
          退出
        </RouterLink>
      </header>

      <RouterView />
    </div>
  </section>
</template>

<style scoped>
.main-layout {
  display: grid;
  min-height: 100vh;
  grid-template-columns: 240px minmax(0, 1fr);
  background: #f4f6f8;
}

.sidebar {
  display: grid;
  align-content: start;
  gap: 28px;
  border-right: 1px solid #dbe1ea;
  padding: 22px 16px;
  background: #ffffff;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #111827;
  font-weight: 700;
}

.brand-mark {
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  border-radius: 6px;
  color: #ffffff;
  background: #0f766e;
  font-size: 13px;
}

.nav-list {
  display: grid;
  gap: 6px;
}

.nav-list a {
  border-radius: 6px;
  padding: 10px 12px;
  color: #475569;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
}

.nav-list a.router-link-active,
.nav-list a:hover {
  color: #0f766e;
  background: #e7f5f2;
}

.workspace {
  display: grid;
  min-width: 0;
  align-content: start;
}

.topbar {
  display: flex;
  min-height: 72px;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #dbe1ea;
  padding: 14px 24px;
  background: #ffffff;
}

.topbar h1,
.topbar-kicker {
  margin: 0;
}

.topbar h1 {
  color: #111827;
  font-size: 20px;
  letter-spacing: 0;
}

.topbar-kicker {
  color: #64748b;
  font-size: 12px;
  font-weight: 700;
}

.logout-link {
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  padding: 8px 12px;
  color: #334155;
  font-size: 14px;
  text-decoration: none;
}

@media (max-width: 760px) {
  .main-layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    gap: 14px;
    border-right: 0;
    border-bottom: 1px solid #dbe1ea;
  }

  .nav-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
