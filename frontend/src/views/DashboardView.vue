<template>
  <main class="dashboard-view">
    <section class="dashboard-header">
      <p class="section-kicker">
        Overview
      </p>
      <h2 data-test="dashboard-title">
        Dashboard
      </h2>
    </section>

    <section class="metric-grid">
      <article
        class="metric-card"
        data-test="metric-events"
      >
        <span>待处理事件</span>
        <strong>18</strong>
      </article>
      <article
        class="metric-card"
        data-test="metric-decisions"
      >
        <span>今日决策</span>
        <strong>126</strong>
      </article>
      <article class="metric-card">
        <span>高风险主体</span>
        <strong>7</strong>
      </article>
      <article class="metric-card">
        <span>启用规则</span>
        <strong>24</strong>
      </article>
    </section>

    <section class="work-panel">
      <h3>近期风险概览</h3>
      <div class="risk-bars">
        <span class="risk-bar high">高</span>
        <span class="risk-bar medium">中</span>
        <span class="risk-bar low">低</span>
      </div>
    </section>
  </main>
</template>

<style scoped>
.dashboard-view {
  display: grid;
  gap: 20px;
  padding: 24px;
}

.dashboard-header {
  display: grid;
  gap: 4px;
}

.section-kicker,
.dashboard-header h2 {
  margin: 0;
}

.section-kicker {
  color: #0f766e;
  font-size: 12px;
  font-weight: 700;
}

.dashboard-header h2 {
  color: #111827;
  font-size: 24px;
  letter-spacing: 0;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.metric-card,
.work-panel {
  border: 1px solid #dbe1ea;
  border-radius: 8px;
  background: #ffffff;
}

.metric-card {
  display: grid;
  gap: 12px;
  min-height: 112px;
  padding: 16px;
}

.metric-card span {
  color: #64748b;
  font-size: 14px;
}

.metric-card strong {
  color: #111827;
  font-size: 30px;
  letter-spacing: 0;
}

.work-panel {
  display: grid;
  gap: 16px;
  padding: 18px;
}

.work-panel h3 {
  margin: 0;
  color: #111827;
  font-size: 18px;
  letter-spacing: 0;
}

.risk-bars {
  display: grid;
  gap: 10px;
}

.risk-bar {
  min-height: 36px;
  border-radius: 6px;
  padding: 8px 12px;
  color: #ffffff;
  font-weight: 700;
}

.risk-bar.high {
  width: 86%;
  background: #b91c1c;
}

.risk-bar.medium {
  width: 58%;
  background: #b45309;
}

.risk-bar.low {
  width: 32%;
  background: #15803d;
}

@media (max-width: 900px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .dashboard-view {
    padding: 16px;
  }

  .metric-grid {
    grid-template-columns: 1fr;
  }
}
</style>
