import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'

// 引入全局配置（必须在其他模块之前加载，以禁用生产环境 console）
import '@/config/env'

// 引入全局样式
import './styles/main.scss'
import './styles/tailwind.css'

// 引入 i18n
import i18n from './i18n'

export function createApp() {
  const app = createSSRApp(App)

  // 使用 Pinia 状态管理
  const pinia = createPinia()
  app.use(pinia)

  // 使用 i18n 国际化
  app.use(i18n)

  // 初始化全局设置（主题、语言必须在 pinia 和 i18n 安装后尽早执行，防止首屏闪动）
  const authStore = useAuthStore()
  const appStore = useAppStore()
  authStore.init()
  appStore.init()

  return {
    app,
    pinia
  }
}
