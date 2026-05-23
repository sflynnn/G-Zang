import { RouteRecordRaw } from 'vue-router';

// 使用 Vue 的 defineAsyncComponent 实现路由懒加载
const Dashboard = () => import('../views/Dashboard.vue');
const TransactionList = () => import('../views/TransactionList.vue');
const TransactionForm = () => import('../views/TransactionForm.vue');
const AccountList = () => import('../views/AccountList.vue');
const CategoryList = () => import('../views/CategoryList.vue');
const Report = () => import('../views/Report.vue');

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { title: '概览' }
  },
  {
    path: '/transactions',
    name: 'TransactionList',
    component: TransactionList,
    meta: { title: '交易记录' }
  },
  {
    path: '/transaction/add',
    name: 'TransactionAdd',
    component: TransactionForm,
    meta: { title: '新增交易' }
  },
  {
    path: '/transaction/edit/:id',
    name: 'TransactionEdit',
    component: TransactionForm,
    meta: { title: '编辑交易' }
  },
  {
    path: '/accounts',
    name: 'AccountList',
    component: AccountList,
    meta: { title: '账户管理' }
  },
  {
    path: '/categories',
    name: 'CategoryList',
    component: CategoryList,
    meta: { title: '分类管理' }
  },
  {
    path: '/report',
    name: 'Report',
    component: Report,
    meta: { title: '财务报表' }
  }
];

export default routes;
