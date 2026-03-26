import { Directive, DirectiveBinding } from 'vue';
import { useLoadingStore } from '@/stores/loading';

/**
 * v-loading 指令 - 局部加载状态管理
 *
 * 用法：
 * <div v-loading="isLoading">内容</div>
 * <div v-loading="{ loading: isLoading, text: '加载中...' }">内容</div>
 * <div v-loading:custom-key="{ loading: isLoading, text: '自定义文本' }">内容</div>
 */
export const vLoading: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const loadingStore = useLoadingStore();

    // 生成唯一标识符
    const key = binding.arg || generateUniqueKey(el);

    // 更新加载状态
    updateLoadingState(el, binding, key, loadingStore);
  },

  updated(el: HTMLElement, binding: DirectiveBinding) {
    const loadingStore = useLoadingStore();

    // 生成唯一标识符
    const key = binding.arg || generateUniqueKey(el);

    // 更新加载状态
    updateLoadingState(el, binding, key, loadingStore);
  },

  unmounted(el: HTMLElement, binding: DirectiveBinding) {
    const loadingStore = useLoadingStore();
    const key = binding.arg || generateUniqueKey(el);

    // 清理加载状态
    loadingStore.stopLocalLoading(key);
    removeLoadingOverlay(el);
  }
};

/**
 * 更新加载状态
 */
function updateLoadingState(
  el: HTMLElement,
  binding: DirectiveBinding,
  key: string,
  loadingStore: ReturnType<typeof useLoadingStore>
) {
  // 解析绑定值
  const { loading, text } = parseBindingValue(binding.value);

  // 获取当前加载状态
  const isCurrentlyLoading = loadingStore.getLocalLoading(key);

  if (loading && !isCurrentlyLoading) {
    // 开始加载
    loadingStore.startLocalLoading(key, text || '');
    showLoadingOverlay(el, text || '');
  } else if (!loading && isCurrentlyLoading) {
    // 停止加载
    loadingStore.stopLocalLoading(key);
    removeLoadingOverlay(el);
  } else if (loading && isCurrentlyLoading) {
    // 更新加载文本
    updateLoadingText(el, text || '');
  }
}

/**
 * 解析绑定值
 */
function parseBindingValue(value: any): { loading: boolean; text?: string } {
  if (typeof value === 'boolean') {
    return { loading: value };
  }

  if (typeof value === 'object' && value !== null) {
    return {
      loading: Boolean(value.loading),
      text: value.text
    };
  }

  return { loading: Boolean(value) };
}

/**
 * 显示加载遮罩
 */
function showLoadingOverlay(el: HTMLElement, text: string) {
  // 防止重复添加
  if (el.querySelector('.v-loading-overlay')) {
    return;
  }

  // 创建遮罩层
  const overlay = document.createElement('div');
  overlay.className = 'v-loading-overlay';
  overlay.style.cssText = `
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(4px);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    z-index: 10;
    border-radius: inherit;
  `;

  // 创建加载动画容器
  const spinnerContainer = document.createElement('div');
  spinnerContainer.className = 'v-loading-spinner';
  spinnerContainer.style.cssText = `
    width: 32px;
    height: 32px;
    border: 2px solid #e5e7eb;
    border-top: 2px solid #3b82f6;
    border-radius: 50%;
    animation: v-loading-spin 1s linear infinite;
  `;

  // 创建文本元素
  const textElement = document.createElement('div');
  textElement.className = 'v-loading-text';
  textElement.textContent = text || '加载中...';
  textElement.style.cssText = `
    margin-top: 8px;
    font-size: 12px;
    color: #6b7280;
    font-weight: 500;
  `;

  // 添加到遮罩层
  overlay.appendChild(spinnerContainer);
  if (text) {
    overlay.appendChild(textElement);
  }

  // 确保元素有相对定位
  const originalPosition = getComputedStyle(el).position;
  if (originalPosition === 'static') {
    el.style.position = 'relative';
  }

  // 添加到元素
  el.appendChild(overlay);

  // 添加动画样式
  if (!document.getElementById('v-loading-styles')) {
    const style = document.createElement('style');
    style.id = 'v-loading-styles';
    style.textContent = `
      @keyframes v-loading-spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
      }
    `;
    document.head.appendChild(style);
  }
}

/**
 * 移除加载遮罩
 */
function removeLoadingOverlay(el: HTMLElement) {
  const overlay = el.querySelector('.v-loading-overlay');
  if (overlay) {
    overlay.remove();
  }
}

/**
 * 更新加载文本
 */
function updateLoadingText(el: HTMLElement, text: string) {
  const textElement = el.querySelector('.v-loading-text') as HTMLElement;
  if (textElement) {
    textElement.textContent = text || '加载中...';
  }
}

/**
 * 生成唯一标识符
 */
function generateUniqueKey(el: HTMLElement): string {
  // 使用元素的位置信息生成唯一键
  const rect = el.getBoundingClientRect();
  return `loading-${rect.top}-${rect.left}-${Date.now()}`;
}

/**
 * Composable for programmatic loading control
 */
export function useLocalLoading(key: string) {
  const loadingStore = useLoadingStore();

  return {
    start: (text = '') => loadingStore.startLocalLoading(key, text),
    stop: () => loadingStore.stopLocalLoading(key),
    isLoading: () => loadingStore.getLocalLoading(key),
    text: () => loadingStore.getLocalLoadingText(key),
    withLoading: <T>(operation: () => Promise<T>, text = '') =>
      loadingStore.withLoading(operation, text, false, key)
  };
}
