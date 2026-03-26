import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { Result, BusinessCode } from '../types';

// HTTP客户端类
export class HttpClient {
  private instance: AxiosInstance;

  constructor(config: { baseURL: string; timeout?: number }) {
    this.instance = axios.create({
      baseURL: config.baseURL,
      timeout: config.timeout || 10000,
      headers: {
        'Content-Type': 'application/json'
      }
    });

    this.setupInterceptors();
  }

  // 设置请求和响应拦截器
  private setupInterceptors() {
    // 请求拦截器：添加JWT Token
    this.instance.interceptors.request.use(
      (config) => {
        const token = this.getToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // 响应拦截器：统一错误处理
    this.instance.interceptors.response.use(
      (response: AxiosResponse<Result>) => {
        const { data } = response;

        // 业务层面的错误
        if (data.code !== BusinessCode.SUCCESS) {
          const error = new Error(data.message || '请求失败');
          (error as any).code = data.code;
          return Promise.reject(error);
        }

        return response;
      },
      (error) => {
        // HTTP层面的错误
        if (error.response) {
          const { status, data } = error.response;
          switch (status) {
            case 401:
              // 未授权，清除token并跳转登录
              this.clearToken();
              window.location.href = '/login';
              break;
            case 403:
              throw new Error('权限不足');
            case 404:
              throw new Error('接口不存在');
            case 500:
              throw new Error('服务器内部错误');
            default:
              throw new Error(data?.message || `请求失败 (${status})`);
          }
        } else if (error.request) {
          throw new Error('网络连接失败，请检查网络设置');
        } else {
          throw error;
        }
      }
    );
  }

  // 获取token
  private getToken(): string | null {
    return localStorage.getItem('token');
  }

  // 清除token
  private clearToken() {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
  }

  // 设置token
  public setToken(token: string, refreshToken?: string) {
    localStorage.setItem('token', token);
    if (refreshToken) {
      localStorage.setItem('refreshToken', refreshToken);
    }
  }

  // 清除认证信息
  public clearAuth() {
    this.clearToken();
  }

  // GET请求
  public get<T = any>(url: string, config?: AxiosRequestConfig): Promise<Result<T>> {
    return this.instance.get(url, config).then(res => res.data);
  }

  // POST请求
  public post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<Result<T>> {
    return this.instance.post(url, data, config).then(res => res.data);
  }

  // PUT请求
  public put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<Result<T>> {
    return this.instance.put(url, data, config).then(res => res.data);
  }

  // DELETE请求
  public delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<Result<T>> {
    return this.instance.delete(url, config).then(res => res.data);
  }
}

// 创建默认实例
const httpClient = new HttpClient({
  baseURL: process.env.VUE_APP_API_BASE_URL || '/api/v1',
  timeout: 10000
});

export default httpClient;
