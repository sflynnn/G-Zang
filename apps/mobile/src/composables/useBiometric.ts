/**
 * 生物识别 Composable
 */
import { ref } from 'vue'

export function useBiometric() {
  const isSupported = ref(false)
  const isAvailable = ref(false)
  const biometricType = ref<'fingerPrint' | 'facial' | ''>('')

  const checkSupport = () => {
    // #ifdef APP-PLUS
    const biometric = uni.requireNativePlugin('plus-bimetric')
    if (biometric) {
      isSupported.value = true
      biometric.isSupport((res: any) => {
        isAvailable.value = res.result
        biometricType.value = res.type || ''
      })
    }
    // #endif

    // #ifdef MP-WEIXIN
    wx.checkIsSupportFacialEvaluation?.({
      success: () => {
        isSupported.value = true
        isAvailable.value = true
        biometricType.value = 'facial'
      },
      fail: () => {
        isSupported.value = true
        isAvailable.value = false
      }
    })
    // #endif
  }

  const authenticate = (desc = '验证身份'): Promise<boolean> => {
    return new Promise((resolve) => {
      // #ifdef APP-PLUS
      const biometric = uni.requireNativePlugin('plus-bimetric')
      biometric.authenticate(
        { tip: desc },
        (res: any) => resolve(res.result),
        (res: any) => {
          resolve(false)
        }
      )
      // #endif

      // #ifdef MP-WEIXIN
      wx.startFacialRecognitionVerify({
        name: '',
        idCardNumber: '',
        success: () => resolve(true),
        fail: () => resolve(false)
      })
      // #endif

      // #ifdef H5
      resolve(false)
      // #endif
    })
  }

  checkSupport()

  return {
    isSupported,
    isAvailable,
    biometricType,
    authenticate
  }
}
