/**
 * 微前端全局状态管理
 * 实现主子应用间的状态同步
 */
import { ref, computed, watch } from 'vue'

// 全局状态
interface GlobalState {
  user: any | null
  companyId: number | null
  token: string | null
  theme: 'light' | 'dark'
  language: string
  sidebarCollapsed: boolean
}

// 状态存储
const state = ref<GlobalState>({
  user: null,
  companyId: null,
  token: null,
  theme: 'light',
  language: 'zh-CN',
  sidebarCollapsed: false
})

// 状态变更监听器
type StateChangeListener = (key: keyof GlobalState, value: any, prevValue: any) => void
const listeners: Set<StateChangeListener> = new Set()

// 派发状态变更的函数
let dispatch: ((state: Partial<GlobalState>) => void) | null = null

/**
 * 初始化全局状态
 */
export function initGlobalState(initialState?: Partial<GlobalState>) {
  if (initialState) {
    Object.assign(state.value, initialState)
  }

  // 创建派发函数
  dispatch = (newState: Partial<GlobalState>) => {
    const prevState = { ...state.value }
    Object.assign(state.value, newState)

    // 通知所有监听器
    Object.keys(newState).forEach(key => {
      const k = key as keyof GlobalState
      listeners.forEach(listener => {
        listener(k, state.value[k], prevState[k])
      })
    })
  }

  return dispatch
}

/**
 * 获取派发函数（供 qiankun 使用）
 */
export function getMicroAppDispatch() {
  if (!dispatch) {
    initGlobalState()
  }
  return dispatch
}

/**
 * 订阅状态变更
 */
export function subscribeStateChange(listener: StateChangeListener) {
  listeners.add(listener)
  return () => {
    listeners.delete(listener)
  }
}

/**
 * 获取当前全局状态
 */
export function getGlobalState(): GlobalState {
  return state.value
}

/**
 * 获取单个状态
 */
export function getState<K extends keyof GlobalState>(key: K): GlobalState[K] {
  return state.value[key]
}

/**
 * 监听单个状态变化
 */
export function watchState<K extends keyof GlobalState>(
  key: K,
  callback: (value: GlobalState[K], prevValue: GlobalState[K]) => void
) {
  return watch(
    () => state.value[key],
    (value, prevValue) => {
      callback(value, prevValue)
    }
  )
}

/**
 * 创建子应用状态代理
 * 子应用可以通过此代理访问和修改全局状态
 */
export function createStateProxy() {
  return {
    // 获取用户信息
    getUser: () => state.value.user,

    // 获取公司ID
    getCompanyId: () => state.value.companyId,

    // 获取Token
    getToken: () => state.value.token,

    // 获取主题
    getTheme: () => state.value.theme,

    // 获取语言
    getLanguage: () => state.value.language,

    // 更新用户信息
    setUser: (user: any) => {
      dispatch?.({ user })
    },

    // 更新Token
    setToken: (token: string) => {
      dispatch?.({ token })
    },

    // 更新主题
    setTheme: (theme: 'light' | 'dark') => {
      dispatch?.({ theme })
    },

    // 更新语言
    setLanguage: (language: string) => {
      dispatch?.({ language })
    },

    // 登出
    logout: () => {
      dispatch?.({
        user: null,
        companyId: null,
        token: null
      })
    }
  }
}

// 组合式函数：供 Vue 组件使用
export function useGlobalState() {
  const user = computed(() => state.value.user)
  const companyId = computed(() => state.value.companyId)
  const token = computed(() => state.value.token)
  const theme = computed(() => state.value.theme)
  const language = computed(() => state.value.language)
  const sidebarCollapsed = computed(() => state.value.sidebarCollapsed)

  const isLoggedIn = computed(() => !!state.value.token)
  const isDarkMode = computed(() => state.value.theme === 'dark')

  return {
    // 状态
    user,
    companyId,
    token,
    theme,
    language,
    sidebarCollapsed,

    // 计算属性
    isLoggedIn,
    isDarkMode,

    // 方法
    setUser: (user: any) => dispatch?.({ user }),
    setToken: (token: string) => dispatch?.({ token }),
    setTheme: (theme: 'light' | 'dark') => dispatch?.({ theme }),
    setLanguage: (language: string) => dispatch?.({ language }),
    setSidebarCollapsed: (collapsed: boolean) => dispatch?.({ sidebarCollapsed: collapsed }),
    logout: () => dispatch?.({ user: null, companyId: null, token: null })
  }
}

export default {
  initGlobalState,
  getMicroAppDispatch,
  subscribeStateChange,
  getGlobalState,
  getState,
  watchState,
  createStateProxy,
  useGlobalState
}
