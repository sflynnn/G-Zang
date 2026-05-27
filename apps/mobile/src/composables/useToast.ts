/**
 * Toast 提示 - 使用 CustomToast 组件
 * 需在每个页面中引入 CustomToast 组件使用
 */
import { reactive } from 'vue'

export type ToastType = 'success' | 'error' | 'warning' | 'info'

interface ToastOptions {
  type?: ToastType
  message: string
  duration?: number
}

interface ToastItem {
  id: number
  message: string
  type: ToastType
  visible: boolean
}

// Toast 队列
const toastList = reactive<ToastItem[]>([])
let toastId = 0

function getIconConfig(type: ToastType) {
  const configs = {
    success: { icon: 'check-circle', color: '#06D6A0' },
    error: { icon: 'x-circle', color: '#EF476F' },
    warning: { icon: 'alert-circle', color: '#FB8B24' },
    info: { icon: 'info', color: '#0F4C5C' },
  }
  return configs[type]
}

function removeToast(id: number) {
  const index = toastList.findIndex(t => t.id === id)
  if (index > -1) {
    toastList.splice(index, 1)
  }
}

function showToast(options: ToastOptions | string): void {
  const opts: ToastOptions = typeof options === 'string' ? { message: options } : options
  const { type = 'info', message, duration = 2000 } = opts

  const id = ++toastId
  toastList.push({ id, message, type, visible: true })
  setTimeout(() => removeToast(id), duration)
}

function hideToast(): void {
  toastList.splice(0, toastList.length)
}

// Composable 方式
export function useToast() {
  return {
    list: toastList,
    show: showToast,
    success: (message: string, duration?: number) => showToast({ type: 'success', message, duration }),
    error: (message: string, duration?: number) => showToast({ type: 'error', message, duration }),
    warning: (message: string, duration?: number) => showToast({ type: 'warning', message, duration }),
    info: (message: string, duration?: number) => showToast({ type: 'info', message, duration }),
    hide: hideToast,
    getIconConfig,
  }
}

// 命名导出（供 store/api 等非组件环境使用）
export const toast = {
  list: toastList,
  show: showToast,
  success: (message: string, duration?: number) => showToast({ type: 'success', message, duration }),
  error: (message: string, duration?: number) => showToast({ type: 'error', message, duration }),
  warning: (message: string, duration?: number) => showToast({ type: 'warning', message, duration }),
  info: (message: string, duration?: number) => showToast({ type: 'info', message, duration }),
  hide: hideToast,
  getIconConfig,
}

export default useToast
