/**
 * Loading Utilities - 加载状态管理工具
 *
 * 导出所有加载相关的工具和方法，方便在组件中使用
 */

export { useLoadingStore } from '@/stores/loading';
export { useLocalLoading } from '@/directives/loading';
export { HttpClient, httpClient } from './http';

// 类型导出
export type { HttpRequestConfig } from './http';

/**
 * 使用示例：
 *
 * // 1. 全局加载 (通过Axios自动管理)
 * import { httpClient } from '@/utils/loading';
 * const data = await httpClient.get('/api/users');
 *
 * // 2. 自定义加载文本
 * import { HttpClient } from '@/utils/loading';
 * const data = await httpClient.withLoadingText('正在加载用户数据...', {
 *   method: 'GET',
 *   url: '/api/users'
 * });
 *
 * // 3. 跳过全局加载
 * import { HttpClient } from '@/utils/loading';
 * const data = await api.get('/api/heartbeat', HttpClient.skipLoading());
 *
 * // 4. 局部加载指令
 * <template>
 *   <div v-loading="isLoading">内容</div>
 *   <div v-loading="{ loading: isLoading, text: '自定义文本' }">内容</div>
 *   <div v-loading:unique-key="{ loading: isLoading, text: '加载中...' }">内容</div>
 * </template>
 *
 * // 5. 编程式局部加载
 * import { useLocalLoading } from '@/utils/loading';
 * const loading = useLocalLoading('chart-section');
 *
 * // 开始加载
 * loading.start('正在加载图表数据...');
 *
 * // 停止加载
 * loading.stop();
 *
 * // 或者使用便捷方法
 * const data = await loading.withLoading(() => fetchChartData());
 */
