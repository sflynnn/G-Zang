/**
 * 支付方式相关 API
 * MobilePaymentMethodController - /api/mobile/payment-methods
 */
import { api } from './index';

export interface PaymentMethodVO {
  id: number;
  methodCode: string;
  methodName: string;
  icon: string;
  color: string;
  sortOrder: number;
  isEnabled: number;
}

/**
 * 获取用户支付方式列表
 * GET /api/mobile/payment-methods
 */
export async function getPaymentMethods(): Promise<PaymentMethodVO[]> {
  return api.get('/payment-methods');
}

/**
 * 创建支付方式
 * POST /api/mobile/payment-methods
 */
export async function createPaymentMethod(data: {
  methodCode: string;
  methodName: string;
  icon?: string;
  color?: string;
  sortOrder?: number;
}): Promise<PaymentMethodVO> {
  return api.post('/payment-methods', data);
}

/**
 * 更新支付方式
 * PUT /api/mobile/payment-methods/{id}
 */
export async function updatePaymentMethod(
  id: number,
  data: {
    methodName?: string;
    icon?: string;
    color?: string;
    sortOrder?: number;
    isEnabled?: number;
  }
): Promise<void> {
  return api.put(`/payment-methods/${id}`, data);
}

/**
 * 删除支付方式
 * DELETE /api/mobile/payment-methods/{id}
 */
export async function deletePaymentMethod(id: number): Promise<void> {
  return api.delete(`/payment-methods/${id}`);
}

export const paymentMethodApi = {
  getPaymentMethods,
  createPaymentMethod,
  updatePaymentMethod,
  deletePaymentMethod,
};

export default paymentMethodApi;
