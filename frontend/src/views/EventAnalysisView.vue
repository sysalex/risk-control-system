<script setup lang="ts">
import { inject, onMounted, ref } from 'vue'

import { eventApi as defaultEventApi, listRecords, type EventResponse } from '@/api/modules'

type EventApi = Pick<typeof defaultEventApi, 'list' | 'resolve'>

const eventApi = inject<EventApi>('eventApi', defaultEventApi)
const events = ref<EventResponse[]>([])
const loading = ref(false)

async function loadEvents() {
  loading.value = true
  try {
    const response = await eventApi.list({ page: 1, limit: 20 })
    events.value = listRecords(response.data.data)
  } finally {
    loading.value = false
  }
}

async function resolveEvent(id: number) {
  await eventApi.resolve(id)
  await loadEvents()
}

onMounted(loadEvents)
</script>

<template>
  <main class="analyst-page">
    <section class="page-header">
      <p>Analyst</p>
      <h2>风险事件</h2>
    </section>

    <section class="table-panel">
      <p
        v-if="loading"
        class="loading"
      >
        加载中
      </p>
      <table data-test="event-table">
        <thead>
          <tr>
            <th>主体类型</th>
            <th>主体 ID</th>
            <th>风险等级</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="event in events"
            :key="event.id"
          >
            <td>{{ event.subjectType }}</td>
            <td>{{ event.subjectId }}</td>
            <td>{{ event.riskLevel }}</td>
            <td>{{ event.status }}</td>
            <td>
              <button
                v-if="event.status !== 'resolved'"
                class="text-button"
                type="button"
                :data-test="`resolve-event-${event.id}`"
                @click="resolveEvent(event.id)"
              >
                处理
              </button>
              <span
                v-else
                class="muted"
              >
                已处理
              </span>
            </td>
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
.page-header h2 {
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

.table-panel {
  overflow-x: auto;
  border: 1px solid #dbe1ea;
  border-radius: 8px;
  background: #ffffff;
  padding: 8px;
}

.loading {
  margin: 8px;
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

.text-button {
  min-height: 36px;
  border: 0;
  border-radius: 6px;
  padding: 0 12px;
  color: #ffffff;
  background: #0f766e;
  font-weight: 700;
  cursor: pointer;
}

.muted {
  color: #64748b;
}
</style>
