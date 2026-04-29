<script setup lang="ts">
import { inject, onMounted, reactive, ref } from 'vue'

import { listRecords, userApi as defaultUserApi, type Payload, type UserResponse } from '@/api/modules'

type UserApi = typeof defaultUserApi

const userApi = inject<UserApi>('userApi', defaultUserApi)
const users = ref<UserResponse[]>([])
const loading = ref(false)
const form = reactive({
  username: '',
  email: '',
  password: '',
  role: 'RISK_ANALYST',
})

async function loadUsers() {
  loading.value = true
  try {
    const response = await userApi.list({ page: 1, limit: 20 })
    users.value = listRecords(response.data.data)
  } finally {
    loading.value = false
  }
}

async function createUser() {
  const request: Payload = {
    username: form.username,
    email: form.email,
    password: form.password,
    role: form.role,
  }
  await userApi.create(request)
  form.username = ''
  form.email = ''
  form.password = ''
  form.role = 'RISK_ANALYST'
  await loadUsers()
}

async function deleteUser(id: number) {
  await userApi.remove(id)
  await loadUsers()
}

onMounted(loadUsers)
</script>

<template>
  <main class="admin-page">
    <section class="page-header">
      <p>Admin</p>
      <h2>用户管理</h2>
    </section>

    <form
      class="toolbar-form"
      data-test="user-form"
      @submit.prevent="createUser"
    >
      <input
        v-model="form.username"
        data-test="username-input"
        placeholder="用户名"
        required
      >
      <input
        v-model="form.email"
        data-test="email-input"
        placeholder="邮箱"
        type="email"
        required
      >
      <input
        v-model="form.password"
        data-test="password-input"
        placeholder="初始密码"
        type="password"
        required
      >
      <select v-model="form.role">
        <option value="RISK_ANALYST">
          RISK_ANALYST
        </option>
        <option value="ADMIN">
          ADMIN
        </option>
        <option value="OPERATOR">
          OPERATOR
        </option>
      </select>
      <button type="submit">
        新增用户
      </button>
    </form>

    <section class="table-panel">
      <p
        v-if="loading"
        class="loading"
      >
        加载中
      </p>
      <table data-test="user-table">
        <thead>
          <tr>
            <th>用户名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="user in users"
            :key="user.id"
          >
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.role }}</td>
            <td>{{ (user.enabled ?? user.active) ? '启用' : '停用' }}</td>
            <td>
              <button
                class="text-button danger"
                type="button"
                :data-test="`delete-user-${user.id}`"
                @click="deleteUser(user.id)"
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
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
  padding: 14px;
}

.toolbar-form input,
.toolbar-form select {
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

.text-button.danger {
  background: #b91c1c;
}

@media (max-width: 860px) {
  .toolbar-form {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
