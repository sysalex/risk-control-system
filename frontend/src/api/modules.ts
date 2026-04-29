import { http, type ApiResponse } from './http'

export interface PageQuery {
  page?: number
  limit?: number
}

export type Payload = Record<string, unknown>

export interface LoginRequest {
  username: string
  password: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  expiresIn: number
}

export interface UserResponse {
  id: number
  username: string
  email: string
  role: string
  active?: boolean
  enabled: boolean
}

export interface RuleResponse {
  id: number
  name: string
  enabled: boolean
  priority: number
}

export interface EventResponse {
  id: number
  subjectType: string
  subjectId: number
  riskLevel: string
  status: string
}

export interface ScoreResponse {
  id: number
  subjectType: string
  subjectId: number
  score: number
  riskLevel: string
}

export interface DecisionResponse {
  id: number
  eventId: number
  decisionType: string
  status: string
}

export interface AuditLogResponse {
  id: number
  userId: number
  action: string
  resourceType: string
  resourceId: number | null
  createdAt: string
  ipAddress: string
}

export interface PagePayload<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export type ListPayload<T> = T[] | PagePayload<T>

export function listRecords<T>(payload: ListPayload<T> | null | undefined): T[] {
  if (Array.isArray(payload)) {
    return payload
  }

  return payload?.records ?? []
}

export const authApi = {
  login(request: LoginRequest) {
    return http.post<ApiResponse<TokenResponse>>('/auth/login', request)
  },
  refresh(refreshToken: string) {
    return http.post<ApiResponse<TokenResponse>>('/auth/refresh', { refreshToken })
  },
  logout() {
    return http.post<ApiResponse<null>>('/auth/logout')
  },
}

export const userApi = {
  me() {
    return http.get<ApiResponse<UserResponse>>('/users/me')
  },
  list(params: PageQuery) {
    return http.get<ApiResponse<ListPayload<UserResponse>>>('/users', { params })
  },
  create(request: Payload) {
    return http.post<ApiResponse<UserResponse>>('/users', request)
  },
  update(id: number, request: Payload) {
    return http.put<ApiResponse<UserResponse>>(`/users/${id}`, request)
  },
  remove(id: number) {
    return http.delete<ApiResponse<null>>(`/users/${id}`)
  },
}

export const ruleApi = {
  list(params: PageQuery) {
    return http.get<ApiResponse<ListPayload<RuleResponse>>>('/rules', { params })
  },
  create(request: Payload) {
    return http.post<ApiResponse<RuleResponse>>('/rules', request)
  },
  update(id: number, request: Payload) {
    return http.put<ApiResponse<RuleResponse>>(`/rules/${id}`, request)
  },
  remove(id: number) {
    return http.delete<ApiResponse<null>>(`/rules/${id}`)
  },
  enable(id: number) {
    return http.post<ApiResponse<RuleResponse>>(`/rules/${id}/enable`)
  },
  disable(id: number) {
    return http.post<ApiResponse<RuleResponse>>(`/rules/${id}/disable`)
  },
}

export const eventApi = {
  list(params: PageQuery & { riskLevel?: string; status?: string }) {
    return http.get<ApiResponse<ListPayload<EventResponse>>>('/events', { params })
  },
  create(request: Payload) {
    return http.post<ApiResponse<EventResponse>>('/events', request)
  },
  update(id: number, request: Payload) {
    return http.put<ApiResponse<EventResponse>>(`/events/${id}`, request)
  },
  resolve(id: number) {
    return http.post<ApiResponse<EventResponse>>(`/events/${id}/resolve`)
  },
}

export const scoreApi = {
  list(params: PageQuery) {
    return http.get<ApiResponse<ListPayload<ScoreResponse>>>('/scores', { params })
  },
  evaluate(request: Payload) {
    return http.post<ApiResponse<ScoreResponse>>('/scores/evaluate', request)
  },
  latestBySubject(subjectType: string, subjectId: number) {
    return http.get<ApiResponse<ScoreResponse>>(`/scores/subject/${subjectType}/${subjectId}`)
  },
}

export const decisionApi = {
  list(params: PageQuery) {
    return http.get<ApiResponse<ListPayload<DecisionResponse>>>('/decisions', { params })
  },
  create(request: Payload) {
    return http.post<ApiResponse<DecisionResponse>>('/decisions', request)
  },
  update(id: number, request: Payload) {
    return http.put<ApiResponse<DecisionResponse>>(`/decisions/${id}`, request)
  },
}

export const auditApi = {
  list(params: PageQuery & { userId?: number; action?: string; resourceType?: string }) {
    return http.get<ApiResponse<ListPayload<AuditLogResponse>>>('/audit-logs', { params })
  },
}
