import router from './index'

describe('router', () => {
  it('registers the login route', () => {
    const resolved = router.resolve('/login')

    expect(resolved.name).toBe('login')
    expect(resolved.matched).toHaveLength(1)
  })

  it('registers dashboard as the root page under main layout', () => {
    const resolved = router.resolve('/')

    expect(resolved.name).toBe('dashboard')
    expect(resolved.matched).toHaveLength(2)
  })

  it('loads the dashboard view component lazily', async () => {
    const route = router.getRoutes().find((item) => item.name === 'dashboard')
    const component = route?.components?.default

    expect(typeof component).toBe('function')

    if (typeof component === 'function') {
      const loaded = await component()

      expect(loaded).toHaveProperty('default')
    }
  })
})
