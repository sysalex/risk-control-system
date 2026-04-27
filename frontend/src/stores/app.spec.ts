import { createPinia, setActivePinia } from 'pinia'

import { useAppStore } from './app'

describe('useAppStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('exposes the default application shell state', () => {
    const store = useAppStore()

    expect(store.appName).toBe('风控系统')
    expect(store.sidebarCollapsed).toBe(false)
  })

  it('updates the sidebar collapsed state through an action', () => {
    const store = useAppStore()

    store.setSidebarCollapsed(true)

    expect(store.sidebarCollapsed).toBe(true)
  })
})
