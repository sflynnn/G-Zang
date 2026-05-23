import { RouteRecordRaw } from 'vue-router';
import Dashboard from '@/views/Dashboard.vue';
import BusinessOrderList from '@/views/BusinessOrderList.vue';
import BudgetManagement from '@/views/BudgetManagement.vue';
import CostCenter from '@/views/CostCenter.vue';
import FinancialReport from '@/views/FinancialReport.vue';
import ApprovalCenter from '@/views/ApprovalCenter.vue';

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', name: 'Dashboard', component: Dashboard, meta: { title: '工作台' } },
  { path: '/orders', name: 'BusinessOrderList', component: BusinessOrderList, meta: { title: '业务订单' } },
  { path: '/budget', name: 'BudgetManagement', component: BudgetManagement, meta: { title: '预算管理' } },
  { path: '/cost-center', name: 'CostCenter', component: CostCenter, meta: { title: '成本中心' } },
  { path: '/report', name: 'FinancialReport', component: FinancialReport, meta: { title: '财务报表' } },
  { path: '/approval', name: 'ApprovalCenter', component: ApprovalCenter, meta: { title: '审批中心' } },
];

export default routes;
