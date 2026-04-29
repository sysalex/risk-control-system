import { expect, test, type Page, type Response, type APIRequestContext } from '@playwright/test'

const API_BASE = 'http://localhost:8080/api/v1'
const ADMIN_STATE = 'e2e/.auth/admin.json'

function waitForApi(page: Page, method: string, path: string) {
  return page.waitForResponse((response) => {
    const request = response.request()
    return response.url().startsWith(API_BASE)
      && request.method() === method
      && response.url().includes(path)
  })
}

async function expectSuccessfulApi(responsePromise: Promise<Response>) {
  const response = await responsePromise
  expect(response.status(), `${response.request().method()} ${response.url()}`).toBeLessThan(400)
  const body = await response.json()
  expect(body.success, `${response.request().method()} ${response.url()}`).toBe(true)
  return body
}

async function adminLogin(request: APIRequestContext) {
  const loginRes = await request.post(`${API_BASE}/auth/login`, {
    data: { username: 'admin', password: 'admin12345' },
  })
  const loginBody = await loginRes.json()
  expect(loginBody.success).toBe(true)
  const token = loginBody.data.accessToken as string

  const meRes = await request.get(`${API_BASE}/users/me`, {
    headers: { Authorization: `Bearer ${token}` },
  })
  const meBody = await meRes.json()
  expect(meBody.success).toBe(true)

  return {
    token,
    refreshToken: loginBody.data.refreshToken as string,
    userId: meBody.data.id as number,
  }
}

test.describe.configure({ mode: 'serial' })

/* ───────── 页面交互联调（保留并扩充） ───────── */

test.describe('前后端接口联调', () => {
  test('登录页面通过真实 auth/login 接口进入工作台', async ({ page }) => {
    await page.goto('/login')
    await page.fill('[data-test="username-input"]', 'admin')
    await page.fill('[data-test="password-input"]', 'admin12345')

    const loginResponse = waitForApi(page, 'POST', '/auth/login')
    await page.click('button[type="submit"]')

    await expectSuccessfulApi(loginResponse)
    await expect(page).toHaveURL('http://localhost:5173/')
  })
})

test.describe('已登录前后端接口联调', () => {
  test.use({ storageState: ADMIN_STATE })

  test('用户管理页面通过真实 users 接口完成列表、创建和删除', async ({ page }) => {
    const suffix = Date.now()
    const username = `pw_user_${suffix}`
    const email = `pw_user_${suffix}@example.com`

    const usersResponse = waitForApi(page, 'GET', '/users')
    await page.goto('/users')
    await expectSuccessfulApi(usersResponse)
    await expect(page.getByRole('cell', { name: 'admin', exact: true })).toBeVisible()

    await page.fill('[data-test="username-input"]', username)
    await page.fill('[data-test="email-input"]', email)
    await page.fill('[data-test="password-input"]', 'password123')
    await page.locator('select').selectOption('RISK_ANALYST')

    const createResponse = waitForApi(page, 'POST', '/users')
    await page.click('button[type="submit"]')
    const createBody = await expectSuccessfulApi(createResponse)
    const userId = createBody.data.id

    await expect(page.getByRole('cell', { name: username, exact: true })).toBeVisible()

    const deleteResponse = waitForApi(page, 'DELETE', `/users/${userId}`)
    await page.locator(`[data-test="delete-user-${userId}"]`).click()
    await expectSuccessfulApi(deleteResponse)
    await expect(page.getByRole('cell', { name: username, exact: true })).toHaveCount(0)
  })

  test('规则管理页面通过真实 rules 接口完成列表、创建、启停和删除', async ({ page }) => {
    const ruleName = `pw_rule_${Date.now()}`

    const rulesResponse = waitForApi(page, 'GET', '/rules')
    await page.goto('/rules')
    await expectSuccessfulApi(rulesResponse)

    await page.fill('[data-test="rule-name-input"]', ruleName)
    await page.fill('[data-test="rule-priority-input"]', '11')

    const createResponse = waitForApi(page, 'POST', '/rules')
    await page.click('button[type="submit"]')
    const createBody = await expectSuccessfulApi(createResponse)
    const ruleId = createBody.data.id

    await expect(page.getByRole('cell', { name: ruleName })).toBeVisible()

    const disableResponse = waitForApi(page, 'POST', `/rules/${ruleId}/disable`)
    await page.locator(`[data-test="toggle-rule-${ruleId}"]`).click()
    await expectSuccessfulApi(disableResponse)

    const enableResponse = waitForApi(page, 'POST', `/rules/${ruleId}/enable`)
    await page.locator(`[data-test="toggle-rule-${ruleId}"]`).click()
    await expectSuccessfulApi(enableResponse)

    const deleteResponse = waitForApi(page, 'DELETE', `/rules/${ruleId}`)
    await page.locator(`[data-test="delete-rule-${ruleId}"]`).click()
    await expectSuccessfulApi(deleteResponse)
    await expect(page.getByRole('cell', { name: ruleName })).toHaveCount(0)
  })

  test('事件、评分决策和审计页面通过真实列表接口加载', async ({ page }) => {
    const eventsResponse = waitForApi(page, 'GET', '/events')
    await page.goto('/events')
    await expectSuccessfulApi(eventsResponse)
    await expect(page.locator('[data-test="event-table"]')).toBeVisible()

    const scoresResponse = waitForApi(page, 'GET', '/scores')
    const decisionsResponse = waitForApi(page, 'GET', '/decisions')
    await page.goto('/decisions')
    await expectSuccessfulApi(scoresResponse)
    await expectSuccessfulApi(decisionsResponse)
    await expect(page.locator('[data-test="score-table"]')).toBeVisible()
    await expect(page.locator('[data-test="decision-table"]')).toBeVisible()

    const auditResponse = waitForApi(page, 'GET', '/audit-logs')
    await page.goto('/audit-logs')
    await expectSuccessfulApi(auditResponse)
    await expect(page.locator('[data-test="audit-table"]')).toBeVisible()
  })
})

/* ───────── API 直接调用 — 认证接口 ───────── */

test.describe('API 直接调用 — 认证接口', () => {
  test('POST /auth/register 注册新用户', async ({ request }) => {
    const suffix = Date.now()
    const response = await request.post(`${API_BASE}/auth/register`, {
      data: {
        username: `api_user_${suffix}`,
        email: `api_user_${suffix}@example.com`,
        password: 'Password123!',
      },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.username).toBe(`api_user_${suffix}`)
  })

  test('POST /auth/login 登录并获取双 Token', async ({ request }) => {
    const response = await request.post(`${API_BASE}/auth/login`, {
      data: { username: 'admin', password: 'admin12345' },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.accessToken).toBeTruthy()
    expect(body.data.refreshToken).toBeTruthy()
  })

  test('GET /users/me 获取当前登录用户信息', async ({ request }) => {
    const { token } = await adminLogin(request)
    const response = await request.get(`${API_BASE}/users/me`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.username).toBe('admin')
  })

  test('POST /auth/refresh 刷新 access token', async ({ request }) => {
    const { refreshToken } = await adminLogin(request)
    const response = await request.post(`${API_BASE}/auth/refresh`, {
      data: { refreshToken },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.accessToken).toBeTruthy()
  })

  test('POST /auth/logout 登出当前用户', async ({ request }) => {
    const { token } = await adminLogin(request)
    const response = await request.post(`${API_BASE}/auth/logout`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
  })
})

/* ───────── API 直接调用 — 用户管理完整接口 ───────── */

test.describe('API 直接调用 — 用户管理完整接口', () => {
  let token: string
  let userId: number

  test.beforeAll(async ({ request }) => {
    const login = await adminLogin(request)
    token = login.token
  })

  test('POST /users 创建用户', async ({ request }) => {
    const suffix = Date.now()
    const response = await request.post(`${API_BASE}/users`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        username: `pw_api_user_${suffix}`,
        email: `pw_api_user_${suffix}@example.com`,
        password: 'password123',
        role: 'RISK_ANALYST',
      },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    userId = body.data.id
  })

  test('GET /users 查询用户列表', async ({ request }) => {
    const response = await request.get(`${API_BASE}/users?page=1&limit=20`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(Array.isArray(body.data.records)).toBe(true)
  })

  test('PUT /users/{id} 更新用户', async ({ request }) => {
    const response = await request.put(`${API_BASE}/users/${userId}`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        email: `updated_${userId}@example.com`,
        role: 'OPERATOR',
        active: true,
      },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.email).toBe(`updated_${userId}@example.com`)
  })

  test('DELETE /users/{id} 删除用户', async ({ request }) => {
    const response = await request.delete(`${API_BASE}/users/${userId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
  })
})

/* ───────── API 直接调用 — 规则管理完整接口 ───────── */

test.describe('API 直接调用 — 规则管理完整接口', () => {
  let token: string
  let ruleId: number

  test.beforeAll(async ({ request }) => {
    const login = await adminLogin(request)
    token = login.token
  })

  test('POST /rules 创建规则', async ({ request }) => {
    const suffix = Date.now()
    const response = await request.post(`${API_BASE}/rules`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        name: `api_rule_${suffix}`,
        description: 'api test rule',
        conditions: '{}',
        actions: '{}',
        priority: 5,
      },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    ruleId = body.data.id
  })

  test('GET /rules 查询规则列表', async ({ request }) => {
    const response = await request.get(`${API_BASE}/rules?page=1&limit=20`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
  })

  test('GET /rules/{id} 查询规则详情', async ({ request }) => {
    const response = await request.get(`${API_BASE}/rules/${ruleId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.id).toBe(ruleId)
  })

  test('PUT /rules/{id} 更新规则', async ({ request }) => {
    const response = await request.put(`${API_BASE}/rules/${ruleId}`, {
      headers: { Authorization: `Bearer ${token}` },
      data: { name: `updated_rule_${ruleId}`, priority: 10 },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.name).toContain('updated_rule_')
  })

  test('POST /rules/{id}/disable 停用规则', async ({ request }) => {
    const response = await request.post(`${API_BASE}/rules/${ruleId}/disable`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.enabled).toBe(false)
  })

  test('POST /rules/{id}/enable 启用规则', async ({ request }) => {
    const response = await request.post(`${API_BASE}/rules/${ruleId}/enable`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.enabled).toBe(true)
  })

  test('DELETE /rules/{id} 删除无引用的规则成功', async ({ request }) => {
    const response = await request.delete(`${API_BASE}/rules/${ruleId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
  })

  test('DELETE /rules/{id} 删除被事件引用的规则返回业务错误', async ({ request }) => {
    const suffix = Date.now()

    const ruleRes = await request.post(`${API_BASE}/rules`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        name: `constraint_rule_${suffix}`,
        description: 'for constraint test',
        conditions: '{}',
        actions: '{}',
        priority: 1,
      },
    })
    const ruleBody = await ruleRes.json()
    expect(ruleBody.success).toBe(true)
    const constraintRuleId = ruleBody.data.id

    const eventRes = await request.post(`${API_BASE}/events`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        ruleId: constraintRuleId,
        subjectType: 'user',
        subjectId: '99',
        riskLevel: 'HIGH',
        description: 'linked event',
      },
    })
    expect(eventRes.ok()).toBe(true)

    const deleteRes = await request.delete(`${API_BASE}/rules/${constraintRuleId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    expect(deleteRes.status()).toBeLessThan(500)
    const deleteBody = await deleteRes.json()
    expect(deleteBody.success).toBe(false)
  })
})

/* ───────── API 直接调用 — 事件管理完整接口 ───────── */

test.describe('API 直接调用 — 事件管理完整接口', () => {
  let token: string
  let ruleId: number
  let eventId: number

  test.beforeAll(async ({ request }) => {
    const login = await adminLogin(request)
    token = login.token

    const ruleRes = await request.post(`${API_BASE}/rules`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        name: `event_test_rule_${Date.now()}`,
        description: 'for event test',
        conditions: '{}',
        actions: '{}',
        priority: 1,
      },
    })
    ruleId = (await ruleRes.json()).data.id
  })

  test('POST /events 创建事件', async ({ request }) => {
    const response = await request.post(`${API_BASE}/events`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        ruleId,
        subjectType: 'user',
        subjectId: '42',
        riskLevel: 'HIGH',
        description: 'api test event',
      },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    eventId = body.data.id
  })

  test('GET /events 查询事件列表', async ({ request }) => {
    const response = await request.get(`${API_BASE}/events?page=1&limit=20`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
  })

  test('GET /events/{id} 查询事件详情', async ({ request }) => {
    const response = await request.get(`${API_BASE}/events/${eventId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.id).toBe(eventId)
  })

  test('PUT /events/{id} 更新事件', async ({ request }) => {
    const response = await request.put(`${API_BASE}/events/${eventId}`, {
      headers: { Authorization: `Bearer ${token}` },
      data: { description: 'updated description' },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
  })

  test('POST /events/{id}/resolve 解决事件', async ({ request }) => {
    const response = await request.post(`${API_BASE}/events/${eventId}/resolve`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.status).toBe('RESOLVED')
  })
})

/* ───────── API 直接调用 — 评分完整接口 ───────── */

test.describe('API 直接调用 — 评分完整接口', () => {
  let token: string
  let ruleId: number
  let eventId: number
  let scoreId: number

  test.beforeAll(async ({ request }) => {
    const login = await adminLogin(request)
    token = login.token

    const ruleRes = await request.post(`${API_BASE}/rules`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        name: `score_test_rule_${Date.now()}`,
        description: 'for score test',
        conditions: '{}',
        actions: '{}',
        priority: 1,
      },
    })
    ruleId = (await ruleRes.json()).data.id

    const eventRes = await request.post(`${API_BASE}/events`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        ruleId,
        subjectType: 'user',
        subjectId: '42',
        riskLevel: 'HIGH',
        description: 'for score test',
      },
    })
    eventId = (await eventRes.json()).data.id
  })

  test('POST /scores/evaluate 创建评分', async ({ request }) => {
    const response = await request.post(`${API_BASE}/scores/evaluate`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        eventId,
        subjectType: 'user',
        subjectId: '42',
        score: 85.5,
        dimensions: '{}',
      },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    scoreId = body.data.id
  })

  test('GET /scores 查询评分列表', async ({ request }) => {
    const response = await request.get(`${API_BASE}/scores?page=1&limit=20`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
  })

  test('GET /scores/{id} 查询评分详情', async ({ request }) => {
    const response = await request.get(`${API_BASE}/scores/${scoreId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.id).toBe(scoreId)
  })

  test('GET /scores/subject/{type}/{id} 按主体查询最新评分', async ({ request }) => {
    const response = await request.get(`${API_BASE}/scores/subject/user/42`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
  })
})

/* ───────── API 直接调用 — 决策完整接口 ───────── */

test.describe('API 直接调用 — 决策完整接口', () => {
  let token: string
  let userId: number
  let ruleId: number
  let eventId: number
  let decisionId: number

  test.beforeAll(async ({ request }) => {
    const login = await adminLogin(request)
    token = login.token
    userId = login.userId

    const ruleRes = await request.post(`${API_BASE}/rules`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        name: `decision_test_rule_${Date.now()}`,
        description: 'for decision test',
        conditions: '{}',
        actions: '{}',
        priority: 1,
      },
    })
    ruleId = (await ruleRes.json()).data.id

    const eventRes = await request.post(`${API_BASE}/events`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        ruleId,
        subjectType: 'user',
        subjectId: '42',
        riskLevel: 'HIGH',
        description: 'for decision test',
      },
    })
    eventId = (await eventRes.json()).data.id
  })

  test('POST /decisions 创建决策', async ({ request }) => {
    const response = await request.post(`${API_BASE}/decisions`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        eventId,
        decisionType: 'REJECT',
        reason: 'risk too high',
        decidedBy: userId,
      },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    decisionId = body.data.id
  })

  test('GET /decisions 查询决策列表', async ({ request }) => {
    const response = await request.get(`${API_BASE}/decisions?page=1&limit=20`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
  })

  test('GET /decisions/{id} 查询决策详情', async ({ request }) => {
    const response = await request.get(`${API_BASE}/decisions/${decisionId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.id).toBe(decisionId)
  })

  test('PUT /decisions/{id} 更新决策', async ({ request }) => {
    const response = await request.put(`${API_BASE}/decisions/${decisionId}`, {
      headers: { Authorization: `Bearer ${token}` },
      data: { reason: 'updated reason' },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
  })
})

/* ───────── API 直接调用 — 审计日志完整接口 ───────── */

test.describe('API 直接调用 — 审计日志完整接口', () => {
  let token: string
  let auditLogId: number | undefined

  test.beforeAll(async ({ request }) => {
    const login = await adminLogin(request)
    token = login.token
  })

  test('GET /audit-logs 查询审计日志列表', async ({ request }) => {
    const response = await request.get(`${API_BASE}/audit-logs?page=1&limit=20`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(Array.isArray(body.data.records)).toBe(true)
    if (body.data.records.length > 0) {
      auditLogId = body.data.records[0].id
    }
  })

  test('GET /audit-logs/{id} 查询审计日志详情', async ({ request }) => {
    if (!auditLogId) test.skip()
    const response = await request.get(`${API_BASE}/audit-logs/${auditLogId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.id).toBe(auditLogId)
  })
})

/* ───────── API 直接调用 — 健康检查 ───────── */

test.describe('API 直接调用 — 健康检查', () => {
  test('GET /health 返回服务状态', async ({ request }) => {
    const response = await request.get(`${API_BASE}/health`)
    const body = await response.json()
    expect(body.success).toBe(true)
    expect(body.data.status).toBe('ok')
  })
})
