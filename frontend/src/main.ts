import './styles/main.css'

import { createPinia } from 'pinia'
import { createApp } from 'vue'

import App from './App.vue'
import router from './router'
import { setupErrorReporting } from './utils/error-reporting'

const app = createApp(App)

setupErrorReporting(app)

app.use(createPinia())
app.use(router)

app.mount('#app')
