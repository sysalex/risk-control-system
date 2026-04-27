import { mount } from '@vue/test-utils'

import App from './App.vue'

describe('App', () => {
  it('renders the application shell with a router outlet', () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          RouterView: {
            template: '<div data-test="router-view" />',
          },
        },
      },
    })

    expect(wrapper.get('[data-test="app-shell"]').exists()).toBe(true)
    expect(wrapper.get('[data-test="router-view"]').exists()).toBe(true)
  })
})
