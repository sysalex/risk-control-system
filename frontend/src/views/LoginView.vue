<script setup lang="ts">
import { inject, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { setAuthTokens } from '@/api/http'
import { authApi, type LoginRequest } from '@/api/modules'

type LoginHandler = (request: LoginRequest) => Promise<unknown>

const login = inject<LoginHandler>('login', (request) => authApi.login(request))
const form = reactive<LoginRequest>({
  username: '',
  password: '',
})
const router = useRouter()
const submitting = ref(false)
const errorMessage = ref('')

async function submitLogin() {
  errorMessage.value = ''
  submitting.value = true
  try {
    const response = await login({ ...form })
    if (response && typeof response === 'object' && 'data' in response) {
      const tokenResponse = response as { data?: { data?: { accessToken: string; refreshToken: string } } }
      const tokens = tokenResponse.data?.data
      if (tokens) {
        setAuthTokens(tokens.accessToken, tokens.refreshToken)
      }
    }
    await router.push('/')
  } catch {
    errorMessage.value = '登录失败，请检查用户名和密码。'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="login-view">
    <form
      class="login-panel"
      data-test="login-form"
      @submit.prevent="submitLogin"
    >
      <div class="login-heading">
        <p class="login-kicker">
          Risk Control
        </p>
        <h1>风控系统</h1>
      </div>

      <label class="field">
        <span>用户名</span>
        <input
          v-model="form.username"
          data-test="username-input"
          autocomplete="username"
          required
        >
      </label>

      <label class="field">
        <span>密码</span>
        <input
          v-model="form.password"
          data-test="password-input"
          type="password"
          autocomplete="current-password"
          required
        >
      </label>

      <p
        v-if="errorMessage"
        class="error-message"
      >
        {{ errorMessage }}
      </p>

      <button
        class="login-button"
        type="submit"
        :disabled="submitting"
      >
        {{ submitting ? '登录中' : '登录' }}
      </button>
    </form>
  </section>
</template>

<style scoped>
.login-view {
  display: grid;
  min-height: 100vh;
  place-items: center;
  padding: 24px;
  background: #f7f8fa;
}

.login-panel {
  display: grid;
  width: min(100%, 380px);
  gap: 18px;
  padding: 28px;
  border: 1px solid #d9dee7;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 18px 45px rgba(17, 24, 39, 0.1);
}

.login-heading {
  display: grid;
  gap: 6px;
}

.login-kicker {
  margin: 0;
  color: #0f766e;
  font-size: 13px;
  font-weight: 700;
}

.login-heading h1 {
  margin: 0;
  color: #111827;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 0;
}

.field {
  display: grid;
  gap: 8px;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
}

.field input {
  width: 100%;
  min-height: 42px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  padding: 0 12px;
  color: #111827;
  background: #ffffff;
}

.field input:focus {
  border-color: #0f766e;
  outline: 3px solid rgba(15, 118, 110, 0.16);
}

.error-message {
  margin: 0;
  color: #b91c1c;
  font-size: 13px;
}

.login-button {
  min-height: 42px;
  border: 0;
  border-radius: 6px;
  color: #ffffff;
  background: #0f766e;
  font-weight: 700;
  cursor: pointer;
}

.login-button:disabled {
  cursor: wait;
  opacity: 0.7;
}
</style>
