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

export const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10_000,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
})

http.defaults.headers.common.Accept = 'application/json'
