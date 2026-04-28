import { flushPromises, mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'

import EventAnalysisView from './EventAnalysisView.vue'

describe('EventAnalysisView', () => {
  it('loads and renders risk events', async () => {
    const events = [
      { id: 1, subjectType: 'user', subjectId: 10, riskLevel: 'high', status: 'open' },
      { id: 2, subjectType: 'account', subjectId: 20, riskLevel: 'medium', status: 'resolved' },
    ]
    const api = {
      list: vi.fn().mockResolvedValue({ data: { data: events } }),
      resolve: vi.fn(),
    }

    const wrapper = mount(EventAnalysisView, {
      global: { provide: { eventApi: api } },
    })
    await flushPromises()

    expect(api.list).toHaveBeenCalledWith({ page: 1, limit: 20 })
    expect(wrapper.get('[data-test="event-table"]').text()).toContain('high')
    expect(wrapper.get('[data-test="event-table"]').text()).toContain('account')
  })

  it('resolves event and reloads list', async () => {
    const events = [{ id: 1, subjectType: 'user', subjectId: 10, riskLevel: 'high', status: 'open' }]
    const api = {
      list: vi.fn().mockResolvedValue({ data: { data: events } }),
      resolve: vi.fn().mockResolvedValue({}),
    }
    const wrapper = mount(EventAnalysisView, {
      global: { provide: { eventApi: api } },
    })
    await flushPromises()

    await wrapper.get('[data-test="resolve-event-1"]').trigger('click')
    await flushPromises()

    expect(api.resolve).toHaveBeenCalledWith(1)
    expect(api.list).toHaveBeenCalledTimes(2)
  })
})
