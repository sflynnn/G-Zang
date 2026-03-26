import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'

// 引入全局样式
import './styles/main.scss'

// 引入uni-ui组件库
import uniUI from '@dcloudio/uni-ui'

export function createApp() {
  const app = createSSRApp(App)

  // 使用 Pinia 状态管理
  const pinia = createPinia()
  app.use(pinia)

  // 使用 uni-ui 组件库
  app.use(uniUI)

  return {
    app,
    pinia
  }
}
