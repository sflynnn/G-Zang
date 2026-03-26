import { RouteRecordRaw } from 'vue-router';
import Home from '../views/Home.vue';
import Login from '../views/Login.vue';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  // 微前端子应用路由
  {
    path: '/personal/:pathMatch(.*)*',
    name: 'PersonalApp',
    component: () => import('../views/MicroApp.vue'),
    meta: { requiresAuth: true, appName: 'pc-personal' }
  },
  {
    path: '/business/:pathMatch(.*)*',
    name: 'BusinessApp',
    component: () => import('../views/MicroApp.vue'),
    meta: { requiresAuth: true, appName: 'pc-business' }
  },
  {
    path: '/admin/:pathMatch(.*)*',
    name: 'AdminApp',
    component: () => import('../views/MicroApp.vue'),
    meta: { requiresAuth: true, appName: 'pc-admin' }
  }
];

export default routes;
