import { flushPromises, mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'

import RuleManagementView from './RuleManagementView.vue'

describe('RuleManagementView', () => {
  it('loads and renders rules', async () => {
    const rules = [
      { id: 1, name: '大额交易', enabled: true, priority: 10 },
      { id: 2, name: '异常登录', enabled: false, priority: 20 },
    ]
    const api = {
      list: vi.fn().mockResolvedValue({ data: { data: rules } }),
      create: vi.fn(),
      update: vi.fn(),
      remove: vi.fn(),
      enable: vi.fn(),
      disable: vi.fn(),
    }

    const wrapper = mount(RuleManagementView, {
      global: {
        provide: {
          ruleApi: api,
        },
      },
    })
    await flushPromises()

    expect(api.list).toHaveBeenCalledWith({ page: 1, limit: 20 })
    expect(wrapper.get('[data-test="rule-table"]').text()).toContain('大额交易')
    expect(wrapper.get('[data-test="rule-table"]').text()).toContain('停用')
  })

  it('toggles rule enabled state and reloads list', async () => {
    const rules = [{ id: 1, name: '大额交易', enabled: true, priority: 10 }]
    const api = {
      list: vi.fn().mockResolvedValue({ data: { data: rules } }),
      create: vi.fn(),
      update: vi.fn(),
      remove: vi.fn(),
      enable: vi.fn(),
      disable: vi.fn().mockResolvedValue({}),
    }
    const wrapper = mount(RuleManagementView, {
      global: {
        provide: {
          ruleApi: api,
        },
      },
    })
    await flushPromises()

    await wrapper.get('[data-test="toggle-rule-1"]').trigger('click')
    await flushPromises()

    expect(api.disable).toHaveBeenCalledWith(1)
    expect(api.list).toHaveBeenCalledTimes(2)
  })
})
