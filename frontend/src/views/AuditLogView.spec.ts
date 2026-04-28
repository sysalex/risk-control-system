import { flushPromises, mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'

import AuditLogView from './AuditLogView.vue'

describe('AuditLogView', () => {
  it('loads and renders audit logs', async () => {
    const logs = [
      {
        id: 1,
        userId: 2,
        action: 'create',
        resourceType: 'rule',
        resourceId: 3,
        createdAt: '2026-04-28T10:00:00',
        ipAddress: '127.0.0.1',
      },
    ]
    const api = {
      list: vi.fn().mockResolvedValue({ data: { data: logs } }),
    }

    const wrapper = mount(AuditLogView, {
      global: { provide: { auditApi: api } },
    })
    await flushPromises()

    expect(api.list).toHaveBeenCalledWith({ page: 1, limit: 20 })
    expect(wrapper.get('[data-test="audit-table"]').text()).toContain('create')
    expect(wrapper.get('[data-test="audit-table"]').text()).toContain('127.0.0.1')
  })
})
