import {
  ACCESS_TOKEN_KEY,
  API_BASE_URL,
  clearAuthTokens,
  http,
  setAuthTokens,
  type ApiResponse,
} from './http'

describe('http client', () => {
  it('uses the configured API base URL', () => {
    expect(API_BASE_URL).toBe('http://localhost:8080/api/v1')
    expect(http.defaults.baseURL).toBe(API_BASE_URL)
    expect(http.defaults.headers.common.Accept).toBe('application/json')
  })

  it('models the unified response envelope', () => {
    const response: ApiResponse<{ status: string }> = {
      success: true,
      data: { status: 'ok' },
      message: null,
      meta: null,
    }

    expect(response.data?.status).toBe('ok')
  })

  it('stores and clears auth tokens', () => {
    setAuthTokens('access-token', 'refresh-token')

    expect(localStorage.getItem(ACCESS_TOKEN_KEY)).toBe('access-token')

    clearAuthTokens()

    expect(localStorage.getItem(ACCESS_TOKEN_KEY)).toBeNull()
  })

  it('adds bearer token to outgoing requests', async () => {
    setAuthTokens('access-token', 'refresh-token')
    const interceptor = http.interceptors.request.handlers[0]?.fulfilled

    const config = await interceptor?.({ headers: {} })

    expect(config?.headers.Authorization).toBe('Bearer access-token')
  })
})
