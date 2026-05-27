/**
 * Loading 状态管理 - 与 CustomLoading 组件配合使用
 * API 层使用此模块管理 Loading 状态
 */
import { reactive } from 'vue'

interface LoadingState {
  visible: boolean
  text: string
}

// 全局状态
const state = reactive<LoadingState>({
  visible: false,
  text: '加载中...',
})

// 请求计数器
let loadingCount = 0
let loadingTimer: ReturnType<typeof setTimeout> | null = null

// 防抖延迟（快速请求不显示 loading）
const DEBOUNCE_DELAY = 200

function showLoading(text = '加载中...') {
  loadingCount++

  // 防抖：如果 loading 已经显示，不再重新显示
  if (loadingTimer) {
    clearTimeout(loadingTimer)
    loadingTimer = null
  }

  // 延迟显示，避免闪烁
  loadingTimer = setTimeout(() => {
    state.text = text
    state.visible = true
  }, DEBOUNCE_DELAY)
}

function hideLoading() {
  loadingCount = Math.max(0, loadingCount - 1)

  // 只有所有请求都完成才隐藏 loading
  if (loadingCount === 0) {
    if (loadingTimer) {
      clearTimeout(loadingTimer)
      loadingTimer = null
    }
    state.visible = false
  }
}

// 强制重置（用于页面切换等场景）
function resetLoading() {
  loadingCount = 0
  if (loadingTimer) {
    clearTimeout(loadingTimer)
    loadingTimer = null
  }
  state.visible = false
}

export const loadingManager = {
  state,
  show: showLoading,
  hide: hideLoading,
  reset: resetLoading,
}

export default loadingManager
