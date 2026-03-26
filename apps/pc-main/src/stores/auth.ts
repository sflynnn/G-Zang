import { defineStore } from 'pinia';
import { UserInfo } from '@gzang/shared';
import { httpClient } from '@gzang/shared';

interface AuthState {
  token: string | null;
  refreshToken: string | null;
  user: UserInfo | null;
  companyId: number | null;
  isAuthenticated: boolean;
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
    refreshToken: localStorage.getItem('refreshToken'),
    user: null,
    companyId: null,
    isAuthenticated: !!localStorage.getItem('token')
  }),

  getters: {
    // 获取用户名
    username: (state) => state.user?.username || '',

    // 获取用户昵称
    nickname: (state) => state.user?.nickname || '',

    // 判断是否为企业用户
    isCompanyUser: (state) => !!state.companyId,

    // 获取用户角色ID
    roleId: (state) => state.user?.roleId
  },

  actions: {
    // 设置认证信息
    setAuth(token: string, refreshToken: string, user: UserInfo) {
      this.token = token;
      this.refreshToken = refreshToken;
      this.user = user;
      this.companyId = user.companyId || null;
      this.isAuthenticated = true;

      // 保存到本地存储
      localStorage.setItem('token', token);
      localStorage.setItem('refreshToken', refreshToken);

      // 设置HTTP客户端token
      httpClient.setToken(token, refreshToken);
    },

    // 清除认证信息
    clearAuth() {
      this.token = null;
      this.refreshToken = null;
      this.user = null;
      this.companyId = null;
      this.isAuthenticated = false;

      // 清除本地存储
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');

      // 清除HTTP客户端token
      httpClient.clearAuth();
    },

    // 刷新用户信息
    async refreshUserInfo() {
      if (!this.isAuthenticated) return;

      try {
        const response = await fetch('/api/v1/user/info', {
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        });

        if (response.ok) {
          const result = await response.json();
          if (result.code === 0) {
            this.user = result.data;
            this.companyId = result.data.companyId || null;
          }
        }
      } catch (error) {
        console.error('刷新用户信息失败:', error);
      }
    },

    // 登录
    async login(username: string, password: string) {
      try {
        const response = await fetch('/api/v1/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ username, password })
        });

        const result = await response.json();

        if (result.code === 0) {
          const { token, refreshToken, user } = result.data;
          this.setAuth(token, refreshToken, user);
          return { success: true };
        } else {
          return { success: false, message: result.message };
        }
      } catch (error) {
        console.error('登录失败:', error);
        return { success: false, message: '网络错误，请稍后重试' };
      }
    },

    // 登出
    logout() {
      this.clearAuth();
    }
  }
});
