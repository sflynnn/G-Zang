import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosRequestConfig } from 'axios';
import { useLoadingStore } from '@/stores/loading';

// 创建axios实例
const api: AxiosInstance = axios.create({
  baseURL: (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 扩展接口：响应拦截器已将 AxiosResponse.data 作为返回值，因此 get/post/put/delete 返回 T 而非 AxiosResponse<T>
interface TypedAxiosInstance extends AxiosInstance {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>;
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>;
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>;
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>;
  patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>;
}

// 断言为带类型安全的实例
const typedApi = api as TypedAxiosInstance;

// 获取loading store实例
const loadingStore = useLoadingStore();

// 请求拦截器
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token (使用admin_token)
    const token = localStorage.getItem('admin_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // 检查是否需要跳过加载状态
    if (!loadingStore.shouldSkipLoading(config)) {
      // 开始全局加载，传递请求URL作为默认文本
      const loadingText = getLoadingTextFromConfig(config);
      loadingStore.startGlobalLoading(loadingText);
    }

    return config;
  },
  (error) => {
    // 请求失败时也要停止加载
    loadingStore.stopGlobalLoading();
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response: AxiosResponse) => {
    // 响应成功，停止加载
    loadingStore.stopGlobalLoading();
    return response.data;
  },
  (error) => {
    // 响应失败，停止加载
    loadingStore.stopGlobalLoading();

    if (error.response?.status === 401) {
      // token过期，跳转到登录页
      localStorage.removeItem('admin_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

/**
 * 从请求配置中提取加载文本
 * @param config Axios请求配置
 */
function getLoadingTextFromConfig(config: InternalAxiosRequestConfig): string {
  // 检查自定义加载文本头部
  const customText = config.headers?.['Loading-Text'] as string;
  if (customText) {
    return customText;
  }

  // 根据请求方法和URL生成默认文本
  const method = config.method?.toUpperCase();
  const url = config.url || '';

  if (method === 'GET') {
    if (url.includes('/list') || url.includes('/search') || url.includes('/query')) {
      return '加载数据中...';
    }
    return '获取数据中...';
  }

  if (method === 'POST') {
    if (url.includes('/login') || url.includes('/auth')) {
      return '登录中...';
    }
    return '提交数据中...';
  }

  if (method === 'PUT' || method === 'PATCH') {
    return '更新数据中...';
  }

  if (method === 'DELETE') {
    return '删除数据中...';
  }

  return '处理中...';
}

export default typedApi;

// 导出所有API模块
export * from './auth';
export * from './user';
export * from './role';
export * from './permission';
export * from './audit-log';
export * from './company';