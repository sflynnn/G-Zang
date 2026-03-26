import { createI18n } from 'vue-i18n';

// 定义语言包
export const messages = {
  zh: {
    common: {
      dashboard: '仪表板',
      userManagement: '用户管理',
      roleManagement: '角色管理',
      companyManagement: '公司管理',
      permissionManagement: '权限管理',
      systemSettings: '系统设置',
      profile: '个人中心',
      logout: '退出登录',
      toggleSidebar: '切换侧边栏',
      toggleTheme: '切换主题',
      language: '语言',
      chinese: '中文',
      english: 'English',
      adminPanel: '管理端',
      loading: '加载中'
    },
    login: {
      title: '管理员登录',
      subtitle: '请输入您的账户信息进行登录',
      username: '请输入用户名',
      password: '请输入密码',
      rememberMe: '记住我',
      forgotPassword: '忘记密码？',
      loginButton: '登录系统',
      loggingIn: '登录中...',
      validation: {
        usernameRequired: '请输入用户名',
        passwordRequired: '请输入密码',
        passwordMinLength: '密码长度不能少于6位'
      },
      sliderCaptcha: {
        hint: '请拖动滑块验证',
        success: '验证成功'
      },
      copyright: '© 2026 G-Zang 财务管理系统 · 企业级管理平台'
    },
    brand: {
      title: '归 藏',
      subtitle: '智能财务管理系统',
      slogan: '智简·归藏 · 专业管理平台'
    }
  },
  en: {
    common: {
      dashboard: 'Dashboard',
      userManagement: 'User Management',
      roleManagement: 'Role Management',
      companyManagement: 'Company Management',
      permissionManagement: 'Permission Management',
      systemSettings: 'System Settings',
      profile: 'Profile',
      logout: 'Logout',
      toggleSidebar: 'Toggle Sidebar',
      toggleTheme: 'Toggle Theme',
      language: 'Language',
      chinese: '中文',
      english: 'English',
      adminPanel: 'Admin Panel',
      loading: 'Loading'
    },
    login: {
      title: 'Admin Login',
      subtitle: 'Please enter your account information to log in',
      username: 'Please enter username',
      password: 'Please enter password',
      rememberMe: 'Remember me',
      forgotPassword: 'Forgot password?',
      loginButton: 'Login',
      loggingIn: 'Logging in...',
      validation: {
        usernameRequired: 'Please enter username',
        passwordRequired: 'Please enter password',
        passwordMinLength: 'Password must be at least 6 characters'
      },
      sliderCaptcha: {
        hint: 'Please slide to verify',
        success: 'Verification successful'
      },
      copyright: '© 2026 G-Zang Financial Management System · Enterprise Management Platform'
    },
    brand: {
      title: 'G-Zang',
      subtitle: 'Intelligent Financial Management System',
      slogan: 'Smart & Simple · G-Zang · Professional Management Platform'
    }
  }
};

// 创建i18n实例
const i18n = createI18n({
  legacy: false, // 使用Composition API模式
  locale: localStorage.getItem('admin_language') || 'zh', // 默认语言
  fallbackLocale: 'zh', // 回退语言
  messages,
  globalInjection: true, // 全局注入 $t 函数
});

export default i18n;
