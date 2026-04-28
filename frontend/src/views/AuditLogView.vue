<script setup lang="ts">
import { inject, onMounted, ref } from 'vue'

import { auditApi as defaultAuditApi, type AuditLogResponse } from '@/api/modules'

type AuditApi = Pick<typeof defaultAuditApi, 'list'>

const auditApi = inject<AuditApi>('auditApi', defaultAuditApi)
const logs = ref<AuditLogResponse[]>([])
const loading = ref(false)

async function loadLogs() {
  loading.value = true
  try {
    const response = await auditApi.list({ page: 1, limit: 20 })
    logs.value = response.data.data ?? []
  } finally {
    loading.value = false
  }
}

onMounted(loadLogs)
</script>

<template>
  <main class="analyst-page">
    <section class="page-header">
      <p>Analyst</p>
      <h2>审计日志</h2>
    </section>

    <section class="table-panel">
      <p
        v-if="loading"
        class="loading"
      >
        加载中
      </p>
      <table data-test="audit-table">
        <thead>
          <tr>
            <th>用户 ID</th>
            <th>操作</th>
            <th>资源</th>
            <th>资源 ID</th>
            <th>IP</th>
            <th>时间</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="log in logs"
            :key="log.id"
          >
            <td>{{ log.userId }}</td>
            <td>{{ log.action }}</td>
            <td>{{ log.resourceType }}</td>
            <td>{{ log.resourceId ?? '-' }}</td>
            <td>{{ log.ipAddress }}</td>
            <td>{{ log.createdAt }}</td>
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
</style>
