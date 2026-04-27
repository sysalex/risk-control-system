import { logger } from './logger'

describe('logger', () => {
  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('writes informational messages through the logger facade', () => {
    const info = vi.spyOn(console, 'info').mockImplementation(() => undefined)

    logger.info('quality gate started', { task: '0.4' })

    expect(info).toHaveBeenCalledWith('[risk-control] quality gate started', {
      task: '0.4',
    })
  })

  it('writes errors without exposing a console.log dependency', () => {
    const error = new Error('boom')
    const errorLog = vi.spyOn(console, 'error').mockImplementation(() => undefined)

    logger.error('unexpected frontend error', error)

    expect(errorLog).toHaveBeenCalledWith(
      '[risk-control] unexpected frontend error',
      error,
    )
  })

  it('supports debug and warning messages without payloads', () => {
    const debug = vi.spyOn(console, 'debug').mockImplementation(() => undefined)
    const warn = vi.spyOn(console, 'warn').mockImplementation(() => undefined)

    logger.debug('debug detail')
    logger.warn('careful')

    expect(debug).toHaveBeenCalledWith('[risk-control] debug detail')
    expect(warn).toHaveBeenCalledWith('[risk-control] careful')
  })
})
