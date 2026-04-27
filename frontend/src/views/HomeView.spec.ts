import { mount } from '@vue/test-utils'

import HomeView from './HomeView.vue'

describe('HomeView', () => {
  it('renders the application name as the page heading', () => {
    const wrapper = mount(HomeView)

    expect(wrapper.get('h1').text()).toBe('风控系统')
  })
})
