/**
 * 推送通知 Composable
 */
import { ref } from 'vue'

export function usePush() {
  const clientId = ref('')
  const isEnabled = ref(false)

  const initPush = () => {
    // #ifdef APP-PLUS
    const push = uni.requireNativePlugin('plus-push')
    if (push) {
      push.init()
      push.getClientInfo({
        success: (res: any) => {
          clientId.value = res.clientid || ''
        }
      })
    }
    // #endif

    // #ifdef MP-WEIXIN
    wx.getSetting({
      success: (res: any) => {
        isEnabled.value = res.authSetting['scope.notifyMsg'] || false
      }
    })
    // #endif
  }

  const requestPermission = (): Promise<boolean> => {
    return new Promise((resolve) => {
      // #ifdef APP-PLUS
      const push = uni.requireNativePlugin('plus-push')
      push.requestPermission((res: any) => {
        isEnabled.value = res.granted
        resolve(res.granted)
      })
      // #endif

      // #ifdef MP-WEIXIN
      wx.requestSubscribeMessage({
        tmplIds: [],
        success: () => resolve(true),
        fail: () => resolve(false)
      })
      // #endif

      // #ifdef H5
      resolve(false)
      // #endif
    })
  }

  const onMessage = (callback: (message: any) => void) => {
    // #ifdef APP-PLUS
    const push = uni.requireNativePlugin('plus-push')
    push.addEventListener('message', callback)
    // #endif
  }

  return {
    clientId,
    isEnabled,
    initPush,
    requestPermission,
    onMessage
  }
}
