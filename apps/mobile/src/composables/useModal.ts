/**
 * Modal 模态框 - 使用 CustomModal 组件
 * 需在每个页面中引入 CustomModal 组件使用
 * 支持组件内 useModal() 和全局 modal.xxx() 两种调用方式
 */
import { reactive } from 'vue'

export interface ModalOptions {
  title?: string
  message: string
  confirmText?: string
  cancelText?: string
  showCancel?: boolean
  onConfirm?: () => void
  onCancel?: () => void
}

interface ModalState {
  visible: boolean
  title: string
  message: string
  confirmText: string
  cancelText: string
  showCancel: boolean
  resolve: ((value: boolean) => void) | null
}

const modalState = reactive<ModalState>({
  visible: false,
  title: '',
  message: '',
  confirmText: '确定',
  cancelText: '取消',
  showCancel: true,
  resolve: null,
})

// 模块级回调引用
let pendingResolve: ((value: boolean) => void) | null = null

function showModal(options: ModalOptions | string): Promise<boolean> {
  const opts: ModalOptions = typeof options === 'string'
    ? { message: options }
    : options

  modalState.visible = true
  modalState.title = opts.title || ''
  modalState.message = opts.message
  modalState.confirmText = opts.confirmText || '确定'
  modalState.cancelText = opts.cancelText || '取消'
  modalState.showCancel = opts.showCancel !== false
  modalState.resolve = null

  return new Promise((resolve) => {
    pendingResolve = resolve
    modalState.resolve = resolve

    if (opts.onConfirm) {
      const originalResolve = resolve
      pendingResolve = (confirmed: boolean) => {
        if (confirmed && opts.onConfirm) opts.onConfirm()
        if (!confirmed && opts.onCancel) opts.onCancel()
        originalResolve(confirmed)
      }
      modalState.resolve = pendingResolve
    }
  })
}

function hideModal() {
  if (pendingResolve) {
    pendingResolve(false)
    pendingResolve = null
  }
  modalState.visible = false
  modalState.resolve = null
}

function confirm() {
  if (pendingResolve) {
    pendingResolve(true)
    pendingResolve = null
  }
  modalState.visible = false
  modalState.resolve = null
}

function cancel() {
  if (pendingResolve) {
    pendingResolve(false)
    pendingResolve = null
  }
  modalState.visible = false
  modalState.resolve = null
}

// 全局 modal 实例
export const modal = {
  state: modalState,
  show: showModal,
  hide: hideModal,
  confirm,
  cancel,
}

// Composable 方式
export function useModal() {
  return {
    state: modalState,
    show: showModal,
    hide: hideModal,
    confirm,
    cancel,
  }
}

export default modal
