/**
 * G-Zang Mobile Loading Store
 * 全局加载状态管理 - 防止闪烁
 */

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useLoadingStore = defineStore('loading', () => {
  // 状态
  const globalRequestCount = ref(0);
  const globalLoadingStartTime = ref<number | null>(null);
  const loadingText = ref('加载中...');
  const localLoadingStates = ref<Record<string, boolean>>({});

  // 防闪烁延迟 (ms)
  const FLICKER_DELAY = 200;

  // 计算属性
  const isGlobalLoading = computed(() => {
    if (globalRequestCount.value === 0) return false;
    const now = Date.now();
    const startTime = globalLoadingStartTime.value;
    if (!startTime) return false;
    return now - startTime >= FLICKER_DELAY;
  });

  const isLoading = (key: string) => localLoadingStates.value[key] ?? false;

  // 开始全局加载
  const startGlobalLoading = (text?: string) => {
    if (globalRequestCount.value === 0) {
      globalLoadingStartTime.value = Date.now();
      if (text) {
        loadingText.value = text;
      }
    }
    globalRequestCount.value++;
  };

  // 结束全局加载
  const endGlobalLoading = () => {
    if (globalRequestCount.value > 0) {
      globalRequestCount.value--;
      if (globalRequestCount.value === 0) {
        globalLoadingStartTime.value = null;
      }
    }
  };

  // 批量请求管理
  const withLoading = async <T>(
    operation: () => Promise<T>,
    text = '加载中...'
  ): Promise<T> => {
    startGlobalLoading(text);
    try {
      const result = await operation();
      return result;
    } finally {
      endGlobalLoading();
    }
  };

  // 本地加载状态管理
  const setLocalLoading = (key: string, loading: boolean) => {
    localLoadingStates.value[key] = loading;
  };

  const isLocalLoading = (key: string) => localLoadingStates.value[key] ?? false;

  // 判断是否应该跳过加载状态（用于小请求）
  const shouldSkipLoading = (config?: { skipLoading?: boolean; url?: string }): boolean => {
    if (config?.skipLoading) return true;
    // 可以根据 URL 或其他条件判断
    return false;
  };

  // 清除所有状态
  const clearAll = () => {
    globalRequestCount.value = 0;
    globalLoadingStartTime.value = null;
    localLoadingStates.value = {};
  };

  return {
    // 状态
    globalRequestCount,
    loadingText,
    localLoadingStates,

    // 计算属性
    isGlobalLoading,

    // 方法
    startGlobalLoading,
    endGlobalLoading,
    withLoading,
    setLocalLoading,
    isLocalLoading,
    isLoading,
    shouldSkipLoading,
    clearAll,
  };
});
