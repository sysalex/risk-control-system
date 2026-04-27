import { API_BASE_URL, http, type ApiResponse } from './http'

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
})
