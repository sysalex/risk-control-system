import { defineStore } from 'pinia'

interface AppState {
  appName: string
  sidebarCollapsed: boolean
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    appName: '风控系统',
    sidebarCollapsed: false,
  }),
  actions: {
    setSidebarCollapsed(collapsed: boolean) {
      this.sidebarCollapsed = collapsed
    },
  },
})
