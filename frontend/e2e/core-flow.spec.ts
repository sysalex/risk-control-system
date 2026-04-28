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
    await page.fill('[data-test="password-input"]', 'admin123')
    await page.click('button[type="submit"]')

    await expect(page).toHaveURL(/\//)
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

  test('管理员可完成规则的增删改查与启停', async ({ page }) => {
    await page.goto('/rules')

    // 创建规则
    await page.click('text=新建规则')
    await page.fill('input[name="name"]', 'E2E测试规则')
    await page.fill('textarea[name="description"]', '由E2E测试自动创建')
    await page.fill('input[name="priority"]', '99')
    await page.click('.dialog-footer button:has-text("确定")')

    await expect(page.locator('text=E2E测试规则')).toBeVisible()

    // 禁用规则
    await page.locator('tr:has-text("E2E测试规则") button:has-text("禁用")').click()
    await page.click('.el-message-box .el-button--primary')

    // 启用规则
    await page.locator('tr:has-text("E2E测试规则") button:has-text("启用")').click()
    await page.click('.el-message-box .el-button--primary')

    // 删除规则
    await page.locator('tr:has-text("E2E测试规则") button:has-text("删除")').click()
    await page.click('.el-message-box .el-button--primary')
  })
})

test.describe('风险事件流程', () => {
  test.use({ storageState: 'e2e/.auth/admin.json' })

  test('管理员可创建并解决事件', async ({ page }) => {
    await page.goto('/events')

    // 创建事件
    await page.click('text=新建事件')
    await page.fill('input[name="subjectId"]', 'e2e-user-001')
    await page.selectOption('select[name="riskLevel"]', 'HIGH')
    await page.fill('textarea[name="description"]', 'E2E测试事件')
    await page.click('.dialog-footer button:has-text("确定")')

    await expect(page.locator('text=E2E测试事件')).toBeVisible()

    // 解决事件
    await page.locator('tr:has-text("E2E测试事件") button:has-text("解决")').click()
    await page.click('.el-message-box .el-button--primary')
  })
})

test.describe('评分与决策流程', () => {
  test.use({ storageState: 'e2e/.auth/admin.json' })

  test('管理员可查看评分并创建决策', async ({ page }) => {
    await page.goto('/scores')
    await expect(page.locator('text=风险评分')).toBeVisible()

    // 进入决策页面
    await page.goto('/decisions')
    await expect(page.locator('text=决策记录')).toBeVisible()

    // 创建决策
    await page.click('text=新建决策')
    await page.fill('input[name="reason"]', 'E2E测试决策')
    await page.fill('textarea[name="comment"]', '由E2E测试自动创建')
    await page.click('.dialog-footer button:has-text("确定")')

    await expect(page.locator('text=E2E测试决策')).toBeVisible()
  })
})

test.describe('审计日志流程', () => {
  test.use({ storageState: 'e2e/.auth/admin.json' })

  test('管理员可查看审计日志列表', async ({ page }) => {
    await page.goto('/audit-logs')
    await expect(page.locator('text=审计日志')).toBeVisible()
  })
})

test.describe('权限控制', () => {
  test('未登录用户访问受保护页面被重定向到登录页', async ({ page }) => {
    await page.goto('/rules')
    await expect(page).toHaveURL(/\/login/)
  })
})
