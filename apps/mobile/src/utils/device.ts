// G-Zang 移动端设备工具函数

// 设备信息接口
interface DeviceInfo {
  platform: 'ios' | 'android' | 'web'
  model: string
  system: string
  version: string
  statusBarHeight: number
  windowWidth: number
  windowHeight: number
  pixelRatio: number
  safeAreaInsets: {
    top: number
    bottom: number
    left: number
    right: number
  }
}

// 网络状态接口
interface NetworkInfo {
  isConnected: boolean
  networkType: 'wifi' | '2g' | '3g' | '4g' | '5g' | 'none' | 'unknown'
}

// 位置信息接口
interface LocationInfo {
  latitude: number
  longitude: number
  altitude?: number
  accuracy: number
  altitudeAccuracy?: number
  speed?: number
  timestamp: number
}

// 设备工具类
export class DeviceUtils {
  private static deviceInfo: DeviceInfo | null = null
  private static networkInfo: NetworkInfo | null = null

  // 获取设备信息
  static async getDeviceInfo(): Promise<DeviceInfo> {
    if (this.deviceInfo) {
      return this.deviceInfo
    }

    return new Promise((resolve, reject) => {
      uni.getSystemInfo({
        success: (res) => {
          this.deviceInfo = {
            platform: res.platform as 'ios' | 'android' | 'web',
            model: res.model,
            system: res.system,
            version: res.version,
            statusBarHeight: res.statusBarHeight || 0,
            windowWidth: res.windowWidth,
            windowHeight: res.windowHeight,
            pixelRatio: res.pixelRatio,
            safeAreaInsets: {
              top: res.safeAreaInsets?.top || 0,
              bottom: res.safeAreaInsets?.bottom || 0,
              left: res.safeAreaInsets?.left || 0,
              right: res.safeAreaInsets?.right || 0
            }
          }
          resolve(this.deviceInfo)
        },
        fail: reject
      })
    })
  }

  // 获取网络状态
  static async getNetworkInfo(): Promise<NetworkInfo> {
    return new Promise((resolve) => {
      uni.getNetworkType({
        success: (res) => {
          const networkInfo: NetworkInfo = {
            isConnected: res.networkType !== 'none',
            networkType: res.networkType as NetworkInfo['networkType']
          }
          this.networkInfo = networkInfo
          resolve(networkInfo)
        },
        fail: () => {
          resolve({
            isConnected: false,
            networkType: 'unknown'
          })
        }
      })
    })
  }

  // 获取当前位置
  static async getCurrentLocation(options: {
    type?: 'wgs84' | 'gcj02'
    altitude?: boolean
    timeout?: number
  } = {}): Promise<LocationInfo> {
    const { type = 'wgs84', altitude = false, timeout = 10000 } = options

    return new Promise((resolve, reject) => {
      uni.getLocation({
        type,
        altitude,
        timeout,
        success: (res) => {
          resolve({
            latitude: res.latitude,
            longitude: res.longitude,
            altitude: res.altitude,
            accuracy: res.accuracy || 0,
            altitudeAccuracy: res.altitudeAccuracy,
            speed: res.speed,
            timestamp: Date.now()
          })
        },
        fail: reject
      })
    })
  }

  // 监听网络状态变化
  static onNetworkStatusChange(callback: (networkInfo: NetworkInfo) => void): void {
    uni.onNetworkStatusChange((res) => {
      const networkInfo: NetworkInfo = {
        isConnected: res.isConnected,
        networkType: res.networkType as NetworkInfo['networkType']
      }
      this.networkInfo = networkInfo
      callback(networkInfo)
    })
  }

  // 监听位置变化
  static onLocationChange(
    callback: (location: LocationInfo) => void,
    options: { type?: 'wgs84' | 'gcj02'; timeout?: number } = {}
  ): () => void {
    const { type = 'wgs84', timeout = 10000 } = options

    const watchId = uni.onLocationChange?.((res) => {
      const location: LocationInfo = {
        latitude: res.latitude,
        longitude: res.longitude,
        accuracy: res.accuracy || 0,
        speed: res.speed,
        timestamp: Date.now()
      }
      callback(location)
    })

    return () => {
      if (watchId) {
        uni.offLocationChange?.(watchId)
      }
    }
  }

  // 检查是否为iOS设备
  static isIOS(): boolean {
    return uni.getSystemInfoSync().platform === 'ios'
  }

  // 检查是否为Android设备
  static isAndroid(): boolean {
    return uni.getSystemInfoSync().platform === 'android'
  }

  // 检查是否为Web环境
  static isWeb(): boolean {
    return uni.getSystemInfoSync().platform === 'web'
  }

  // 获取状态栏高度
  static getStatusBarHeight(): number {
    return uni.getSystemInfoSync().statusBarHeight || 0
  }

  // 获取导航栏高度
  static getNavigationBarHeight(): number {
    const systemInfo = uni.getSystemInfoSync()
    return systemInfo.statusBarHeight || 0 + 44 // iOS标准导航栏高度44px
  }

  // 获取底部安全区域高度
  static getSafeAreaBottom(): number {
    const systemInfo = uni.getSystemInfoSync()
    return systemInfo.safeAreaInsets?.bottom || 0
  }

  // 获取屏幕尺寸
  static getScreenSize(): { width: number; height: number } {
    const systemInfo = uni.getSystemInfoSync()
    return {
      width: systemInfo.windowWidth,
      height: systemInfo.windowHeight
    }
  }

  // 振动反馈
  static vibrateShort(): void {
    uni.vibrateShort?.({
      type: 'light'
    })
  }

  static vibrateLong(): void {
    uni.vibrateLong?.()
  }

  // 设备方向
  static getOrientation(): 'portrait' | 'landscape' {
    const systemInfo = uni.getSystemInfoSync()
    return systemInfo.windowWidth > systemInfo.windowHeight ? 'landscape' : 'portrait'
  }

  // 监听设备方向变化
  static onOrientationChange(callback: (orientation: 'portrait' | 'landscape') => void): void {
    uni.onWindowResize?.(() => {
      callback(this.getOrientation())
    })
  }

  // 检查是否支持某功能
  static supportsFeature(feature: string): boolean {
    const systemInfo = uni.getSystemInfoSync()
    switch (feature) {
      case 'camera':
        return !!uni.getSystemInfoSync().camera
      case 'microphone':
        return true // 大多数设备都支持
      case 'location':
        return true
      case 'vibration':
        return !!uni.vibrateShort
      case 'biometric':
        return this.isIOS() || this.isAndroid() // 指纹/面部识别
      default:
        return false
    }
  }

  // 获取设备唯一标识（如果支持）
  static getDeviceId(): string | null {
    try {
      // 在uni-app中，可能需要使用特定的API
      return uni.getSystemInfoSync().deviceId || null
    } catch (error) {
      console.warn('无法获取设备ID:', error)
      return null
    }
  }

  // 检查应用是否在前台
  static isAppInForeground(): boolean {
    // uni-app没有直接的方法，但可以通过页面生命周期判断
    const pages = getCurrentPages()
    return pages.length > 0
  }

  // 获取应用版本信息
  static getAppVersion(): { version: string; build: string } {
    const accountInfo = uni.getAccountInfoSync?.()
    return {
      version: accountInfo?.miniProgram?.version || '1.0.0',
      build: accountInfo?.miniProgram?.envVersion || 'release'
    }
  }
}

// 相机工具类
export class CameraUtils {
  // 拍照
  static async takePhoto(options: {
    quality?: number
    allowEdit?: boolean
    saveToPhotosAlbum?: boolean
  } = {}): Promise<string> {
    const { quality = 80, allowEdit = false, saveToPhotosAlbum = false } = options

    return new Promise((resolve, reject) => {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['camera'],
        success: (res) => {
          resolve(res.tempFilePaths[0])
        },
        fail: reject
      })
    })
  }

  // 选择图片
  static async chooseImage(options: {
    count?: number
    sizeType?: ('original' | 'compressed')[]
    sourceType?: ('album' | 'camera')[]
  } = {}): Promise<string[]> {
    const {
      count = 9,
      sizeType = ['original', 'compressed'],
      sourceType = ['album', 'camera']
    } = options

    return new Promise((resolve, reject) => {
      uni.chooseImage({
        count,
        sizeType,
        sourceType,
        success: (res) => {
          resolve(res.tempFilePaths)
        },
        fail: reject
      })
    })
  }

  // 压缩图片
  static async compressImage(imagePath: string, quality = 80): Promise<string> {
    return new Promise((resolve, reject) => {
      uni.compressImage({
        src: imagePath,
        quality,
        success: (res) => {
          resolve(res.tempFilePath)
        },
        fail: reject
      })
    })
  }
}

// 文件工具类
export class FileUtils {
  // 保存文件到相册
  static async saveToPhotosAlbum(filePath: string): Promise<void> {
    return new Promise((resolve, reject) => {
      uni.saveImageToPhotosAlbum({
        filePath,
        success: resolve,
        fail: reject
      })
    })
  }

  // 获取文件信息
  static async getFileInfo(filePath: string): Promise<{
    size: number
    createTime: number
    modifyTime?: number
  }> {
    return new Promise((resolve, reject) => {
      uni.getFileInfo({
        filePath,
        success: (res) => {
          resolve({
            size: res.size,
            createTime: res.createTime,
            modifyTime: res.modifyTime
          })
        },
        fail: reject
      })
    })
  }

  // 删除文件
  static async removeFile(filePath: string): Promise<void> {
    return new Promise((resolve, reject) => {
      uni.removeSavedFile?.({
        filePath,
        success: resolve,
        fail: reject
      })
    })
  }
}
