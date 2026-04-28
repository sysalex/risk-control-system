import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'

import DashboardView from './DashboardView.vue'

describe('DashboardView', () => {
  it('shows key risk operation metrics', () => {
    const wrapper = mount(DashboardView)

    expect(wrapper.get('[data-test="dashboard-title"]').text()).toBe('Dashboard')
    expect(wrapper.get('[data-test="metric-events"]').text()).toContain('待处理事件')
    expect(wrapper.get('[data-test="metric-decisions"]').text()).toContain('今日决策')
  })
})
