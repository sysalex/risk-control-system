<script setup lang="ts">
import { inject, onMounted, reactive, ref } from 'vue'

import { listRecords, ruleApi as defaultRuleApi, type Payload, type RuleResponse } from '@/api/modules'

type RuleApi = typeof defaultRuleApi

const ruleApi = inject<RuleApi>('ruleApi', defaultRuleApi)
const rules = ref<RuleResponse[]>([])
const loading = ref(false)
const form = reactive({
  name: '',
  priority: 10,
  description: '',
})

async function loadRules() {
  loading.value = true
  try {
    const response = await ruleApi.list({ page: 1, limit: 20 })
    rules.value = listRecords(response.data.data)
  } finally {
    loading.value = false
  }
}

async function createRule() {
  const request: Payload = {
    name: form.name,
    description: form.description,
    conditions: '{}',
    actions: '{}',
    priority: form.priority,
  }
  await ruleApi.create(request)
  form.name = ''
  form.description = ''
  form.priority = 10
  await loadRules()
}

async function toggleRule(rule: RuleResponse) {
  if (rule.enabled) {
    await ruleApi.disable(rule.id)
  } else {
    await ruleApi.enable(rule.id)
  }
  await loadRules()
}

async function deleteRule(id: number) {
  await ruleApi.remove(id)
  await loadRules()
}

onMounted(loadRules)
</script>

<template>
  <main class="admin-page">
    <section class="page-header">
      <p>Admin</p>
      <h2>规则管理</h2>
    </section>

    <form
      class="toolbar-form"
      data-test="rule-form"
      @submit.prevent="createRule"
    >
      <input
        v-model="form.name"
        data-test="rule-name-input"
        placeholder="规则名称"
        required
      >
      <input
        v-model.number="form.priority"
        data-test="rule-priority-input"
        min="1"
        placeholder="优先级"
        type="number"
        required
      >
      <input
        v-model="form.description"
        placeholder="描述"
      >
      <button type="submit">
        新增规则
      </button>
    </form>

    <section class="table-panel">
      <p
        v-if="loading"
        class="loading"
      >
        加载中
      </p>
      <table data-test="rule-table">
        <thead>
          <tr>
            <th>规则名称</th>
            <th>优先级</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="rule in rules"
            :key="rule.id"
          >
            <td>{{ rule.name }}</td>
            <td>{{ rule.priority }}</td>
            <td>{{ rule.enabled ? '启用' : '停用' }}</td>
            <td class="actions">
              <button
                class="text-button"
                type="button"
                :data-test="`toggle-rule-${rule.id}`"
                @click="toggleRule(rule)"
              >
                {{ rule.enabled ? '停用' : '启用' }}
              </button>
              <button
                class="text-button danger"
                type="button"
                :data-test="`delete-rule-${rule.id}`"
                @click="deleteRule(rule.id)"
              >
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </main>
</template>

<style scoped>
.admin-page {
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

.toolbar-form,
.table-panel {
  border: 1px solid #dbe1ea;
  border-radius: 8px;
  background: #ffffff;
}

.toolbar-form {
  display: grid;
  grid-template-columns: 2fr 1fr 2fr auto;
  gap: 10px;
  padding: 14px;
}

.toolbar-form input {
  min-height: 38px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  padding: 0 10px;
}

.toolbar-form button,
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

.table-panel {
  overflow-x: auto;
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

.actions {
  display: flex;
  gap: 8px;
}

.text-button.danger {
  background: #b91c1c;
}

@media (max-width: 860px) {
  .toolbar-form {
    grid-template-columns: 1fr;
  }
}
</style>
