import router from './index'

describe('router', () => {
  it('registers the home route as the root page', () => {
    const resolved = router.resolve('/')

    expect(resolved.name).toBe('home')
    expect(resolved.matched).toHaveLength(1)
  })

  it('loads the home view component lazily', async () => {
    const route = router.getRoutes().find((item) => item.name === 'home')
    const component = route?.components?.default

    expect(typeof component).toBe('function')

    if (typeof component === 'function') {
      const loaded = await component()

      expect(loaded).toHaveProperty('default')
    }
  })
})
