/**
 * Toast 提示组件
 * 需在每个页面中引入使用
 */
import { ref } from 'vue'

export type ToastType = 'success' | 'error' | 'warning' | 'info'

interface ToastItem {
  id: number
  message: string
  type: ToastType
}

// 全局状态
const toastList = ref<ToastItem[]>([])
let toastId = 0

// 图标配置
const iconMap: Record<ToastType, { name: string; color: string }> = {
  success: { name: 'check-circle', color: '#06D6A0' },
  error: { name: 'x-circle', color: '#EF476F' },
  warning: { name: 'alert-circle', color: '#FB8B24' },
  info: { name: 'info', color: '#0F4C5C' },
}

function removeToast(id: number) {
  const index = toastList.value.findIndex(t => t.id === id)
  if (index > -1) {
    toastList.value.splice(index, 1)
  }
}

function showToast(options: { type?: ToastType; message: string; duration?: number } | string) {
  const opts = typeof options === 'string' ? { message: options } : options
  const { type = 'info', message, duration = 2000 } = opts

  const id = ++toastId
  toastList.value.push({ id, message, type })

  setTimeout(() => removeToast(id), duration)
}

// 导出 composable
export function useCustomToast() {
  return {
    list: toastList,
    iconMap,
    show: showToast,
    success: (message: string, duration?: number) => showToast({ type: 'success', message, duration }),
    error: (message: string, duration?: number) => showToast({ type: 'error', message, duration }),
    warning: (message: string, duration?: number) => showToast({ type: 'warning', message, duration }),
    info: (message: string, duration?: number) => showToast({ type: 'info', message, duration }),
  }
}

export default useCustomToast
