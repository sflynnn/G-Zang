import { AxiosRequestConfig } from 'axios';
import { useLoadingStore } from '@/stores/loading';
import api from '@/api';

/**
 * HTTP 工具类 - 集成全局加载状态管理
 *
 * 特性：
 * - 自动管理加载状态
 * - 支持请求过滤 (跳过加载)
 * - 支持自定义加载文本
 * - 支持防闪烁逻辑
 */
export class HttpClient {
    private loadingStore = useLoadingStore();

    /**
     * 发送GET请求
     * @param url 请求URL
     * @param config 请求配置
     */
    async get<T = any>(url: string, config?: HttpRequestConfig): Promise<T> {
        return this.request<T>({ ...config, method: 'GET', url });
    }

    /**
     * 发送POST请求
     * @param url 请求URL
     * @param data 请求数据
     * @param config 请求配置
     */
    async post<T = any>(url: string, data?: any, config?: HttpRequestConfig): Promise<T> {
        return this.request<T>({ ...config, method: 'POST', url, data });
    }

    /**
     * 发送PUT请求
     * @param url 请求URL
     * @param data 请求数据
     * @param config 请求配置
     */
    async put<T = any>(url: string, data?: any, config?: HttpRequestConfig): Promise<T> {
        return this.request<T>({ ...config, method: 'PUT', url, data });
    }

    /**
     * 发送DELETE请求
     * @param url 请求URL
     * @param config 请求配置
     */
    async delete<T = any>(url: string, config?: HttpRequestConfig): Promise<T> {
        return this.request<T>({ ...config, method: 'DELETE', url });
    }

    /**
     * 发送PATCH请求
     * @param url 请求URL
     * @param data 请求数据
     * @param config 请求配置
     */
    async patch<T = any>(url: string, data?: any, config?: HttpRequestConfig): Promise<T> {
        return this.request<T>({ ...config, method: 'PATCH', url, data });
    }

    /**
     * 通用请求方法
     * @param config 请求配置
     */
    private async request<T>(config: HttpRequestConfig): Promise<T> {
        // 检查是否需要跳过加载
        const shouldSkipLoading = this.loadingStore.shouldSkipLoading(config);

        try {
            // 如果不跳过加载，开始全局加载
            if (!shouldSkipLoading) {
                const loadingText = config.loadingText || this.generateLoadingText(config);
                this.loadingStore.startGlobalLoading(loadingText);
            }

      // 发送请求
      const response = await api.request(config);

      return response.data;
        } finally {
            // 停止加载
            if (!shouldSkipLoading) {
                this.loadingStore.stopGlobalLoading();
            }
        }
    }

    /**
     * 生成默认加载文本
     * @param config 请求配置
     */
    private generateLoadingText(config: HttpRequestConfig): string {
        const method = config.method?.toUpperCase();
        const url = config.url || '';

        if (method === 'GET') {
            if (url.includes('/list') || url.includes('/search') || url.includes('/query')) {
                return '加载数据中...';
            }
            if (url.includes('/detail') || url.includes('/info')) {
                return '获取详情中...';
            }
            return '获取数据中...';
        }

        if (method === 'POST') {
            if (url.includes('/login') || url.includes('/auth')) {
                return '登录中...';
            }
            if (url.includes('/register') || url.includes('/signup')) {
                return '注册中...';
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

    /**
     * 便捷方法：执行异步操作时自动管理加载状态
     * @param operation 异步操作函数
     * @param loadingText 加载文本
     */
    async withLoading<T>(operation: () => Promise<T>, loadingText = '处理中...'): Promise<T> {
        return this.loadingStore.withLoading(operation, loadingText);
    }

    /**
     * 创建跳过加载的请求配置
     * @param config 原始配置
     */
    static skipLoading(config: AxiosRequestConfig = {}): AxiosRequestConfig {
        return {
            ...config,
            headers: {
                ...config.headers,
                'No-Loading': true
            }
        };
    }

    /**
     * 创建带有自定义加载文本的请求配置
     * @param loadingText 加载文本
     * @param config 原始配置
     */
    static withLoadingText(loadingText: string, config: AxiosRequestConfig = {}): HttpRequestConfig {
        return {
            ...config,
            loadingText
        };
    }
}

/**
 * 扩展的HTTP请求配置接口
 */
export interface HttpRequestConfig extends AxiosRequestConfig {
    /** 自定义加载文本 */
    loadingText?: string;
}

// 创建默认实例
export const httpClient = new HttpClient();

// 导出便捷方法
export default httpClient;
