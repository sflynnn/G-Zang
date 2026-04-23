import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getCurrentUser as getCurrentUserApi } from '@/api/auth'
import { saveToken, clearToken as clearTokenLocal } from '@/api/auth'

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
  companyId?: number
  status?: number
  role?: string
  permissions?: string[]
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
  const isAuthenticated = computed(() => {
    return !!token.value && !checkTokenExpired()
  })
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const displayName = computed(() => {
    return user.value?.nickname || user.value?.username || '未登录'
  })

  // 登录
  const login = async (params: LoginParams) => {
    try {
      loading.value = true
      const response = await loginApi(params)

      // 更新状态
      user.value = response.user
      token.value = response.token
      refreshToken.value = (response as any).refreshToken || ''

      // 本地存储
      saveToken(token.value, refreshToken.value)
      uni.setStorageSync('user', JSON.stringify(user.value))

      return response
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 注册
  const register = async (params: any) => {
    try {
      loading.value = true
      const { register: registerApi } = await import('@/api/auth')
      await registerApi(params)

      // 自动登录
      await login({
        username: params.username,
        password: params.password
      })

      return true
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = async () => {
    try {
      const { logout: logoutApi } = await import('@/api/auth')
      await logoutApi()
    } catch (e) {
      // ignore logout API error
    } finally {
      user.value = null
      token.value = ''
      refreshToken.value = ''

      // 清除本地存储
      clearTokenLocal()
      uni.removeStorageSync('user')

      // 跳转到登录页
      uni.reLaunch({
        url: '/pages/login/index'
      })
    }
  }

  // 刷新token
  const refresh = async () => {
    try {
      const { refreshToken: refreshTokenApi } = await import('@/api/auth')
      const response = await refreshTokenApi()

      token.value = response.token
      refreshToken.value = (response as any).refreshToken || ''

      // 更新本地存储
      saveToken(token.value, refreshToken.value)
    } catch (error) {
      logout() // 刷新失败，清除登录状态
      throw error
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      loading.value = true
      const response = await getCurrentUserApi()
      user.value = response

      // 更新本地存储
      uni.setStorageSync('user', JSON.stringify(user.value))

      return response
    } catch (error) {
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

      // 优先使用 token 判断是否登录（refreshToken 可能后端未返回）
      if (savedToken) {
        // 先设置 token（即使过期也保留，用于后续刷新）
        token.value = savedToken
        refreshToken.value = savedRefreshToken || ''

        // 验证 token 是否过期
        if (checkTokenExpired()) {
          // Token 已过期，清除
          token.value = ''
          refreshToken.value = ''
          clearTokenLocal()
          uni.removeStorageSync('user')
        } else {
          // Token 有效，恢复用户信息
          if (savedUser) {
            user.value = JSON.parse(savedUser)
          }
        }
      }
    } catch (error) {
      // 解析失败，清除状态
      token.value = ''
      refreshToken.value = ''
      clearTokenLocal()
      uni.removeStorageSync('user')
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
