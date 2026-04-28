import { describe, expect, it, vi } from 'vitest'

import { http } from './http'
import {
  auditApi,
  authApi,
  decisionApi,
  eventApi,
  ruleApi,
  scoreApi,
  userApi,
} from './modules'

describe('api modules', () => {
  it('maps auth endpoints to backend routes', async () => {
    const post = vi.spyOn(http, 'post').mockResolvedValue({ data: {} })

    await authApi.login({ username: 'alice', password: 'password123' })

    expect(post).toHaveBeenCalledWith('/auth/login', {
      username: 'alice',
      password: 'password123',
    })
  })

  it('maps business modules to backend routes', async () => {
    const get = vi.spyOn(http, 'get').mockResolvedValue({ data: {} })
    const post = vi.spyOn(http, 'post').mockResolvedValue({ data: {} })
    const put = vi.spyOn(http, 'put').mockResolvedValue({ data: {} })
    const remove = vi.spyOn(http, 'delete').mockResolvedValue({ data: {} })

    await authApi.refresh('refresh-token')
    await authApi.logout()
    await userApi.me()
    await userApi.list({ page: 1, limit: 20 })
    await userApi.create({ username: 'alice' })
    await userApi.update(2, { email: 'alice@example.com' })
    await userApi.remove(2)
    await ruleApi.list({ page: 1, limit: 20 })
    await ruleApi.create({ name: '规则' })
    await ruleApi.update(3, { priority: 2 })
    await ruleApi.remove(3)
    await ruleApi.enable(3)
    await ruleApi.disable(3)
    await eventApi.list({ page: 1, limit: 20, status: 'open' })
    await eventApi.create({ subjectId: 4 })
    await eventApi.update(4, { status: 'processing' })
    await eventApi.resolve(4)
    await scoreApi.list({ page: 1, limit: 20 })
    await scoreApi.evaluate({ subjectId: 5 })
    await scoreApi.latestBySubject('user', 5)
    await decisionApi.create({ eventId: 4 })
    await decisionApi.update(6, { status: 'done' })
    await decisionApi.list({ page: 1, limit: 20 })
    await auditApi.list({ page: 1, limit: 20 })

    expect(post).toHaveBeenCalledWith('/auth/refresh', { refreshToken: 'refresh-token' })
    expect(post).toHaveBeenCalledWith('/auth/logout')
    expect(get).toHaveBeenCalledWith('/users/me')
    expect(get).toHaveBeenCalledWith('/users', { params: { page: 1, limit: 20 } })
    expect(post).toHaveBeenCalledWith('/users', { username: 'alice' })
    expect(put).toHaveBeenCalledWith('/users/2', { email: 'alice@example.com' })
    expect(remove).toHaveBeenCalledWith('/users/2')
    expect(get).toHaveBeenCalledWith('/rules', { params: { page: 1, limit: 20 } })
    expect(post).toHaveBeenCalledWith('/rules', { name: '规则' })
    expect(put).toHaveBeenCalledWith('/rules/3', { priority: 2 })
    expect(remove).toHaveBeenCalledWith('/rules/3')
    expect(post).toHaveBeenCalledWith('/rules/3/enable')
    expect(post).toHaveBeenCalledWith('/rules/3/disable')
    expect(get).toHaveBeenCalledWith('/events', {
      params: { page: 1, limit: 20, status: 'open' },
    })
    expect(post).toHaveBeenCalledWith('/events', { subjectId: 4 })
    expect(put).toHaveBeenCalledWith('/events/4', { status: 'processing' })
    expect(post).toHaveBeenCalledWith('/events/4/resolve')
    expect(get).toHaveBeenCalledWith('/scores', { params: { page: 1, limit: 20 } })
    expect(post).toHaveBeenCalledWith('/scores/evaluate', { subjectId: 5 })
    expect(get).toHaveBeenCalledWith('/scores/subject/user/5')
    expect(post).toHaveBeenCalledWith('/decisions', { eventId: 4 })
    expect(put).toHaveBeenCalledWith('/decisions/6', { status: 'done' })
    expect(get).toHaveBeenCalledWith('/decisions', { params: { page: 1, limit: 20 } })
    expect(get).toHaveBeenCalledWith('/audit-logs', { params: { page: 1, limit: 20 } })
  })
})
