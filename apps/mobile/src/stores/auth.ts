import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@shared/api'

// 类型定义
export interface LoginParams {
  username: string
  password: string
  remember?: boolean
}

export interface UserInfo {
  id: number
  username: string
  nickname?: string
  avatar?: string
  role: string
  companyId?: number
  permissions: string[]
}

export interface AuthState {
  token: string
  refreshToken: string
  user: UserInfo | null
  loading: boolean
}

// Store定义
export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref('')
  const refreshToken = ref('')
  const user = ref<UserInfo | null>(null)
  const loading = ref(false)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const displayName = computed(() => {
    return user.value?.nickname || user.value?.username || '未登录'
  })

  // 登录
  const login = async (params: LoginParams) => {
    try {
      loading.value = true
      const response = await authApi.login(params)

      // 更新状态
      user.value = response.data.user
      token.value = response.data.token
      refreshToken.value = response.data.refreshToken

      // 本地存储
      uni.setStorageSync('token', token.value)
      uni.setStorageSync('refreshToken', refreshToken.value)
      uni.setStorageSync('user', JSON.stringify(user.value))

      return response
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 注册
  const register = async (params: any) => {
    try {
      loading.value = true
      const response = await authApi.register(params)

      // 自动登录
      await login({
        username: params.username,
        password: params.password
      })

      return response
    } catch (error) {
      console.error('注册失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = () => {
    user.value = null
    token.value = ''
    refreshToken.value = ''

    // 清除本地存储
    uni.removeStorageSync('token')
    uni.removeStorageSync('refreshToken')
    uni.removeStorageSync('user')

    // 跳转到登录页
    uni.reLaunch({
      url: '/pages/login/index'
    })
  }

  // 刷新token
  const refresh = async () => {
    try {
      const response = await authApi.refresh({
        refreshToken: refreshToken.value
      })

      token.value = response.data.token
      refreshToken.value = response.data.refreshToken

      // 更新本地存储
      uni.setStorageSync('token', token.value)
      uni.setStorageSync('refreshToken', refreshToken.value)
    } catch (error) {
      console.error('刷新token失败:', error)
      logout() // 刷新失败，清除登录状态
      throw error
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      loading.value = true
      const response = await authApi.getCurrentUser()
      user.value = response.data

      // 更新本地存储
      uni.setStorageSync('user', JSON.stringify(user.value))

      return response
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 初始化（从本地存储恢复状态）
  const init = () => {
    try {
      const savedToken = uni.getStorageSync('token')
      const savedRefreshToken = uni.getStorageSync('refreshToken')
      const savedUser = uni.getStorageSync('user')

      if (savedToken && savedRefreshToken) {
        token.value = savedToken
        refreshToken.value = savedRefreshToken

        if (savedUser) {
          user.value = JSON.parse(savedUser)
        }
      }
    } catch (error) {
      console.error('初始化认证状态失败:', error)
      logout()
    }
  }

  // 检查token是否过期
  const checkTokenExpired = () => {
    if (!token.value) return true

    try {
      // 简单的JWT过期检查（实际项目中应该解析token）
      const payload = JSON.parse(atob(token.value.split('.')[1]))
      const exp = payload.exp * 1000 // 转换为毫秒
      return Date.now() >= exp
    } catch (error) {
      console.error('解析token失败:', error)
      return true
    }
  }

  return {
    // 状态
    token: computed(() => token.value),
    refreshToken: computed(() => refreshToken.value),
    user: computed(() => user.value),
    loading: computed(() => loading.value),

    // 计算属性
    isAuthenticated,
    isAdmin,
    displayName,

    // 方法
    login,
    register,
    logout,
    refresh,
    getUserInfo,
    init,
    checkTokenExpired
  }
})
