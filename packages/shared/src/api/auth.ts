import httpClient from './http';
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  Result
} from '../types';

// 认证相关API
export const authApi = {
  // 用户登录
  login: (data: LoginRequest): Promise<Result<LoginResponse>> => {
    return httpClient.post('/auth/login', data);
  },

  // 用户注册
  register: (data: RegisterRequest): Promise<Result<void>> => {
    return httpClient.post('/auth/register', data);
  },

  // 刷新token
  refreshToken: (refreshToken: string): Promise<Result<{ token: string; refreshToken: string }>> => {
    return httpClient.post('/auth/refresh', { refreshToken });
  },

  // 用户登出
  logout: (): Promise<Result<void>> => {
    return httpClient.post('/auth/logout');
  }
};
