/* ================================================
   G-Zang Mobile - Page Transition Composable
   统一管理页面跳转动画与 Loading 状态

   使用方式：
   import { usePageTransition } from '@/composables/usePageTransition'

   const { navigateTo, switchTab, showLoading, hideLoading } = usePageTransition()

   // 跳转页面（带渐入动画）
   navigateTo('/pages/bills/detail', { id: 1 })

   // TabBar 切换
   switchTab('/pages/home/index')

   // 显示/隐藏加载中
   showLoading()
   hideLoading()
   ================================================ */

import { useLoading } from './useLoading'

export type TransitionType =
  | 'slide-right'    // iOS 经典：从右滑入（push）
  | 'slide-left'     // 左滑退出（pop）
  | 'fade'           // 淡入淡出（modal/小窗）
  | 'scale-fade'     // 缩放淡入（弹窗类页面）
  | 'slide-up'       // 从底部滑入（底部表单）
  | 'none'           // 无动画

export interface TransitionOptions {
  type?: TransitionType
  duration?: number
  delay?: number
  loading?: boolean
  loadingText?: string
  successCallback?: () => void
  failCallback?: (err: any) => void
}

interface PageRouteInfo {
  pagePath: string
  fullPath: string
}

const TAB_BAR_PAGES = [
  '/pages/home/index',
  '/pages/accounting/index',
  '/pages/bills/index',
  '/pages/analysis/index',
  '/pages/profile/index',
]

function isTabBarPage(path: string): boolean {
  const normalized = path.split('?')[0]
  return TAB_BAR_PAGES.includes(normalized)
}

function getCurrentPagePath(): string {
  if (typeof window !== 'undefined') {
    try {
      const match = window.location.href.match(/#(\/[^?#]+)/)
      if (match) return match[1].split('?')[0]
    } catch { /* H5 only */ }
  }
  try {
    const pages = getCurrentPages()
    if (pages && pages.length > 0) {
      const currentPage = pages[pages.length - 1] as any
      if (currentPage.route) return '/' + currentPage.route
    }
  } catch { /* Mini-program only */ }
  return ''
}

export function usePageTransition() {
  const loading = useLoading()

  const defaultDuration = 300

  const transitionMap: Record<TransitionType, string | undefined> = {
    'slide-right': undefined,
    'slide-left': undefined,
    'fade': undefined,
    'scale-fade': undefined,
    'slide-up': undefined,
    'none': undefined,
  }

  const navigateTo = (
    url: string,
    options: TransitionOptions = {}
  ) => {
    const {
      type = 'slide-right',
      loading: showLoadingFlag = false,
      loadingText,
    } = options

    if (showLoadingFlag) {
      loading.show(loadingText)
    }

    const urlStr = url.startsWith('/') ? url : `/${url}`

    if (type === 'fade') {
      uni.navigateTo({
        url: urlStr,
        animationType: 'fade-in',
        animationDuration: options.duration ?? defaultDuration,
        fail(e: any) {
          if (showLoadingFlag) loading.hide()
          options.failCallback?.(e)
        },
        success() {
          if (showLoadingFlag) {
            setTimeout(() => loading.hide(), options.duration ?? defaultDuration)
          }
          options.successCallback?.()
        },
      })
      return
    }

    if (type === 'scale-fade') {
      uni.navigateTo({
        url: urlStr,
        animationType: 'zoom-fade-out',
        animationDuration: options.duration ?? defaultDuration,
        fail(e: any) {
          if (showLoadingFlag) loading.hide()
          options.failCallback?.(e)
        },
        success() {
          if (showLoadingFlag) {
            setTimeout(() => loading.hide(), options.duration ?? defaultDuration)
          }
          options.successCallback?.()
        },
      })
      return
    }

    if (type === 'slide-up') {
      uni.navigateTo({
        url: urlStr,
        animationType: 'slide-in-bottom',
        animationDuration: options.duration ?? defaultDuration,
        fail(e: any) {
          if (showLoadingFlag) loading.hide()
          options.failCallback?.(e)
        },
        success() {
          if (showLoadingFlag) {
            setTimeout(() => loading.hide(), options.duration ?? defaultDuration)
          }
          options.successCallback?.()
        },
      })
      return
    }

    if (type === 'none') {
      uni.redirectTo({ url: urlStr })
      return
    }

    uni.navigateTo({
      url: urlStr,
      fail(e: any) {
        if (showLoadingFlag) loading.hide()
        options.failCallback?.(e)
      },
      success() {
        if (showLoadingFlag) {
          setTimeout(() => loading.hide(), options.duration ?? defaultDuration)
        }
        options.successCallback?.()
      },
    })
  }

  const switchTab = (url: string) => {
    const urlStr = url.startsWith('/') ? url : `/${url}`
    uni.switchTab({ url: urlStr })
  }

  const reLaunch = (url: string) => {
    const urlStr = url.startsWith('/') ? url : `/${url}`
    uni.reLaunch({ url: urlStr })
  }

  const goBack = (delta = 1) => {
    uni.navigateBack({ delta })
  }

  const showLoading = (text?: string) => {
    loading.show(text)
  }

  const hideLoading = () => {
    loading.hide()
  }

  return {
    navigateTo,
    switchTab,
    reLaunch,
    goBack,
    showLoading,
    hideLoading,
    isTabBarPage,
    getCurrentPagePath,
  }
}
