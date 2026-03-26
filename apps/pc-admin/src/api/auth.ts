import api from './index';

// 用户登录
export const login = (data: { username: string; password: string }) => {
  return api.post('/api/v1/auth/login', data);
};

// 用户注册
export const register = (data: any) => {
  return api.post('/api/v1/auth/register', data);
};
