/* ================================================
   G-Zang Mobile - Loading State Composable
   统一管理全局 Loading 状态

   三种 Loading 类型：
   1. fullscreen  — 全屏遮罩（首次加载、异步操作）
   2. overlay     — 页面遮罩（局部加载、路由转场）
   3. inline      — 行内指示器（按钮加载态等）

   使用方式：
   import { useLoading } from '@/composables/useLoading'

   const loading = useLoading()
   loading.show('加载中...')
   loading.hide()

   进阶：
   loading.show({ type: 'overlay', text: '保存中' })
   ================================================ */

import { ref, computed } from 'vue'

export type LoadingType = 'fullscreen' | 'overlay' | 'inline'
export type LoadingTheme = 'primary' | 'secondary' | 'light' | 'dark'

export interface LoadingOptions {
  type?: LoadingType
  text?: string
  theme?: LoadingTheme
  overlayClosable?: boolean
  minDuration?: number
}

const MIN_DURATION = 200

const globalLoadingRef = ref(false)
const globalLoadingTypeRef = ref<LoadingType>('fullscreen')
const globalLoadingTextRef = ref('')
const globalLoadingThemeRef = ref<LoadingTheme>('primary')
const globalLoadingClosableRef = ref(false)

let hideTimer: ReturnType<typeof setTimeout> | null = null

export function useLoading() {
  const show = (options: LoadingOptions | string = {}) => {
    if (hideTimer) {
      clearTimeout(hideTimer)
      hideTimer = null
    }

    if (typeof options === 'string') {
      globalLoadingTextRef.value = options
      globalLoadingTypeRef.value = 'fullscreen'
      globalLoadingThemeRef.value = 'primary'
    } else {
      globalLoadingTypeRef.value = options.type ?? 'fullscreen'
      globalLoadingTextRef.value = options.text ?? ''
      globalLoadingThemeRef.value = options.theme ?? 'primary'
      globalLoadingClosableRef.value = options.overlayClosable ?? false
    }

    globalLoadingRef.value = true
  }

  const hide = (delay = 0) => {
    if (delay > 0) {
      hideTimer = setTimeout(() => {
        globalLoadingRef.value = false
      }, delay)
      return
    }

    const elapsed = Date.now()
    const remaining = MIN_DURATION - elapsed
    if (remaining > 0) {
      hideTimer = setTimeout(() => {
        globalLoadingRef.value = false
      }, remaining)
    } else {
      globalLoadingRef.value = false
    }
  }

  const isVisible = computed(() => globalLoadingRef.value)
  const loadingType = computed(() => globalLoadingTypeRef.value)
  const loadingText = computed(() => globalLoadingTextRef.value)
  const loadingTheme = computed(() => globalLoadingThemeRef.value)
  const overlayClosable = computed(() => globalLoadingClosableRef.value)

  return {
    show,
    hide,
    isVisible,
    loadingType,
    loadingText,
    loadingTheme,
    overlayClosable,
  }
}
