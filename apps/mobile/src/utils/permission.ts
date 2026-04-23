// G-Zang 移动端权限工具函数

import { useAuthStore } from '@/stores/auth'

// 权限类型
export enum Permission {
  // 用户管理
  USER_READ = 'user:read',
  USER_WRITE = 'user:write',
  USER_DELETE = 'user:delete',

  // 记账管理
  ACCOUNTING_READ = 'accounting:read',
  ACCOUNTING_WRITE = 'accounting:write',
  ACCOUNTING_DELETE = 'accounting:delete',

  // 账单管理
  BILL_READ = 'bill:read',
  BILL_WRITE = 'bill:write',
  BILL_DELETE = 'bill:delete',

  // 统计分析
  ANALYSIS_READ = 'analysis:read',
  ANALYSIS_EXPORT = 'analysis:export',

  // 系统设置
  SYSTEM_CONFIG = 'system:config',
  SYSTEM_MAINTAIN = 'system:maintain'
}

// 权限检查类
export class PermissionChecker {
  private static authStore = useAuthStore()

  // 检查是否有权限
  static hasPermission(permission: Permission | string): boolean {
    const user = this.authStore.user
    if (!user) return false

    // 管理员拥有所有权限
    if (user.role === 'ADMIN') return true

    return user.permissions?.includes(permission) || false
  }

  // 检查是否有任意一个权限
  static hasAnyPermission(permissions: (Permission | string)[]): boolean {
    return permissions.some(permission => this.hasPermission(permission))
  }

  // 检查是否有所有权限
  static hasAllPermissions(permissions: (Permission | string)[]): boolean {
    return permissions.every(permission => this.hasPermission(permission))
  }

  // 检查是否是管理员
  static isAdmin(): boolean {
    return this.authStore.isAdmin
  }

  // 检查是否已登录
  static isAuthenticated(): boolean {
    return this.authStore.isAuthenticated
  }
}

// 系统权限请求
export class SystemPermission {
  // 请求相机权限
  static async requestCameraPermission(): Promise<boolean> {
    return new Promise((resolve) => {
      uni.authorize({
        scope: 'scope.camera',
        success: () => resolve(true),
        fail: () => {
          uni.showModal({
            title: '权限提示',
            content: '需要相机权限才能拍照识别，请前往设置开启',
            showCancel: true,
            confirmText: '去设置',
            success: (res) => {
              if (res.confirm) {
                uni.openSetting()
              }
              resolve(false)
            }
          })
        }
      })
    })
  }

  // 请求麦克风权限
  static async requestMicrophonePermission(): Promise<boolean> {
    return new Promise((resolve) => {
      uni.authorize({
        scope: 'scope.record',
        success: () => resolve(true),
        fail: () => {
          uni.showModal({
            title: '权限提示',
            content: '需要麦克风权限才能语音记账，请前往设置开启',
            showCancel: true,
            confirmText: '去设置',
            success: (res) => {
              if (res.confirm) {
                uni.openSetting()
              }
              resolve(false)
            }
          })
        }
      })
    })
  }

  // 请求位置权限
  static async requestLocationPermission(): Promise<boolean> {
    return new Promise((resolve) => {
      uni.authorize({
        scope: 'scope.userLocation',
        success: () => resolve(true),
        fail: () => {
          uni.showModal({
            title: '权限提示',
            content: '需要位置权限才能获取地理位置信息，请前往设置开启',
            showCancel: true,
            confirmText: '去设置',
            success: (res) => {
              if (res.confirm) {
                uni.openSetting()
              }
              resolve(false)
            }
          })
        }
      })
    })
  }

  // 请求存储权限
  static async requestStoragePermission(): Promise<boolean> {
    return new Promise((resolve) => {
      uni.authorize({
        scope: 'scope.writePhotosAlbum',
        success: () => resolve(true),
        fail: () => {
          uni.showModal({
            title: '权限提示',
            content: '需要存储权限才能保存文件，请前往设置开启',
            showCancel: true,
            confirmText: '去设置',
            success: (res) => {
              if (res.confirm) {
                uni.openSetting()
              }
              resolve(false)
            }
          })
        }
      })
    })
  }

  // 检查权限状态
  static async checkPermission(scope: string): Promise<boolean> {
    return new Promise((resolve) => {
      uni.getSetting({
        success: (res) => {
          resolve((res.authSetting as unknown as Record<string, boolean>)?.[scope] || false)
        },
        fail: () => resolve(false)
      })
    })
  }
}

// 业务权限守卫
export class PermissionGuard {
  // 需要登录的页面守卫
  static requireAuth(): boolean {
    if (!PermissionChecker.isAuthenticated()) {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      })
      uni.reLaunch({
        url: '/pages/login/index'
      })
      return false
    }
    return true
  }

  // 需要特定权限的页面守卫
  static requirePermission(permission: Permission | string, message = '权限不足'): boolean {
    if (!PermissionChecker.hasPermission(permission)) {
      uni.showToast({
        title: message,
        icon: 'none'
      })
      uni.navigateBack()
      return false
    }
    return true
  }

  // 需要管理员权限的页面守卫
  static requireAdmin(): boolean {
    if (!PermissionChecker.isAdmin()) {
      uni.showToast({
        title: '需要管理员权限',
        icon: 'none'
      })
      uni.navigateBack()
      return false
    }
    return true
  }
}

// 权限指令（如果需要的话）
export const permissionDirective = {
  mounted(el: HTMLElement, binding: any) {
    const { value } = binding
    if (value && !PermissionChecker.hasPermission(value)) {
      el.style.display = 'none'
    }
  }
}

// 权限混入（如果需要的话）
export const permissionMixin = {
  methods: {
    $hasPermission(permission: Permission | string): boolean {
      return PermissionChecker.hasPermission(permission)
    },

    $hasAnyPermission(permissions: (Permission | string)[]): boolean {
      return PermissionChecker.hasAnyPermission(permissions)
    },

    $isAdmin(): boolean {
      return PermissionChecker.isAdmin()
    }
  }
}
