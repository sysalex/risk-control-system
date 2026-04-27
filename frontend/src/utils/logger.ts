type ConsoleMethod = 'debug' | 'info' | 'warn' | 'error'

const LOG_PREFIX = '[risk-control]'

function writeLog(method: ConsoleMethod, message: string, payload?: unknown) {
  const line = `${LOG_PREFIX} ${message}`

  if (payload === undefined) {
    console[method](line)
    return
  }

  console[method](line, payload)
}

export const logger = {
  debug(message: string, payload?: unknown) {
    writeLog('debug', message, payload)
  },
  info(message: string, payload?: unknown) {
    writeLog('info', message, payload)
  },
  warn(message: string, payload?: unknown) {
    writeLog('warn', message, payload)
  },
  error(message: string, payload?: unknown) {
    writeLog('error', message, payload)
  },
}
