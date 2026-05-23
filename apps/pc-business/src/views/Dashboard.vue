<template>
  <div class="dashboard p-4 md:p-6 lg:p-8">
    <!-- 顶部欢迎区 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">{{ $t('dashboard.welcome') }}</h1>
      <p class="text-sm text-text-secondary dark:text-dark-text-secondary mt-1">{{ greeting }}</p>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-text-secondary mb-1">{{ $t('dashboard.totalOrders') }}</p>
            <p class="text-2xl font-bold font-mono text-text-primary dark:text-dark-text-primary">{{ stats.pendingOrders }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-warning/10 flex items-center justify-center">
            <svg class="w-6 h-6 text-warning" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
        </div>
      </div>

      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-text-secondary mb-1">{{ $t('dashboard.pendingApprovals') }}</p>
            <p class="text-2xl font-bold font-mono text-text-primary dark:text-dark-text-primary">{{ stats.pendingApprovals }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-info/10 flex items-center justify-center">
            <svg class="w-6 h-6 text-info" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
      </div>

      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-text-secondary mb-1">{{ $t('dashboard.monthlyBudget') }}</p>
            <p class="text-2xl font-bold font-mono text-primary">{{ formatCurrency(stats.monthlyBudget) }}</p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center">
            <svg class="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
            </svg>
          </div>
        </div>
      </div>

      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-text-secondary mb-1">{{ $t('dashboard.monthlyExpense') }}</p>
            <p class="text-2xl font-bold font-mono" :class="stats.monthlyExpense > stats.monthlyBudget ? 'text-danger' : 'text-success'">
              {{ formatCurrency(stats.monthlyExpense) }}
            </p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-success/10 flex items-center justify-center">
            <svg class="w-6 h-6 text-success" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 13l-5 5m0 0l-5-5m5 5V6" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作和最近订单 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 快捷操作 -->
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-6 shadow-gzang">
        <h3 class="text-lg font-semibold mb-4 text-text-primary dark:text-dark-text-primary">{{ $t('dashboard.quickActions') }}</h3>
        <div class="space-y-3">
          <button v-for="action in quickActions" :key="action.label" @click="handleAction(action)" class="w-full flex items-center gap-3 p-3 rounded-xl hover:bg-gray-50 dark:hover:bg-dark-bg transition-colors">
            <div class="w-10 h-10 rounded-lg flex items-center justify-center" :style="{ backgroundColor: action.color + '15' }">
              <svg class="w-5 h-5" :style="{ color: action.color }" fill="none" stroke="currentColor" viewBox="0 0 24 24" v-html="action.icon" />
            </div>
            <span class="font-medium text-text-primary dark:text-dark-text-primary">{{ action.label }}</span>
          </button>
        </div>
      </div>

      <!-- 最近订单 -->
      <div class="lg:col-span-2 bg-surface dark:bg-dark-surface rounded-2xl p-6 shadow-gzang">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-text-primary dark:text-dark-text-primary">{{ $t('dashboard.recentOrders') }}</h3>
          <button @click="router.push('/orders')" class="text-sm text-secondary hover:text-secondary-dark font-medium">{{ $t('order.viewDetail') }} →</button>
        </div>
        <div v-if="recentOrders.length > 0" class="space-y-3">
          <div v-for="order in recentOrders" :key="order.id" class="flex items-center justify-between p-3 rounded-xl hover:bg-gray-50 dark:hover:bg-dark-bg transition-colors cursor-pointer">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center text-primary font-bold">
                {{ order.orderNo.slice(-4) }}
              </div>
              <div>
                <p class="font-medium text-text-primary dark:text-dark-text-primary">{{ order.customer }}</p>
                <p class="text-xs text-text-secondary">{{ order.createTime }}</p>
              </div>
            </div>
            <div class="text-right">
              <p class="font-mono font-bold text-text-primary dark:text-dark-text-primary">{{ formatCurrency(order.amount) }}</p>
              <span class="text-xs px-2 py-0.5 rounded-full" :class="getStatusClass(order.status)">{{ getStatusName(order.status) }}</span>
            </div>
          </div>
        </div>
        <div v-else class="text-center py-12 text-text-secondary">
          <svg class="w-12 h-12 mx-auto mb-3 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          <p>{{ $t('dashboard.noOrders') }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';

const router = useRouter();
const { t } = useI18n();

const stats = ref({
  pendingOrders: 5,
  pendingApprovals: 3,
  monthlyBudget: 100000,
  monthlyExpense: 76500,
});

const recentOrders = ref<any[]>([
  { id: 1, orderNo: 'BO202605001', customer: '北京科技有限公司', amount: 28000, status: 'pending', createTime: '2026-05-22 10:30' },
  { id: 2, orderNo: 'BO202605002', customer: '上海贸易公司', amount: 15600, status: 'approved', createTime: '2026-05-21 14:20' },
  { id: 3, orderNo: 'BO202605003', customer: '广州实业集团', amount: 42000, status: 'inProgress', createTime: '2026-05-20 09:15' },
]);

const quickActions = [
  { label: '订单管理', icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />', color: '#0F4C5C', route: '/orders' },
  { label: '预算控制', icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />', color: '#FB8B24', route: '/budget' },
  { label: '成本分析', icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />', color: '#06D6A0', route: '/report' },
  { label: '审批中心', icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />', color: '#118AB2', route: '/approval' },
];

const greeting = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '凌晨好';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 18) return '下午好';
  return '晚上好';
});

const formatCurrency = (v: number) => v.toLocaleString('zh-CN', { minimumFractionDigits: 2 });

const getStatusClass = (status: string) => ({
  pending: 'bg-warning/10 text-warning',
  approved: 'bg-success/10 text-success',
  rejected: 'bg-danger/10 text-danger',
  inProgress: 'bg-info/10 text-info',
  completed: 'bg-gray-100 text-text-secondary',
}[status] || 'bg-gray-100 text-text-secondary');

const getStatusName = (status: string) => ({
  pending: '待审批',
  approved: '已通过',
  rejected: '已驳回',
  inProgress: '进行中',
  completed: '已完成',
}[status] || status);

const handleAction = (action: any) => router.push(action.route);
</script>
