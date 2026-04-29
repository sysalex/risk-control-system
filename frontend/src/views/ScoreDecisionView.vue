<script setup lang="ts">
import { inject, onMounted, reactive, ref } from 'vue'

import {
  decisionApi as defaultDecisionApi,
  listRecords,
  scoreApi as defaultScoreApi,
  type DecisionResponse,
  type Payload,
  type ScoreResponse,
} from '@/api/modules'

type ScoreApi = Pick<typeof defaultScoreApi, 'list' | 'evaluate'>
type DecisionApi = Pick<typeof defaultDecisionApi, 'list'>

const scoreApi = inject<ScoreApi>('scoreApi', defaultScoreApi)
const decisionApi = inject<DecisionApi>('decisionApi', defaultDecisionApi)
const scores = ref<ScoreResponse[]>([])
const decisions = ref<DecisionResponse[]>([])
const loading = ref(false)
const form = reactive({
  subjectId: '',
})

async function loadScores() {
  const response = await scoreApi.list({ page: 1, limit: 20 })
  scores.value = listRecords(response.data.data)
}

async function loadDecisions() {
  const response = await decisionApi.list({ page: 1, limit: 20 })
  decisions.value = listRecords(response.data.data)
}

async function loadPageData() {
  loading.value = true
  try {
    await Promise.all([loadScores(), loadDecisions()])
  } finally {
    loading.value = false
  }
}

async function evaluateScore() {
  const request: Payload = {
    subjectType: 'user',
    subjectId: Number(form.subjectId),
    factors: {},
  }
  await scoreApi.evaluate(request)
  form.subjectId = ''
  await loadScores()
}

onMounted(loadPageData)
</script>

<template>
  <main class="analyst-page">
    <section class="page-header">
      <p>Analyst</p>
      <h2>评分与决策</h2>
    </section>

    <form
      class="toolbar-form"
      data-test="score-form"
      @submit.prevent="evaluateScore"
    >
      <input
        v-model="form.subjectId"
        data-test="subject-id-input"
        min="1"
        placeholder="用户 ID"
        type="number"
        required
      >
      <button type="submit">
        重新评分
      </button>
    </form>

    <section class="table-panel">
      <p
        v-if="loading"
        class="loading"
      >
        加载中
      </p>
      <h3>风险评分</h3>
      <table data-test="score-table">
        <thead>
          <tr>
            <th>主体类型</th>
            <th>主体 ID</th>
            <th>分数</th>
            <th>风险等级</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="score in scores"
            :key="score.id"
          >
            <td>{{ score.subjectType }}</td>
            <td>{{ score.subjectId }}</td>
            <td>{{ score.score }}</td>
            <td>{{ score.riskLevel }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <section class="table-panel">
      <h3>决策记录</h3>
      <table data-test="decision-table">
        <thead>
          <tr>
            <th>事件 ID</th>
            <th>决策类型</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="decision in decisions"
            :key="decision.id"
          >
            <td>{{ decision.eventId }}</td>
            <td>{{ decision.decisionType }}</td>
            <td>{{ decision.status }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </main>
</template>

<style scoped>
.analyst-page {
  display: grid;
  gap: 18px;
  padding: 24px;
}

.page-header {
  display: grid;
  gap: 4px;
}

.page-header p,
.page-header h2,
.table-panel h3 {
  margin: 0;
}

.page-header p {
  color: #0f766e;
  font-size: 12px;
  font-weight: 700;
}

.page-header h2 {
  color: #111827;
  font-size: 24px;
  letter-spacing: 0;
}

.toolbar-form,
.table-panel {
  border: 1px solid #dbe1ea;
  border-radius: 8px;
  background: #ffffff;
}

.toolbar-form {
  display: grid;
  grid-template-columns: minmax(180px, 260px) auto;
  gap: 10px;
  padding: 14px;
  justify-content: start;
}

.toolbar-form input {
  min-height: 38px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  padding: 0 10px;
}

.toolbar-form button {
  min-height: 38px;
  border: 0;
  border-radius: 6px;
  padding: 0 14px;
  color: #ffffff;
  background: #0f766e;
  font-weight: 700;
  cursor: pointer;
}

.table-panel {
  display: grid;
  gap: 10px;
  overflow-x: auto;
  padding: 12px 8px 8px;
}

.table-panel h3 {
  color: #111827;
  font-size: 16px;
  letter-spacing: 0;
}

.loading {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  border-bottom: 1px solid #edf2f7;
  padding: 12px;
  color: #334155;
  font-size: 14px;
  text-align: left;
  white-space: nowrap;
}

th {
  color: #64748b;
  font-weight: 700;
}

@media (max-width: 680px) {
  .toolbar-form {
    grid-template-columns: 1fr;
  }
}
</style>
