import httpClient from './http';
import { UserInfo, Result } from '../types';

// 用户相关API
export const userApi = {
  // 获取当前用户信息
  getCurrentUser: (): Promise<Result<UserInfo>> => {
    return httpClient.get('/user/info');
  },

  // 更新用户信息
  updateUser: (data: Partial<UserInfo>): Promise<Result<void>> => {
    return httpClient.put('/user/info', data);
  },

  // 修改密码
  changePassword: (data: { oldPassword: string; newPassword: string }): Promise<Result<void>> => {
    return httpClient.put('/user/password', data);
  }
};
