import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'

import MainLayout from './MainLayout.vue'

describe('MainLayout', () => {
  it('renders primary navigation and the active route outlet', () => {
    const wrapper = mount(MainLayout, {
      global: {
        stubs: {
          RouterLink: {
            props: ['to'],
            template: '<a :href="to"><slot /></a>',
          },
          RouterView: {
            template: '<main data-test="main-content">dashboard</main>',
          },
        },
      },
    })

    expect(wrapper.get('[data-test="main-layout"]').exists()).toBe(true)
    expect(wrapper.get('[data-test="nav-dashboard"]').text()).toContain('Dashboard')
    expect(wrapper.get('[data-test="nav-audit"]').text()).toContain('审计日志')
    expect(wrapper.get('[data-test="main-content"]').text()).toBe('dashboard')
  })
})
