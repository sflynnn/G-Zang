/**
 * 认证 API
 * MobileAuthController - /api/mobile/auth
 * 对应后端: server/gzang-mobile-api/.../MobileAuthController.java
 */

import api from './index';

/** 登录请求参数 */
export interface LoginDTO {
  username: string;
  password: string;
}

/** 登录响应 */
export interface LoginVO {
  token: string;
  expiresIn: number;
  user: UserInfo;
}

/** 用户信息 */
export interface UserInfo {
  id: number;
  username: string;
  nickname?: string;
  avatar?: string;
  companyId?: number;
  status?: number;
}

/**
 * 用户登录
 * POST /api/mobile/auth/login
 */
export function login(data: LoginDTO) {
  return api.post<LoginVO>('/auth/login', data);
}

/**
 * 用户注册
 * POST /api/mobile/auth/register
 */
export function register(data: {
  username: string;
  password: string;
  nickname?: string;
  inviteCode?: string;
}) {
  return api.post<LoginVO>('/auth/register', data);
}

/**
 * 刷新 Token
 * POST /api/mobile/auth/refresh-token
 */
export function refreshToken() {
  return api.post<LoginVO>('/auth/refresh-token');
}

/**
 * 退出登录
 * POST /api/mobile/auth/logout
 */
export function logout() {
  return api.post('/auth/logout');
}

/**
 * 获取当前用户信息
 * GET /api/mobile/auth/current-user
 */
export function getCurrentUser() {
  return api.get<UserInfo>('/auth/current-user');
}

/**
 * 存储 Token 到本地
 */
export function saveToken(token: string, refreshToken?: string) {
  uni.setStorageSync('token', token);
  if (refreshToken) {
    uni.setStorageSync('refreshToken', refreshToken);
  }
}

/**
 * 清除 Token
 */
export function clearToken() {
  uni.removeStorageSync('token');
  uni.removeStorageSync('refreshToken');
  uni.removeStorageSync('userInfo');
}

/**
 * 获取本地 Token
 */
export function getToken(): string {
  return uni.getStorageSync('token') || '';
}

/**
 * 检查登录状态
 */
export function isLoggedIn(): boolean {
  return !!uni.getStorageSync('token');
}

export default {
  login,
  register,
  refreshToken,
  logout,
  getCurrentUser,
  saveToken,
  clearToken,
  getToken,
  isLoggedIn,
};
