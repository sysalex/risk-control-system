import { test as setup, expect } from '@playwright/test'

/**
 * 全局认证 Setup，登录管理员账号并保存 storage state。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
const authFile = 'e2e/.auth/admin.json'
const API_BASE = 'http://localhost:8080/api/v1'

setup('authenticate as admin', async ({ page, request }) => {
  // 数据库清空后需重新注册 admin 账号
  await request.post(`${API_BASE}/auth/register`, {
    data: {
      username: 'admin',
      email: 'admin@example.com',
      password: 'admin12345',
    },
  }).catch(() => {})

  await page.goto('/login')
  await page.fill('[data-test="username-input"]', 'admin')
  await page.fill('[data-test="password-input"]', 'admin12345')
  await page.click('button[type="submit"]')

  // 等待登录成功跳转到 Dashboard
  await expect(page).toHaveURL('http://localhost:5173/')
  await page.context().storageState({ path: authFile })
})
