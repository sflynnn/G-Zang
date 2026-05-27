/**
 * G-Zang Mobile API Client
 * 归藏财务管理系统 - 移动端 API 客户端
 * BASE_URL: /api/mobile
 */

// API modules
export * from './book';
export * from './account';
export * from './transaction';
export {
  getCategories,
  getCategoriesWithChildren,
  getCategory,
  createCategory,
  updateCategory,
  deleteCategory,
  getSystemCategories,
  initUserCategories,
  type CategoryVO,
  type CategoryWithChildrenVO,
  type CategoryListWithChildrenResponse,
} from './category';
export { getCategories as getSystemCategoriesFromCategory } from './category';
export * from './report';
export * from './voice';
export * from './budget';
export * from './tag';
export * from './paymentMethod';
export {
  getCategoryBudget,
  getBudgetList,
  saveBudget,
  deleteBudget,
  type CategoryBudgetVO as CategoryBudgetVO2,
  type CategoryBudgetItemVO,
} from './categoryBudget';

// 国际化
import { getLocale } from '@/i18n';
import zhCN from '@/locales/zh-CN';
import enUS from '@/locales/en-US';
import { toast } from '@/composables/useToast';
import { modal as globalModal } from '@/composables/useModal';
import { loadingManager } from '@/composables/loadingManager';

const messages = { 'zh-CN': zhCN, 'en-US': enUS };

function t(key: string): string {
  const locale = getLocale();
  return (messages as any)[locale]?.messages?.[key] || (messages['zh-CN'] as any)?.messages?.[key] || key;
}

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

  // 开始 loading（使用计数器避免闪烁）
  if (!options.skipLoading) {
    loadingManager.show(options.loadingText || '加载中...')
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: `${BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data || options.params,
      header,
      success: (res) => {
        if (res.statusCode === 200) {
          const response = res.data as Response<T>;
          if (response.code === 200 || response.code === 0) {
            // 结束 loading
            if (!options.skipLoading) {
              loadingManager.hide()
            }
            resolve(response.data)
          } else {
            // 结束 loading
            if (!options.skipLoading) {
              loadingManager.hide()
            }
            toast.error(response.message || '请求失败');
            reject(new Error(response.message || '请求失败'));
          }
        } else if (res.statusCode === 401) {
          // 结束 loading
          if (!options.skipLoading) {
            loadingManager.hide()
          }
          uni.removeStorageSync('token');
          uni.removeStorageSync('userInfo');

          const response = res.data as Partial<Response<any>>;
          const message = response.message || t('messages.sessionExpired');

          // 使用 modal.show() 正确触发响应式更新
          globalModal.show({
            title: t('common.warning'),
            message: message,
            confirmText: t('messages.goToLogin'),
            cancelText: t('common.cancel'),
            showCancel: true,
          });

          const err: any = new Error(message)
          err.__handled = true
          reject(err)
        } else {
          // 结束 loading
          if (!options.skipLoading) {
            loadingManager.hide()
          }
          toast.error(`请求错误: ${res.statusCode}`);
          reject(new Error(`请求错误: ${res.statusCode}`));
        }
      },
      fail: (err) => {
        // 结束 loading
        if (!options.skipLoading) {
          loadingManager.hide()
        }

        toast.error('网络请求失败');
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

  loadingManager.show('上传中...')

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
        loadingManager.hide()

        if (res.statusCode === 200) {
          const data = JSON.parse(res.data) as Response<T>;
          if (data.code === 200 || data.code === 0) {
            resolve(data.data);
          } else {
            toast.error(data.message || '上传失败');
            reject(new Error(data.message || '上传失败'));
          }
        } else {
          reject(new Error(`上传错误: ${res.statusCode}`));
        }
      },
      fail: (err) => {
        loadingManager.hide()
        toast.error('上传失败');
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
