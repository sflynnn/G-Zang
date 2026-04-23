/**
 * 设备相关类型定义
 */

// 设备平台
export type Platform = 'app' | 'h5' | 'mp-weixin' | 'mp-alipay' | 'mp-baidu' | 'mp-toutiao'

// 网络状态
export interface NetworkStatus {
  isConnected: boolean
  networkType: 'wifi' | '2g' | '3g' | '4g' | '5g' | 'unknown'
}

// 设备信息
export interface DeviceInfo {
  platform: Platform
  os: string  // 'ios' | 'android' | 'windows' | 'mac' | 'linux'
  osVersion?: string
  brand?: string
  model?: string
  screenWidth: number
  screenHeight: number
  pixelRatio: number
  windowWidth: number
  windowHeight: number
  statusBarHeight?: number
  navigationBarHeight?: number
  safeArea?: {
    top: number
    bottom: number
    left: number
    right: number
  }
}

// 系统权限
export type SystemPermission =
  | 'Camera'         // 相机
  | 'Microphone'     // 麦克风
  | 'Location'        // 位置
  | 'Storage'        // 存储
  | 'Notification'   // 通知
  | 'Biometric'      // 生物识别

// 权限状态
export interface PermissionStatus {
  permission: SystemPermission
  granted: boolean
  denied?: boolean
  restricted?: boolean
}

// 同步状态
export interface SyncStatus {
  pending: number    // 待同步数量
  syncing: boolean   // 同步中
  lastSyncTime?: string
  error?: string
}

// 离线记录
export interface OfflineRecord {
  id: string
  type: 'transaction' | 'attachment' | 'category' | 'account'
  action: 'create' | 'update' | 'delete'
  data: any
  timestamp: string
  retryCount: number
}
