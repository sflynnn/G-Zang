/**
 * 用户相关 API
 * 用户信息、绑定手机、修改密码等
 */
import { api } from './index';

/**
 * 发送绑定手机验证码
 * POST /api/mobile/user/send-bind-code
 */
export async function sendBindCode(phone: string): Promise<void> {
  return api.post('/user/send-bind-code', { phone });
}

/**
 * 绑定手机号
 * POST /api/mobile/user/bind-phone
 */
export async function bindPhone(phone: string, code: string): Promise<void> {
  return api.post('/user/bind-phone', { phone, code });
}

/**
 * 修改密码
 * POST /api/mobile/user/change-password
 */
export async function changePassword(oldPassword: string, newPassword: string): Promise<void> {
  return api.post('/user/change-password', { oldPassword, newPassword });
}

/**
 * 获取用户信息
 * GET /api/mobile/user/info
 */
export async function getUserInfo(): Promise<{
  id: number;
  username: string;
  nickname?: string;
  phone?: string;
  email?: string;
  avatar?: string;
}> {
  return api.get('/user/info');
}

/**
 * 更新用户信息
 * PUT /api/mobile/user/info
 */
export async function updateUserInfo(data: {
  nickname?: string;
  avatar?: string;
}): Promise<void> {
  return api.put('/user/info', data);
}

export const userApi = {
  sendBindCode,
  bindPhone,
  changePassword,
  getUserInfo,
  updateUserInfo,
};

export default userApi;
