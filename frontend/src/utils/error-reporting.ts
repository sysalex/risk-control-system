import type { App } from 'vue'

import { logger } from './logger'

export type FrontendErrorSource = 'vue' | 'window' | 'unhandledrejection'

export interface FrontendErrorReport {
  source: FrontendErrorSource
  error: Error
  info?: string
}

export type ErrorReporter = (report: FrontendErrorReport) => void

function normalizeError(error: unknown): Error {
  if (error instanceof Error) {
    return error
  }

  return new Error(String(error))
}

const defaultReporter: ErrorReporter = (report) => {
  logger.error(`frontend error from ${report.source}`, report.error)
}

export function setupErrorReporting(
  app: App,
  reporter: ErrorReporter = defaultReporter,
) {
  app.config.errorHandler = (error, _instance, info) => {
    reporter({
      source: 'vue',
      error: normalizeError(error),
      info,
    })
  }

  const handleWindowError = (event: ErrorEvent) => {
    reporter({
      source: 'window',
      error: normalizeError(event.error ?? event.message),
    })
  }

  const handleUnhandledRejection = (event: PromiseRejectionEvent) => {
    reporter({
      source: 'unhandledrejection',
      error: normalizeError(event.reason),
    })
  }

  window.addEventListener('error', handleWindowError)
  window.addEventListener('unhandledrejection', handleUnhandledRejection)

  return () => {
    window.removeEventListener('error', handleWindowError)
    window.removeEventListener('unhandledrejection', handleUnhandledRejection)
  }
}
