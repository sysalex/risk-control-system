import { test as setup, expect } from '@playwright/test'

/**
 * 全局认证 Setup，登录管理员账号并保存 storage state。
 *
 * @author harness-agent
 * @since 2026-04-28
 */
const authFile = 'e2e/.auth/admin.json'

setup('authenticate as admin', async ({ page }) => {
  await page.goto('/login')
  await page.fill('[data-test="username-input"]', 'admin')
  await page.fill('[data-test="password-input"]', 'admin123')
  await page.click('button[type="submit"]')

  // 等待登录成功跳转到 Dashboard
  await expect(page).toHaveURL(/\//)
  await page.context().storageState({ path: authFile })
})
