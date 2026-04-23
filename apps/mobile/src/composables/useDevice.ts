/**
 * 设备信息 Composable
 */
import { ref, onMounted } from 'vue'
import type { DeviceInfo, NetworkStatus } from '@/types/device'

export function useDevice() {
  const deviceInfo = ref<DeviceInfo | null>(null)
  const networkStatus = ref<NetworkStatus>({
    isConnected: true,
    networkType: 'unknown'
  })

  const getDeviceInfo = (): DeviceInfo => {
    const systemInfo = uni.getSystemInfoSync()
    return {
      platform: (systemInfo.platform as DeviceInfo['platform']) || 'h5',
      os: systemInfo.osName || 'unknown',
      osVersion: systemInfo.system,
      brand: systemInfo.brand,
      model: systemInfo.model,
      screenWidth: systemInfo.screenWidth,
      screenHeight: systemInfo.screenHeight,
      pixelRatio: systemInfo.pixelRatio || 2,
      windowWidth: systemInfo.windowWidth,
      windowHeight: systemInfo.windowHeight,
      statusBarHeight: systemInfo.statusBarHeight,
      navigationBarHeight: systemInfo.navigationBarHeight
    }
  }

  const initNetworkListener = () => {
    uni.onNetworkStatusChange((res) => {
      networkStatus.value = {
        isConnected: res.isConnected,
        networkType: res.networkType as NetworkStatus['networkType']
      }
    })
  }

  const getNetworkType = (): Promise<string> => {
    return new Promise((resolve) => {
      uni.getNetworkType({
        success: (res) => resolve(res.networkType),
        fail: () => resolve('unknown')
      })
    })
  }

  onMounted(() => {
    deviceInfo.value = getDeviceInfo()
    initNetworkListener()
  })

  return {
    deviceInfo,
    networkStatus,
    getDeviceInfo,
    getNetworkType
  }
}
