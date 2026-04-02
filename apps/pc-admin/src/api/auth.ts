import api from './index';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AdminUserInfo {
  userId: number;
  username: string;
  nickname: string;
  avatar?: string;
  roleId: number;
  roleCode: string;
  roleName: string;
  companyId?: number;
  companyName?: string;
  status: number;
  permissions: string[];
  token?: string;
}

/**
 * 登录响应（含统一 Result 包装）
 * 后端 Result<T> 结构：{ code: 0, message: "登录成功", data: AdminUserInfo }
 */
export interface LoginResponse {
  code: number;
  message: string;
  data: AdminUserInfo;
}

// 用户登录
export const login = (data: LoginRequest): Promise<LoginResponse> => {
  return api.post<LoginResponse>('/api/v1/auth/login', data);
};

// 获取当前用户完整信息（含角色和权限）
export const getCurrentUser = (): Promise<AdminUserInfo> => {
  return api.get<AdminUserInfo>('/api/v1/auth/me');
};

// 用户注册
export const register = (data: LoginRequest): Promise<any> => {
  return api.post('/api/v1/auth/register', data);
};
