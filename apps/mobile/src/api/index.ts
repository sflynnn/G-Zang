/**
 * G-Zang Mobile API Client
 * 归藏财务管理系统 - 移动端 API 客户端
 * BASE_URL: /api/mobile
 */

// API modules
export * from './book';
export * from './account';
export * from './transaction';
export * from './category';
export * from './report';
export * from './voice';

/**
 * API Base Configuration
 */
const BASE_URL = '/api/mobile';

interface RequestOptions {
  url: string;
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  data?: any;
  params?: Record<string, any>;
  header?: Record<string, string>;
  skipLoading?: boolean;
  loadingText?: string;
}

interface Response<T = any> {
  code: number;
  data: T;
  message?: string;
}

/**
 * Unified Request Handler
 */
async function request<T = any>(options: RequestOptions): Promise<T> {
  const token = uni.getStorageSync('token');

  const header: Record<string, string> = {
    'Content-Type': 'application/json',
    ...options.header,
  };

  if (token) {
    header['Authorization'] = `Bearer ${token}`;
  }

  // 开始 loading
  if (!options.skipLoading && typeof uni.showLoading === 'function') {
    uni.showLoading({ title: options.loadingText || '加载中...', mask: true });
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data || options.params,
      header,
      success: (res) => {
        // 结束 loading
        if (!options.skipLoading && typeof uni.hideLoading === 'function') {
          uni.hideLoading();
        }

        if (res.statusCode === 200) {
          const response = res.data as Response<T>;
          if (response.code === 200 || response.code === 0) {
            resolve(response.data);
          } else {
            uni.showToast({
              title: response.message || '请求失败',
              icon: 'none',
            });
            reject(new Error(response.message || '请求失败'));
          }
        } else if (res.statusCode === 401) {
          uni.removeStorageSync('token');
          uni.removeStorageSync('userInfo');
          uni.reLaunch({ url: '/pages/login/index' });
          reject(new Error('未授权，请重新登录'));
        } else {
          uni.showToast({
            title: `请求错误: ${res.statusCode}`,
            icon: 'none',
          });
          reject(new Error(`请求错误: ${res.statusCode}`));
        }
      },
      fail: (err) => {
        // 结束 loading
        if (!options.skipLoading && typeof uni.hideLoading === 'function') {
          uni.hideLoading();
        }

        uni.showToast({
          title: '网络请求失败',
          icon: 'none',
        });
        reject(err);
      },
    });
  });
}

/**
 * GET Request Helper
 */
async function get<T = any>(
  url: string,
  params?: Record<string, any>,
  options?: { skipLoading?: boolean; loadingText?: string }
): Promise<T> {
  return request<T>({ url, method: 'GET', params, ...options });
}

/**
 * POST Request Helper
 */
async function post<T = any>(
  url: string,
  data?: any,
  options?: { skipLoading?: boolean; loadingText?: string }
): Promise<T> {
  return request<T>({ url, method: 'POST', data, ...options });
}

/**
 * PUT Request Helper
 */
async function put<T = any>(
  url: string,
  data?: any,
  options?: { skipLoading?: boolean; loadingText?: string }
): Promise<T> {
  return request<T>({ url, method: 'PUT', data, ...options });
}

/**
 * DELETE Request Helper
 */
async function del<T = any>(
  url: string,
  options?: { skipLoading?: boolean; loadingText?: string }
): Promise<T> {
  return request<T>({ url, method: 'DELETE', ...options });
}

/**
 * Upload File
 */
interface UploadOptions {
  url: string;
  filePath: string;
  name?: string;
  formData?: Record<string, any>;
}

async function upload<T = any>(options: UploadOptions): Promise<T> {
  const token = uni.getStorageSync('token');

  if (typeof uni.showLoading === 'function') {
    uni.showLoading({ title: '上传中...', mask: true });
  }

  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `${BASE_URL}${options.url}`,
      filePath: options.filePath,
      name: options.name || 'file',
      formData: options.formData,
      header: {
        Authorization: token ? `Bearer ${token}` : '',
      },
      success: (res) => {
        if (typeof uni.hideLoading === 'function') {
          uni.hideLoading();
        }

        if (res.statusCode === 200) {
          const data = JSON.parse(res.data) as Response<T>;
          if (data.code === 200 || data.code === 0) {
            resolve(data.data);
          } else {
            uni.showToast({ title: data.message || '上传失败', icon: 'none' });
            reject(new Error(data.message || '上传失败'));
          }
        } else {
          reject(new Error(`上传错误: ${res.statusCode}`));
        }
      },
      fail: (err) => {
        if (typeof uni.hideLoading === 'function') {
          uni.hideLoading();
        }
        uni.showToast({ title: '上传失败', icon: 'none' });
        reject(err);
      },
    });
  });
}

export const api = {
  request,
  get,
  post,
  put,
  delete: del,
  upload,
};

export default api;
