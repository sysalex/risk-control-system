import { test, expect } from '@playwright/test'

/**
 * 核心业务流程 E2E 测试
 *
 * 覆盖：登录 → Dashboard → 规则 → 事件 → 决策 → 审计
 *
 * @author harness-agent
 * @since 2026-04-28
 */

test.describe('登录流程', () => {
  test('用户输入正确凭据后进入 Dashboard', async ({ page }) => {
    await page.goto('/login')
    await page.fill('[data-test="username-input"]', 'admin')
    await page.fill('[data-test="password-input"]', 'admin12345')
    await page.click('button[type="submit"]')

    await expect(page).toHaveURL('http://localhost:5173/')
  })

  test('用户输入错误凭据后停留在登录页并显示错误', async ({ page }) => {
    await page.goto('/login')
    await page.fill('[data-test="username-input"]', 'admin')
    await page.fill('[data-test="password-input"]', 'wrongpassword')
    await page.click('button[type="submit"]')

    await expect(page.locator('.error-message')).toBeVisible()
  })
})

test.describe('规则管理流程', () => {
  test.use({ storageState: 'e2e/.auth/admin.json' })

  test('管理员可查看规则列表并操作规则', async ({ page }) => {
    await page.goto('/rules')

    await expect(page.getByRole('heading', { name: '规则管理' })).toBeVisible()
    await expect(page.locator('[data-test="rule-form"]')).toBeVisible()
    await expect(page.locator('[data-test="rule-table"]')).toBeVisible()
  })
})

test.describe('风险事件流程', () => {
  test.use({ storageState: 'e2e/.auth/admin.json' })

  test('管理员可查看风险事件列表', async ({ page }) => {
    await page.goto('/events')

    await expect(page.getByRole('heading', { name: '风险事件' })).toBeVisible()
    await expect(page.locator('[data-test="event-table"]')).toBeVisible()
  })
})

test.describe('评分与决策流程', () => {
  test.use({ storageState: 'e2e/.auth/admin.json' })

  test('管理员可访问评分与决策页面', async ({ page }) => {
    await page.goto('/scores')
    await expect(page).not.toHaveURL(/\/login/)

    await page.goto('/decisions')
    await expect(page).not.toHaveURL(/\/login/)
  })
})

test.describe('审计日志流程', () => {
  test.use({ storageState: 'e2e/.auth/admin.json' })

  test('管理员可查看审计日志列表', async ({ page }) => {
    await page.goto('/audit-logs')

    await expect(page.getByRole('heading', { name: '审计日志' })).toBeVisible()
    await expect(page.locator('[data-test="audit-table"]')).toBeVisible()
  })
})

test.describe('权限控制', () => {
  test('未登录用户访问受保护页面被重定向到登录页', async ({ page }) => {
    await page.goto('/rules')
    await expect(page).toHaveURL(/\/login/)
  })
})
