import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 类型定义
export interface AppState {
  theme: 'light' | 'dark' | 'auto'
  language: string
  currency: string
  networkStatus: 'online' | 'offline'
  loading: boolean
  error: string | null
  notifications: Array<{
    id: string
    type: 'info' | 'success' | 'warning' | 'error'
    title: string
    message: string
    timestamp: number
    read?: boolean
  }>
}

// Store定义
export const useAppStore = defineStore('app', () => {
  // 状态
  const theme = ref<'light' | 'dark' | 'auto'>('light')
  const language = ref('zh-CN')
  const currency = ref('CNY')
  const networkStatus = ref<'online' | 'offline'>('online')
  const loading = ref(false)
  const error = ref<string | null>(null)
  const notifications = ref<Array<{
    id: string
    type: 'info' | 'success' | 'warning' | 'error'
    title: string
    message: string
    timestamp: number
    read?: boolean
  }>>([])

  // 计算属性
  const isDark = computed(() => {
    if (theme.value === 'auto') {
      // 检测系统主题
      return uni.getSystemInfoSync().theme === 'dark'
    }
    return theme.value === 'dark'
  })

  const isOnline = computed(() => networkStatus.value === 'online')
  const hasError = computed(() => !!error.value)
  const unreadNotifications = computed(() =>
    notifications.value.filter(n => !n.read)
  )

  // 设置主题
  const setTheme = (newTheme: 'light' | 'dark' | 'auto') => {
    theme.value = newTheme
    uni.setStorageSync('theme', newTheme)

    // 应用主题到页面
    applyTheme()
  }

  // 设置语言
  const setLanguage = (newLanguage: string) => {
    language.value = newLanguage
    uni.setStorageSync('language', newLanguage)
    // 同步 i18n
    import('@/i18n').then(({ setLocale }) => {
      setLocale(newLanguage as 'zh-CN' | 'en-US')
    })
  }

  // 设置货币
  const setCurrency = (newCurrency: string) => {
    currency.value = newCurrency
    uni.setStorageSync('currency', newCurrency)
  }

  // 应用主题
  const applyTheme = () => {
    const root = document.documentElement
    if (isDark.value) {
      root.setAttribute('data-theme', 'dark')
    } else {
      root.removeAttribute('data-theme')
    }
  }

  // 设置网络状态
  const setNetworkStatus = (status: 'online' | 'offline') => {
    networkStatus.value = status
  }

  // 设置加载状态
  const setLoading = (value: boolean) => {
    loading.value = value
  }

  // 设置错误信息
  const setError = (message: string | null) => {
    error.value = message
  }

  // 添加通知
  const addNotification = (notification: Omit<AppState['notifications'][0], 'id' | 'timestamp'>) => {
    const id = Date.now().toString()
    notifications.value.unshift({
      id,
      timestamp: Date.now(),
      ...notification
    })

    // 限制通知数量
    if (notifications.value.length > 50) {
      notifications.value = notifications.value.slice(0, 50)
    }

    // 显示Toast提示
    uni.showToast({
      title: notification.message,
      icon: notification.type === 'success' ? 'success' :
            notification.type === 'error' ? 'error' : 'none',
      duration: 2000
    })
  }

  // 移除通知
  const removeNotification = (id: string) => {
    notifications.value = notifications.value.filter(n => n.id !== id)
  }

  // 清空通知
  const clearNotifications = () => {
    notifications.value = []
  }

  // 显示成功消息
  const showSuccess = (message: string, title = '成功') => {
    addNotification({
      type: 'success',
      title,
      message
    })
  }

  // 显示错误消息
  const showError = (message: string, title = '错误') => {
    addNotification({
      type: 'error',
      title,
      message
    })
  }

  // 显示警告消息
  const showWarning = (message: string, title = '警告') => {
    addNotification({
      type: 'warning',
      title,
      message
    })
  }

  // 显示信息消息
  const showInfo = (message: string, title = '提示') => {
    addNotification({
      type: 'info',
      title,
      message
    })
  }

  // 初始化应用设置
  const init = () => {
    // 从本地存储恢复设置
    const savedTheme = uni.getStorageSync('theme')
    const savedLanguage = uni.getStorageSync('language')
    const savedCurrency = uni.getStorageSync('currency')

    if (savedTheme) {
      theme.value = savedTheme
    }

    if (savedLanguage) {
      language.value = savedLanguage
      // 同步 i18n
      import('@/i18n').then(({ setLocale }) => {
        setLocale(savedLanguage as 'zh-CN' | 'en-US')
      })
    } else {
      // 初始化时同步当前 i18n 语言到 appStore
      import('@/i18n').then(({ getLocale }) => {
        const currentLocale = getLocale()
        language.value = currentLocale
      })
    }

    if (savedCurrency) {
      currency.value = savedCurrency
    }

    // 应用主题
    applyTheme()

    // 监听系统主题变化 (auto 模式下)
    if (typeof uni.onThemeChange !== 'undefined') {
      uni.onThemeChange((res) => {
        if (theme.value === 'auto') {
          applyTheme()
        }
      })
    }

    // 监听网络状态
    uni.onNetworkStatusChange((res) => {
      setNetworkStatus(res.isConnected ? 'online' : 'offline')
    })

    // 获取初始网络状态
    uni.getNetworkType({
      success: (res) => {
        setNetworkStatus(res.networkType === 'none' ? 'offline' : 'online')
      }
    })
  }

  // 清空应用状态
  const clearState = () => {
    theme.value = 'light'
    language.value = 'zh-CN'
    currency.value = 'CNY'
    networkStatus.value = 'online'
    loading.value = false
    error.value = null
    notifications.value = []

    // 清除本地存储
    uni.removeStorageSync('theme')
    uni.removeStorageSync('language')
    uni.removeStorageSync('currency')
  }

  return {
    // 状态
    theme: computed(() => theme.value),
    language: computed(() => language.value),
    currency: computed(() => currency.value),
    networkStatus: computed(() => networkStatus.value),
    loading: computed(() => loading.value),
    error: computed(() => error.value),
    notifications: computed(() => notifications.value),

    // 计算属性
    isDark,
    isOnline,
    hasError,
    unreadNotifications,

    // 方法
    setTheme,
    setLanguage,
    setCurrency,
    applyTheme,
    setNetworkStatus,
    setLoading,
    setError,
    addNotification,
    removeNotification,
    clearNotifications,
    showSuccess,
    showError,
    showWarning,
    showInfo,
    init,
    clearState
  }
})
