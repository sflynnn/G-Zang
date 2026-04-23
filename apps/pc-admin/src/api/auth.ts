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

// 后端返回的 AdminLoginVO 结构（Result<AdminLoginVO>）
export interface AdminLoginVO {
  token: string;
  expiresIn: number;
  user: {
    userId: number;
    username: string;
    nickname?: string;
    avatar?: string;
    roleId: number;
    roleCode?: string;
    roleName?: string;
    companyId?: number;
    companyName?: string;
    status: number;
    permissions?: string[];
  };
  permissions: string[];
}

// 统一 Result 包装
export interface Result<T> {
  code: number;
  message: string;
  data: T;
}

// 用户登录
export const login = (data: LoginRequest): Promise<Result<AdminLoginVO>> => {
  return api.post<Result<AdminLoginVO>>('/admin/auth/login', data);
};

// 获取当前用户完整信息（含角色和权限）
export const getCurrentUser = (): Promise<Result<any>> => {
  return api.get<Result<any>>('/admin/auth/current-user');
};

// 用户注册
export const register = (data: LoginRequest): Promise<any> => {
  return api.post('/admin/auth/register', data);
};
