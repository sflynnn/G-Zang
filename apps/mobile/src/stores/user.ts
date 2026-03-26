import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 类型定义
export interface UserState {
  currentUser: any | null
  profile: any | null
  loading: boolean
}

// Store定义
export const useUserStore = defineStore('user', () => {
  // 状态
  const currentUser = ref<any | null>(null)
  const profile = ref<any | null>(null)
  const loading = ref(false)

  // 计算属性
  const isProfileComplete = computed(() => {
    return profile.value?.nickname && profile.value?.avatar
  })

  const userDisplayName = computed(() => {
    return profile.value?.nickname || currentUser.value?.username || '用户'
  })

  // 更新用户信息
  const updateUserInfo = (userInfo: any) => {
    currentUser.value = { ...currentUser.value, ...userInfo }
  }

  // 更新用户资料
  const updateProfile = (userProfile: any) => {
    profile.value = { ...profile.value, ...userProfile }
  }

  // 清空用户状态
  const clearUserState = () => {
    currentUser.value = null
    profile.value = null
  }

  return {
    // 状态
    currentUser: computed(() => currentUser.value),
    profile: computed(() => profile.value),
    loading: computed(() => loading.value),

    // 计算属性
    isProfileComplete,
    userDisplayName,

    // 方法
    updateUserInfo,
    updateProfile,
    clearUserState
  }
})
