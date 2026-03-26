import { BaseEntity, UserStatus } from './common';

// 用户实体
export interface User extends BaseEntity {
  username: string;
  password: string;
  nickname: string;
  avatar?: string;
  roleId: number;
  companyId?: number;
  status: UserStatus;
}

// 角色实体
export interface Role extends BaseEntity {
  roleName: string;
  roleCode: string;
  description?: string;
}

// 权限实体
export interface Permission extends BaseEntity {
  permissionName: string;
  permissionCode: string;
}

// 公司实体
export interface Company extends BaseEntity {
  companyName: string;
  adminUserId: number;
}

// 用户信息响应
export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  roleId: number;
  companyId?: number;
}

// 登录请求
export interface LoginRequest {
  username: string;
  password: string;
}

// 登录响应
export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: UserInfo;
}

// 注册请求
export interface RegisterRequest {
  username: string;
  password: string;
  nickname: string;
}
