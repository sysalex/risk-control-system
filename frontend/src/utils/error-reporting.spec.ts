import type { App } from 'vue'

import { setupErrorReporting } from './error-reporting'

describe('setupErrorReporting', () => {
  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('registers a Vue error handler that forwards errors to the reporter', () => {
    const reported = vi.fn()
    const app = { config: {} } as App
    const error = new Error('render failed')

    setupErrorReporting(app, reported)
    app.config.errorHandler?.(error, null, 'render')

    expect(reported).toHaveBeenCalledWith({
      source: 'vue',
      error,
      info: 'render',
    })
  })

  it('registers browser error listeners and removes them on cleanup', () => {
    const reported = vi.fn()
    const app = { config: {} } as App
    const cleanup = setupErrorReporting(app, reported)
    const error = new Error('window failed')
    const rejection = new Error('promise failed')
    const rejectedPromise = Promise.reject(rejection)
    rejectedPromise.catch(() => undefined)

    window.dispatchEvent(new ErrorEvent('error', { error }))
    window.dispatchEvent(
      new PromiseRejectionEvent('unhandledrejection', {
        promise: rejectedPromise,
        reason: rejection,
      }),
    )

    expect(reported).toHaveBeenCalledWith({ source: 'window', error })
    expect(reported).toHaveBeenCalledWith({
      source: 'unhandledrejection',
      error: rejection,
    })

    cleanup()
    reported.mockClear()

    window.dispatchEvent(new ErrorEvent('error', { error }))

    expect(reported).not.toHaveBeenCalled()
  })

  it('normalizes non Error values and uses the default logger reporter', () => {
    const errorLog = vi.spyOn(console, 'error').mockImplementation(() => undefined)
    const app = { config: {} } as App

    setupErrorReporting(app)
    app.config.errorHandler?.('plain failure', null, 'setup')

    expect(errorLog).toHaveBeenCalledWith(
      '[risk-control] frontend error from vue',
      expect.objectContaining({ message: 'plain failure' }),
    )
  })
})
