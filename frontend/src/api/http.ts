import axios from 'axios'

export interface ApiResponse<T> {
  success: boolean
  data: T | null
  message: string | null
  meta: ApiPageMeta | null
}

export interface ApiPageMeta {
  total: number
  page: number
  limit: number
}

export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1'
export const ACCESS_TOKEN_KEY = 'risk-control.access-token'
export const REFRESH_TOKEN_KEY = 'risk-control.refresh-token'

export const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10_000,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
})

http.defaults.headers.common.Accept = 'application/json'

http.interceptors.request.use((config) => {
  const token = localStorage.getItem(ACCESS_TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export function setAuthTokens(accessToken: string, refreshToken: string) {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken)
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
}

export function clearAuthTokens() {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}
