import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

import { ACCESS_TOKEN_KEY } from '@/api/http'

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

router.beforeEach((to) => {
  const token = localStorage.getItem(ACCESS_TOKEN_KEY)
  if (!token && to.path !== '/login') {
    return { path: '/login' }
  }
})

export default router
