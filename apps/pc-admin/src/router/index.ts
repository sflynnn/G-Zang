import { RouteRecordRaw } from 'vue-router';

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
          permission: 'USER_MANAGE' // 需要用户管理权限
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
          permission: 'ROLE_MANAGE' // 需要角色管理权限
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
          role: 'SUPER_ADMIN' // 只有超级管理员可以访问
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
          permission: 'COMPANY_MANAGE' // 需要公司管理权限
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
          role: 'SUPER_ADMIN' // 只有超级管理员可以访问
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

export default routes;
