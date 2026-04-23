import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import { useAppStore } from '@/stores/app';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/dashboard',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表板' }
      }
    ]
  },
  {
    path: '/users',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'UserManagement',
        component: () => import('@/views/UserManagement.vue'),
        meta: {
          title: '用户管理',
          permission: 'USER_MANAGE'
        }
      }
    ]
  },
  {
    path: '/roles',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'RoleManagement',
        component: () => import('@/views/RoleManagement.vue'),
        meta: {
          title: '角色管理',
          permission: 'ROLE_MANAGE'
        }
      }
    ]
  },
  {
    path: '/permissions',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'PermissionManagement',
        component: () => import('@/views/PermissionManagement.vue'),
        meta: {
          title: '权限管理',
          permission: 'ROLE_MANAGE'
        }
      }
    ]
  },
  {
    path: '/companies',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'CompanyManagement',
        component: () => import('@/views/CompanyManagement.vue'),
        meta: {
          title: '公司管理',
          permission: 'COMPANY_MANAGE'
        }
      }
    ]
  },
  {
    path: '/audit-logs',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'AuditLog',
        component: () => import('@/views/AuditLog.vue'),
        meta: {
          title: '审计日志',
          permission: 'SYSTEM_AUDIT'
        }
      }
    ]
  },
  {
    path: '/profile',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: {
          title: '个人中心'
        }
      }
    ]
  },
  {
    path: '/system',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'SystemSettings',
        component: () => import('@/views/SystemSettings.vue'),
        meta: {
          title: '系统设置',
          permission: 'SYSTEM_CONFIG'
        }
      },
      {
        path: 'menu',
        name: 'MenuManagement',
        component: () => import('@/views/MenuManagement.vue'),
        meta: {
          title: '菜单管理',
          permission: 'MENU_MANAGE'
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面未找到' }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, _from, next) => {
  const appStore = useAppStore();
  const token = localStorage.getItem('admin_token');

  // 更新页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 归藏管理后台`;
  }

  // 公开路由
  if (to.path === '/login') {
    if (token) {
      // 已登录，跳转到首页
      next('/dashboard');
    } else {
      next();
    }
    return;
  }

  // 需要认证
  if (!token) {
    next('/login');
    return;
  }

  // 检查权限
  const requiredPermission = to.meta.permission as string | undefined;
  if (requiredPermission && !appStore.checkPermission(requiredPermission)) {
    // 没有权限，跳转到 403
    next('/403');
    return;
  }

  const requiredRole = to.meta.role as string | undefined;
  if (requiredRole && !appStore.checkRole(requiredRole)) {
    next('/403');
    return;
  }

  next();
});

export default router;
