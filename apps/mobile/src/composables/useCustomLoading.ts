/**
 * Loading 组件
 * 需在每个页面中引入使用
 */
import { loadingManager } from '@/composables/loadingManager'

// 导出 composable，直接使用 loadingManager
export function useCustomLoading() {
  return {
    state: loadingManager.state,
    show: loadingManager.show,
    hide: loadingManager.hide,
  }
}

export default useCustomLoading
