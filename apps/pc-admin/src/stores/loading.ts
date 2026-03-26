import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

/**
 * Loading Store - 全局加载状态管理
 *
 * 特性：
 * - 使用 requestCount 计数器，支持多个并发请求
 * - 支持全局和局部加载状态
 * - 支持动态加载文本
 * - 支持加载状态过滤和防闪烁
 */
export const useLoadingStore = defineStore('loading', () => {
  // ===== 状态定义 =====

  /** 全局请求计数器 */
  const globalRequestCount = ref(0);

  /** 全局加载开始时间戳，用于防闪烁逻辑 */
  const globalLoadingStartTime = ref<number | null>(null);

  /** 全局加载文本 */
  const globalLoadingText = ref('');

  /** 局部加载状态映射 (key: 唯一标识符, value: { count, startTime, text }) */
  const localLoadingStates = ref<Map<string, {
    count: number;
    startTime: number | null;
    text: string;
  }>>(new Map());

  // ===== 计算属性 =====

  /** 全局是否显示加载状态 (防闪烁: 请求超过200ms才显示) */
  const isGlobalLoading = computed(() => {
    if (globalRequestCount.value === 0) return false;

    const now = Date.now();
    const startTime = globalLoadingStartTime.value;

    if (!startTime) return false;

    // 如果请求持续时间超过200ms，才显示加载状态
    return (now - startTime) >= 200;
  });

  /** 获取局部加载状态 */
  const getLocalLoading = (key: string) => {
    const state = localLoadingStates.value.get(key);
    if (!state) return false;

    if (state.count === 0) return false;

    const now = Date.now();
    const startTime = state.startTime;

    if (!startTime) return false;

    // 局部加载也使用同样的防闪烁逻辑
    return (now - startTime) >= 200;
  };

  /** 获取局部加载文本 */
  const getLocalLoadingText = (key: string) => {
    return localLoadingStates.value.get(key)?.text || '';
  };

  // ===== 全局加载方法 =====

  /**
   * 开始全局加载
   * @param text 可选的加载文本
   */
  const startGlobalLoading = (text = '') => {
    globalRequestCount.value++;

    // 只有在第一次请求时记录开始时间
    if (globalRequestCount.value === 1) {
      globalLoadingStartTime.value = Date.now();
    }

    // 更新加载文本
    if (text) {
      globalLoadingText.value = text;
    }
  };

  /**
   * 停止全局加载
   */
  const stopGlobalLoading = () => {
    if (globalRequestCount.value > 0) {
      globalRequestCount.value--;

      // 当没有活跃请求时，重置状态
      if (globalRequestCount.value === 0) {
        globalLoadingStartTime.value = null;
        globalLoadingText.value = '';
      }
    }
  };

  // ===== 局部加载方法 =====

  /**
   * 开始局部加载
   * @param key 唯一标识符
   * @param text 可选的加载文本
   */
  const startLocalLoading = (key: string, text = '') => {
    const currentState = localLoadingStates.value.get(key) || {
      count: 0,
      startTime: null,
      text: ''
    };

    currentState.count++;

    // 只有在第一次请求时记录开始时间
    if (currentState.count === 1) {
      currentState.startTime = Date.now();
    }

    // 更新加载文本
    if (text) {
      currentState.text = text;
    }

    localLoadingStates.value.set(key, currentState);
  };

  /**
   * 停止局部加载
   * @param key 唯一标识符
   */
  const stopLocalLoading = (key: string) => {
    const currentState = localLoadingStates.value.get(key);

    if (currentState && currentState.count > 0) {
      currentState.count--;

      // 当没有活跃请求时，从映射中删除
      if (currentState.count === 0) {
        localLoadingStates.value.delete(key);
      }
    }
  };

  // ===== 便捷方法 =====

  /**
   * 检查是否应该跳过加载 (基于请求配置)
   * @param config Axios请求配置
   */
  const shouldSkipLoading = (config: any): boolean => {
    // 检查 No-Loading 头部
    return config?.headers?.['No-Loading'] === true ||
           config?.headers?.['no-loading'] === true;
  };

  /**
   * 执行异步操作时自动管理加载状态
   * @param operation 异步操作函数
   * @param text 可选的加载文本
   * @param isGlobal 是否为全局加载
   * @param localKey 局部加载的唯一标识符
   */
  const withLoading = async <T>(
    operation: () => Promise<T>,
    text = '',
    isGlobal = true,
    localKey?: string
  ): Promise<T> => {
    try {
      if (isGlobal) {
        startGlobalLoading(text);
      } else if (localKey) {
        startLocalLoading(localKey, text);
      }

      return await operation();
    } finally {
      if (isGlobal) {
        stopGlobalLoading();
      } else if (localKey) {
        stopLocalLoading(localKey);
      }
    }
  };

  return {
    // 状态
    globalRequestCount,
    globalLoadingStartTime,
    globalLoadingText,
    localLoadingStates,

    // 计算属性
    isGlobalLoading,

    // 方法
    getLocalLoading,
    getLocalLoadingText,
    startGlobalLoading,
    stopGlobalLoading,
    startLocalLoading,
    stopLocalLoading,
    shouldSkipLoading,
    withLoading
  };
});
