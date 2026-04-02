import { defineStore } from 'pinia';
import { ref, computed, watch } from 'vue';
import { hasPermission, hasRole, getUserPermissions, getUserRole } from '@/utils/permission';

export interface User {
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
}

export const useAppStore = defineStore('app', () => {
  // 应用状态
  const themeMode = ref<'light' | 'dark' | 'auto'>(
    (localStorage.getItem('admin_theme_mode') as 'light' | 'dark' | 'auto') || 'light'
  );
  const isDark = ref(
    localStorage.getItem('admin_theme_mode') === 'auto'
      ? window.matchMedia('(prefers-color-scheme: dark)').matches
      : localStorage.getItem('admin_theme') === 'dark'
  );
  const loading = ref(false);
  const collapsed = ref(false);
  const language = ref(localStorage.getItem('admin_language') || 'zh-CN');

  // 用户信息
  const user = ref<User | null>(JSON.parse(localStorage.getItem('admin_user') || 'null'));
  const token = ref(localStorage.getItem('admin_token') || '');

  // 监听系统主题变化
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
  const handleSystemThemeChange = (e: MediaQueryListEvent | MediaQueryList) => {
    if (themeMode.value === 'auto') {
      isDark.value = e.matches;
    }
  };
  mediaQuery.addEventListener('change', handleSystemThemeChange);

  // 监听用户信息变化，同步持久化
  watch(user, (newUser) => {
    if (newUser) {
      localStorage.setItem('admin_user', JSON.stringify(newUser));
    } else {
      localStorage.removeItem('admin_user');
    }
  }, { deep: true });

  // 监听主题模式变化，应用到DOM
  watch(isDark, (newDark) => {
    const html = document.documentElement;
    if (newDark) {
      html.classList.add('dark');
    } else {
      html.classList.remove('dark');
    }
    localStorage.setItem('admin_theme', newDark ? 'dark' : 'light');
  }, { immediate: true });

  // 设置主题模式
  const setThemeMode = (mode: 'light' | 'dark' | 'auto') => {
    themeMode.value = mode;
    localStorage.setItem('admin_theme_mode', mode);
    if (mode === 'auto') {
      isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches;
    } else {
      isDark.value = mode === 'dark';
    }
  };

  // 设置暗色主题（兼容旧接口）
  const setDarkTheme = (dark: boolean) => {
    setThemeMode(dark ? 'dark' : 'light');
  };

  // 设置语言
  const setLanguage = (lang: string) => {
    language.value = lang;
    localStorage.setItem('admin_language', lang);
    // 同步更新 i18n locale
    import('@/i18n').then(({ default: i18n }) => {
      i18n.global.locale.value = lang as 'zh-CN' | 'en-US';
    });
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
    localStorage.removeItem('admin_user');
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
    themeMode,
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
    setThemeMode,
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
