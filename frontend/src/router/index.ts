import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'dashboard',
        component: () => import('@/views/DashboardView.vue'),
      },
      {
        path: 'users',
        name: 'users',
        component: () => import('@/views/UserManagementView.vue'),
      },
      {
        path: 'rules',
        name: 'rules',
        component: () => import('@/views/RuleManagementView.vue'),
      },
      {
        path: 'events',
        name: 'events',
        component: () => import('@/views/EventAnalysisView.vue'),
      },
      {
        path: 'decisions',
        name: 'score-decisions',
        component: () => import('@/views/ScoreDecisionView.vue'),
      },
      {
        path: 'audit-logs',
        name: 'audit-logs',
        component: () => import('@/views/AuditLogView.vue'),
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
