import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import { createPinia } from 'pinia';
import { createDiscreteApi } from 'naive-ui';
import naive from 'naive-ui';
import { useAppStore } from '@/stores/app';

import App from './App.vue';
import routes from './router';
import i18n from './i18n';
import { vLoading } from './directives/loading';
import { vPermission, vRole } from './directives/permission';
import './styles/main.scss';

const app = createApp(App);

// 注册全局指令
app.directive('loading', vLoading);
app.directive('permission', vPermission);
app.directive('role', vRole);

// 路由配置
const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('admin_token');
  const appStore = useAppStore();

  // 如果访问的是登录页面，直接放行
  if (to.path === '/login') {
    next();
    return;
  }

  // 如果没有token且访问的是需要认证的页面，重定向到登录页
  if (!token && to.path !== '/login') {
    next('/login');
    return;
  }

  // 如果有token且访问登录页面，重定向到首页
  if (token && to.path === '/login') {
    next('/dashboard');
    return;
  }

  // 权限验证：检查路由是否需要特定权限
  if (to.meta?.permission && token) {
    const requiredPermission = to.meta.permission as string;
    if (!appStore.checkPermission(requiredPermission)) {
      // 没有权限，跳转到403页面或首页
      console.warn(`无权限访问页面: ${to.path}, 需要权限: ${requiredPermission}`);
      next('/dashboard'); // 或创建403页面
      return;
    }
  }

  // 角色验证：检查路由是否需要特定角色
  if (to.meta?.role && token) {
    const requiredRole = to.meta.role as string;
    if (!appStore.checkRole(requiredRole)) {
      // 没有角色权限，跳转到403页面或首页
      console.warn(`无权限访问页面: ${to.path}, 需要角色: ${requiredRole}`);
      next('/dashboard'); // 或创建403页面
      return;
    }
  }

  next();
});

// 状态管理
const pinia = createPinia();

app.use(naive);
app.use(router);
app.use(pinia);
app.use(i18n);

// 挂载应用
app.mount('#app');

// 创建离散API（在应用挂载后）
const { message, notification, dialog, loadingBar, modal } = createDiscreteApi(
  ['message', 'notification', 'dialog', 'loadingBar', 'modal']
);

// 挂载到window对象上
(window as any).$message = message;
(window as any).$notification = notification;
(window as any).$dialog = dialog;
(window as any).$loadingBar = loadingBar;
(window as any).$modal = modal;
