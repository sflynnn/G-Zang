import { defineStore } from 'pinia';
import { ref, computed, watch } from 'vue';
import { hasPermission, hasRole, getUserPermissions, getUserRole } from '@/utils/permission';

export interface User {
  id: number;
  username: string;
  nickname: string;
  roleId: number;
  roleCode: string;
  roleName: string;
  permissions: string[];
  companyId?: number;
  companyName?: string;
  avatar?: string;
  status: number;
}

export const useAppStore = defineStore('app', () => {
  // 应用状态
  const isDark = ref(localStorage.getItem('admin_theme') === 'dark' || false);
  const loading = ref(false);
  const collapsed = ref(false);
  const language = ref(localStorage.getItem('admin_language') || 'zh');

  // 用户信息
  const user = ref<User | null>(null);
  const token = ref(localStorage.getItem('admin_token') || '');

  // 监听主题变化，应用到DOM
  watch(isDark, (newDark) => {
    const html = document.documentElement;
    if (newDark) {
      html.classList.add('dark');
    } else {
      html.classList.remove('dark');
    }
    localStorage.setItem('admin_theme', newDark ? 'dark' : 'light');
  }, { immediate: true });

  // 设置暗色主题
  const setDarkTheme = (dark: boolean) => {
    isDark.value = dark;
  };

  // 设置语言
  const setLanguage = (lang: string) => {
    language.value = lang;
    localStorage.setItem('admin_language', lang);
  };

  // 设置加载状态
  const setLoading = (value: boolean) => {
    loading.value = value;
  };

  // 设置侧边栏折叠状态
  const setCollapsed = (value: boolean) => {
    collapsed.value = value;
  };

  // 设置用户信息
  const setUser = (userData: any) => {
    user.value = userData;
  };

  // 设置token
  const setToken = (tokenValue: string) => {
    token.value = tokenValue;
    if (tokenValue) {
      localStorage.setItem('admin_token', tokenValue);
    } else {
      localStorage.removeItem('admin_token');
    }
  };

  // 登出
  const logout = () => {
    user.value = null;
    token.value = '';
    localStorage.removeItem('admin_token');
    // 注意：不清除记住我相关的localStorage，让用户下次登录时保持选择
  };

  // 计算属性
  const isLoggedIn = computed(() => !!token.value);
  const userPermissions = computed(() => getUserPermissions());
  const userRole = computed(() => getUserRole());
  const isSuperAdmin = computed(() => hasRole('SUPER_ADMIN'));
  const isAdmin = computed(() => hasRole('ADMIN'));

  // 权限验证方法
  const checkPermission = (permission: string) => hasPermission(permission);
  const checkRole = (role: string) => hasRole(role);

  return {
    // 状态
    isDark,
    loading,
    collapsed,
    language,
    user,
    token,

    // 计算属性
    isLoggedIn,
    userPermissions,
    userRole,
    isSuperAdmin,
    isAdmin,

    // 方法
    setDarkTheme,
    setLanguage,
    setLoading,
    setCollapsed,
    setUser,
    setToken,
    logout,

    // 权限方法
    checkPermission,
    checkRole
  };
});
