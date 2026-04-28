import { flushPromises, mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'

import ScoreDecisionView from './ScoreDecisionView.vue'

describe('ScoreDecisionView', () => {
  it('loads scores and decisions', async () => {
    const scoreApi = {
      list: vi.fn().mockResolvedValue({
        data: { data: [{ id: 1, subjectType: 'user', subjectId: 10, score: 88, riskLevel: 'high' }] },
      }),
      evaluate: vi.fn(),
    }
    const decisionApi = {
      list: vi.fn().mockResolvedValue({
        data: { data: [{ id: 2, eventId: 1, decisionType: 'reject', status: 'done' }] },
      }),
      create: vi.fn(),
    }

    const wrapper = mount(ScoreDecisionView, {
      global: { provide: { scoreApi, decisionApi } },
    })
    await flushPromises()

    expect(scoreApi.list).toHaveBeenCalledWith({ page: 1, limit: 20 })
    expect(decisionApi.list).toHaveBeenCalledWith({ page: 1, limit: 20 })
    expect(wrapper.get('[data-test="score-table"]').text()).toContain('88')
    expect(wrapper.get('[data-test="decision-table"]').text()).toContain('reject')
  })

  it('submits score evaluation request', async () => {
    const scoreApi = {
      list: vi.fn().mockResolvedValue({ data: { data: [] } }),
      evaluate: vi.fn().mockResolvedValue({}),
    }
    const decisionApi = {
      list: vi.fn().mockResolvedValue({ data: { data: [] } }),
      create: vi.fn(),
    }
    const wrapper = mount(ScoreDecisionView, {
      global: { provide: { scoreApi, decisionApi } },
    })
    await flushPromises()

    await wrapper.get('[data-test="subject-id-input"]').setValue('42')
    await wrapper.get('[data-test="score-form"]').trigger('submit')
    await flushPromises()

    expect(scoreApi.evaluate).toHaveBeenCalledWith({
      subjectType: 'user',
      subjectId: 42,
      factors: {},
    })
  })
})
