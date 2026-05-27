/**
 * Modal 模态框组件
 * 需在每个页面中引入使用
 */
import { reactive, ref } from 'vue'

export interface ModalOptions {
  title?: string
  message: string
  confirmText?: string
  cancelText?: string
  showCancel?: boolean
}

interface ModalState {
  visible: boolean
  title: string
  message: string
  confirmText: string
  cancelText: string
  showCancel: boolean
}

const modalState = reactive<ModalState>({
  visible: false,
  title: '',
  message: '',
  confirmText: '确定',
  cancelText: '取消',
  showCancel: true,
})

let resolveCallback: ((value: boolean) => void) | null = null

function showModal(options: ModalOptions | string): Promise<boolean> {
  const opts: ModalOptions = typeof options === 'string' ? { message: options } : options

  modalState.visible = true
  modalState.title = opts.title || ''
  modalState.message = opts.message
  modalState.confirmText = opts.confirmText || '确定'
  modalState.cancelText = opts.cancelText || '取消'
  modalState.showCancel = opts.showCancel !== false

  return new Promise((resolve) => {
    resolveCallback = resolve
  })
}

function handleConfirm() {
  modalState.visible = false
  if (resolveCallback) {
    resolveCallback(true)
    resolveCallback = null
  }
}

function handleCancel() {
  modalState.visible = false
  if (resolveCallback) {
    resolveCallback(false)
    resolveCallback = null
  }
}

// 导出 composable
export function useCustomModal() {
  return {
    state: modalState,
    show: showModal,
    confirm: handleConfirm,
    cancel: handleCancel,
  }
}

export default useCustomModal
