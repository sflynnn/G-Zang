<template>
  <div class="dashboard min-h-screen p-4 md:p-6 lg:p-8">
    <!-- 顶部资产概览卡片 -->
    <div
      class="relative overflow-hidden rounded-2xl bg-gradient-to-br from-primary via-primary to-primary-light p-6 md:p-8 mb-6 animate-fade-in"
    >
      <!-- 装饰背景 -->
      <div class="absolute top-0 right-0 w-64 h-64 bg-white/5 rounded-full -translate-y-1/2 translate-x-1/2"></div>
      <div class="absolute bottom-0 left-0 w-48 h-48 bg-white/5 rounded-full translate-y-1/2 -translate-x-1/2"></div>
      <div class="absolute top-1/2 right-1/4 w-32 h-32 bg-white/5 rounded-full -translate-y-1/2 animate-float"></div>

      <div class="relative z-10">
        <!-- 日期和问候 -->
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-full bg-white/20 flex items-center justify-center backdrop-blur-sm">
              <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
              </svg>
            </div>
            <div class="text-white">
              <p class="text-white/70 text-sm">{{ greeting }}</p>
              <p class="font-semibold">{{ currentDate }}</p>
            </div>
          </div>
          
          <!-- 月份切换 -->
          <div class="flex items-center gap-2 bg-white/10 rounded-full p-1 backdrop-blur-sm">
            <button
              @click="prevMonth"
              class="w-8 h-8 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition-colors"
            >
              <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
              </svg>
            </button>
            <span class="text-white font-medium px-3 min-w-[80px] text-center">{{ currentMonthLabel }}</span>
            <button
              @click="nextMonth"
              class="w-8 h-8 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition-colors"
            >
              <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- 总资产 -->
        <div class="mb-6">
          <div class="flex items-center gap-2 mb-1">
            <span class="text-white/70 text-sm">{{ $t('dashboard.totalAssets') }}</span>
            <button 
              @click="showAssets = !showAssets"
              class="text-white/70 hover:text-white transition-colors"
            >
              <svg v-if="showAssets" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
              </svg>
              <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>
              </svg>
            </button>
          </div>
          <div class="flex items-baseline gap-2">
            <span class="text-4xl md:text-5xl font-bold text-white font-mono tracking-tight">
              {{ showAssets ? formatCurrency(totalBalance) : '****' }}
            </span>
            <span class="text-white/60 text-sm">CNY</span>
          </div>
        </div>

        <!-- 收支概览 -->
        <div class="grid grid-cols-2 gap-4">
          <div class="flex items-center gap-3 bg-white/10 rounded-2xl p-4 backdrop-blur-sm">
            <div class="w-12 h-12 rounded-xl bg-success/30 flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 11l5-5m0 0l5 5m-5-5v12"/>
              </svg>
            </div>
            <div class="flex-1">
              <p class="text-white/70 text-xs mb-0.5">{{ $t('dashboard.monthIncome') }}</p>
              <p class="text-white font-bold font-mono text-xl">
                {{ showAssets ? '+' + formatCurrency(monthlyIncome) : '****' }}
              </p>
            </div>
          </div>
          <div class="flex items-center gap-3 bg-white/10 rounded-2xl p-4 backdrop-blur-sm">
            <div class="w-12 h-12 rounded-xl bg-danger/30 flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 13l-5 5m0 0l-5-5m5 5V6"/>
              </svg>
            </div>
            <div class="flex-1">
              <p class="text-white/70 text-xs mb-0.5">{{ $t('dashboard.monthExpense') }}</p>
              <p class="text-white font-bold font-mono text-xl">
                {{ showAssets ? '-' + formatCurrency(monthlyExpense) : '****' }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 收支趋势图表 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
      <!-- 趋势图 -->
      <div 
        class="lg:col-span-2 rounded-2xl p-6 animate-slide-up transition-colors duration-300 bg-surface dark:bg-dark-surface shadow-gzang dark:shadow-lg"
      >
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-3">
            <h3 class="text-lg font-semibold text-text-primary dark:text-dark-text-primary">{{ $t('dashboard.trend') }}</h3>
            <div class="flex items-center gap-4 text-sm">
              <div class="flex items-center gap-1.5">
                <span class="w-3 h-3 rounded-full bg-success"></span>
                <span class="text-text-secondary dark:text-dark-text-secondary">{{ $t('dashboard.income') }}</span>
              </div>
              <div class="flex items-center gap-1.5">
                <span class="w-3 h-3 rounded-full bg-danger"></span>
                <span class="text-text-secondary dark:text-dark-text-secondary">{{ $t('dashboard.expense') }}</span>
              </div>
            </div>
          </div>
          <div class="flex gap-1.5 bg-opacity-50 p-1 rounded-lg bg-gray-100 dark:bg-dark-bg">
            <button
              v-for="period in (['week', 'month', 'year'] as const)"
              :key="period"
              @click="selectedPeriod = period"
              class="px-3 py-1.5 text-sm rounded-md transition-all duration-200 font-medium"
              :class="selectedPeriod === period
                ? 'bg-secondary text-white shadow-sm'
                : 'text-text-secondary dark:text-dark-text-secondary hover:text-text-primary dark:hover:text-dark-text-primary'"
            >
              {{ periodLabels[period] }}
            </button>
          </div>
        </div>
        <div ref="trendChartRef" class="w-full h-64"></div>
      </div>

      <!-- 预算进度 -->
      <div 
        class="rounded-2xl p-6 animate-slide-up transition-colors duration-300 bg-surface dark:bg-dark-surface shadow-gzang dark:shadow-lg"
        style="animation-delay: 100ms"
      >
        <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-text-primary dark:text-dark-text-primary">{{ $t('dashboard.budget') }}</h3>
          <span 
            class="text-xs px-2.5 py-1 rounded-full font-medium"
            :class="budgetUsedPercent > 90 ? 'bg-danger/10 text-danger' : budgetUsedPercent > 70 ? 'bg-warning/10 text-warning' : 'bg-success/10 text-success'"
          >
            {{ budgetUsedPercent }}%
          </span>
        </div>

        <div class="space-y-5">
          <!-- 总预算 -->
          <div>
            <div class="flex justify-between items-center mb-2">
              <span class="text-sm text-text-secondary dark:text-dark-text-secondary">{{ $t('dashboard.budgetRemaining') }}</span>
              <span class="font-mono text-sm font-medium text-text-primary dark:text-dark-text-primary">
                {{ formatCurrency(monthlyExpense) }} / {{ formatCurrency(monthlyBudget) }}
              </span>
            </div>
            <div class="h-3 rounded-full overflow-hidden bg-gray-100 dark:bg-dark-bg">
              <div
                class="h-full rounded-full transition-all duration-500 ease-out"
                :class="budgetUsedPercent > 90 ? 'bg-danger' : budgetUsedPercent > 70 ? 'bg-warning' : 'bg-success'"
                :style="{ width: `${Math.min(budgetUsedPercent, 100)}%` }"
              ></div>
            </div>
          </div>

          <!-- 分类预算 -->
          <div class="pt-2 border-t border-border dark:border-dark-border">
            <div class="text-xs font-medium mb-3 text-text-secondary dark:text-dark-text-secondary">{{ $t('dashboard.category') }}</div>
            <div v-for="cat in budgetByCategory" :key="cat.name" class="space-y-1.5 mb-4 last:mb-0">
              <div class="flex justify-between items-center">
                <div class="flex items-center gap-2">
                  <span 
                    class="w-2 h-2 rounded-full"
                    :class="cat.percent > 90 ? 'bg-danger' : cat.percent > 70 ? 'bg-warning' : 'bg-primary'"
                  ></span>
                  <span class="text-sm text-text-secondary dark:text-dark-text-secondary">{{ cat.name }}</span>
                </div>
                <span 
                  class="font-mono text-xs font-medium" 
                  :class="cat.percent > 100 ? 'text-danger' : 'text-text-primary dark:text-dark-text-primary'"
                >
                  {{ cat.percent }}%
                </span>
              </div>
              <div class="h-1.5 rounded-full overflow-hidden ml-4 bg-gray-100 dark:bg-dark-bg">
                <div
                  class="h-full rounded-full transition-all duration-500 ease-out"
                  :class="cat.percent > 90 ? 'bg-danger' : cat.percent > 70 ? 'bg-warning' : 'bg-primary'"
                  :style="{ width: `${Math.min(cat.percent, 100)}%` }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作和最近交易 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 快捷操作 -->
      <div 
        class="rounded-2xl p-6 animate-slide-up transition-colors duration-300 bg-surface dark:bg-dark-surface shadow-gzang dark:shadow-lg"
        style="animation-delay: 200ms"
      >
        <h3 class="text-lg font-semibold mb-4 text-text-primary dark:text-dark-text-primary">{{ $t('dashboard.quickActions') }}</h3>
        <div class="grid grid-cols-2 gap-3">
          <button
            v-for="action in quickActions"
            :key="action.label"
            @click="handleAction(action.action)"
            class="quick-action group flex flex-col items-center justify-center gap-2 p-4 rounded-xl transition-all duration-200 bg-background dark:bg-dark-bg hover:bg-gray-50 dark:hover:bg-dark-surface-elevated"
          >
            <div
              class="w-12 h-12 rounded-xl flex items-center justify-center transition-transform group-hover:scale-110"
              :class="action.bgClass"
            >
              <component :is="action.icon" class="w-6 h-6" :class="action.iconClass" />
            </div>
            <span class="text-sm font-medium text-text-primary dark:text-dark-text-primary">{{ action.label }}</span>
          </button>
        </div>
      </div>

      <!-- 最近交易 -->
      <div 
        class="lg:col-span-2 rounded-2xl p-6 animate-slide-up transition-colors duration-300 bg-surface dark:bg-dark-surface shadow-gzang dark:shadow-lg"
        style="animation-delay: 300ms"
      >
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-text-primary dark:text-dark-text-primary">{{ $t('dashboard.recentTransactions') }}</h3>
          <button
            @click="goToTransactions"
            class="text-sm text-secondary hover:text-secondary-dark transition-colors font-medium flex items-center gap-1"
          >
            {{ $t('dashboard.report') }}
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </button>
        </div>

        <div v-if="recentTransactions.length > 0" class="space-y-2">
          <div
            v-for="(tx, index) in recentTransactions.slice(0, 6)"
            :key="tx.id || index"
            class="flex items-center gap-4 p-3 rounded-xl transition-colors cursor-pointer group hover:bg-background dark:hover:bg-dark-bg"
          >
            <div
              class="w-11 h-11 rounded-xl flex items-center justify-center shrink-0"
              :class="tx.type === 1 ? 'bg-success/10 dark:bg-success/20' : 'bg-danger/10 dark:bg-danger/20'"
            >
              <svg
                class="w-5 h-5"
                :class="tx.type === 1 ? 'text-success' : 'text-danger'"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  :d="tx.type === 1 ? 'M7 11l5-5m0 0l5 5m-5-5v12' : 'M17 13l-5 5m0 0l-5-5m5 5V6'"
                />
              </svg>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium truncate text-text-primary dark:text-dark-text-primary">
                {{ getCategoryName(tx.categoryId) }}
              </p>
              <p class="text-xs text-text-secondary dark:text-dark-text-secondary">
                {{ getAccountName(tx.accountId) }} · {{ formatDate(tx.transactionTime) }}
              </p>
            </div>
            <div class="text-right shrink-0">
              <p
                class="font-mono font-semibold"
                :class="tx.type === 1 ? 'text-success' : 'text-danger'"
              >
                {{ tx.type === 1 ? '+' : '-' }}{{ formatCurrency(tx.amount) }}
              </p>
              <p v-if="tx.remark" class="text-xs truncate max-w-24 text-text-secondary dark:text-dark-text-secondary">
                {{ tx.remark }}
              </p>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="text-center py-12">
          <div 
            class="w-16 h-16 mx-auto mb-4 rounded-2xl flex items-center justify-center bg-gray-100 dark:bg-dark-bg"
          >
            <svg class="w-8 h-8 text-text-secondary dark:text-dark-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
            </svg>
          </div>
          <p class="mb-4 text-text-secondary dark:text-dark-text-secondary">{{ $t('dashboard.noData') }}</p>
          <button
            @click="goToAddTransaction"
            class="btn-primary"
          >
            <svg class="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
            </svg>
            {{ $t('dashboard.addTransaction') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, h, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import * as echarts from 'echarts';
import { TransactionType } from '@gzang/shared';
import { accountingApi } from '@gzang/shared';
import { usePersonalStore } from '../stores/personal';
import { useAppStore } from '../stores/app';

const { t, locale } = useI18n();

type ECharts = echarts.ECharts;

// 路由
const router = useRouter();
const personalStore = usePersonalStore();
const appStore = useAppStore();

// 主题状态
const isDark = computed(() => appStore.isDark);

// 加载状态
const loading = ref(false);

// 资产显示/隐藏
const showAssets = ref(true);

// 图表引用
const trendChartRef = ref<HTMLElement | null>(null);
let trendChart: ECharts | null = null;

// 月份选择
const currentMonth = ref(new Date());

const currentMonthLabel = computed(() => {
  return currentMonth.value.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long' });
});

const prevMonth = () => {
  currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() - 1);
};

const nextMonth = () => {
  const now = new Date();
  const next = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() + 1);
  if (next <= now) {
    currentMonth.value = next;
  }
};

// 时间选择
const selectedPeriod = ref<'week' | 'month' | 'year'>('month');
const periodLabels = computed(() => ({
  week: t('dashboard.thisWeek'),
  month: t('dashboard.thisMonth'),
  year: t('dashboard.thisYear')
}));

// 预算配置
const monthlyBudget = 5000;

const budgetByCategory = computed(() => [
  { name: '餐饮', percent: 65, budget: 1500, spent: 975 },
  { name: '交通', percent: 80, budget: 500, spent: 400 },
  { name: '购物', percent: 45, budget: 1000, spent: 450 },
  { name: '娱乐', percent: 120, budget: 500, spent: 600 }
]);

// 快捷操作
const quickActions = computed(() => [
  {
    label: t('dashboard.addTransaction'),
    action: 'add',
    icon: PlusIcon,
    bgClass: 'bg-secondary',
    iconClass: 'text-white'
  },
  {
    label: t('dashboard.transfer'),
    action: 'transfer',
    icon: ArrowRightLeftIcon,
    bgClass: 'bg-primary',
    iconClass: 'text-white'
  },
  {
    label: t('dashboard.report'),
    action: 'report',
    icon: ChartBarIcon,
    bgClass: 'bg-info',
    iconClass: 'text-white'
  },
  {
    label: t('dashboard.account'),
    action: 'accounts',
    icon: WalletIcon,
    bgClass: 'bg-success',
    iconClass: 'text-white'
  }
]);

// 计算属性
const greeting = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return t('dashboard.night');
  if (hour < 9) return t('dashboard.morning');
  if (hour < 12) return t('dashboard.am');
  if (hour < 14) return t('dashboard.noon');
  if (hour < 18) return t('dashboard.pm');
  if (hour < 22) return t('dashboard.evening');
  return t('dashboard.lateNight');
});

const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  });
});

const monthlyIncome = computed(() => {
  const now = currentMonth.value;
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
  return personalStore.transactions
    .filter(t => t.type === TransactionType.INCOME && new Date(t.transactionTime) >= startOfMonth)
    .reduce((sum, t) => sum + t.amount, 0);
});

const monthlyExpense = computed(() => {
  const now = currentMonth.value;
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
  return personalStore.transactions
    .filter(t => t.type === TransactionType.EXPENSE && new Date(t.transactionTime) >= startOfMonth)
    .reduce((sum, t) => sum + t.amount, 0);
});

const totalBalance = computed(() => {
  return personalStore.accounts.reduce((sum, account) => sum + account.balance, 0);
});

const budgetUsedPercent = computed(() => {
  return Math.round((monthlyExpense.value / monthlyBudget) * 100);
});

const recentTransactions = computed(() => {
  return personalStore.transactions.slice(0, 10);
});

// 图标组件
function PlusIcon() {
  return h('svg', { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
    h('path', {
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round',
      'stroke-width': '2',
      d: 'M12 4v16m8-8H4'
    })
  ]);
}

function ArrowRightLeftIcon() {
  return h('svg', { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
    h('path', {
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round',
      'stroke-width': '2',
      d: 'M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4'
    })
  ]);
}

function ChartBarIcon() {
  return h('svg', { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
    h('path', {
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round',
      'stroke-width': '2',
      d: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z'
    })
  ]);
}

function WalletIcon() {
  return h('svg', { fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
    h('path', {
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round',
      'stroke-width': '2',
      d: 'M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z'
    })
  ]);
}

// 辅助函数
const formatCurrency = (amount: number) => {
  return amount.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  const now = new Date();
  const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));

  if (diffDays === 0) return t('dashboard.today');
  if (diffDays === 1) return t('dashboard.yesterday');
  if (diffDays < 7) return `${diffDays}${t('dashboard.daysAgo')}`;
  return date.toLocaleDateString(locale.value === 'en' ? 'en-US' : 'zh-CN', { month: 'short', day: 'numeric' });
};

const getCategoryName = (categoryId: number) => {
  const category = personalStore.categories.find(c => c.id === categoryId);
  return category?.categoryName || '未分类';
};

const getAccountName = (accountId: number) => {
  const account = personalStore.accounts.find(a => a.id === accountId);
  return account?.accountName || '未知账户';
};

// 初始化图表
const initTrendChart = () => {
  if (!trendChartRef.value) return;

  trendChart = echarts.init(trendChartRef.value);
  updateChartTheme();
};

// 更新图表主题
const updateChartTheme = () => {
  if (!trendChart) return;

  const textColor = isDark.value ? '#9CA3AF' : '#6B7280';
  const borderColor = isDark.value ? '#374151' : '#E5E7EB';
  const gridColor = isDark.value ? '#374151' : '#E5E7EB';

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: isDark.value ? '#1F2937' : '#FFFFFF',
      borderColor: borderColor,
      borderWidth: 1,
      textStyle: {
        color: isDark.value ? '#F9FAFB' : '#1F2937'
      },
      formatter: (params: any) => {
        const date = params[0].axisValue;
        let html = `<div class="font-medium">${date}</div>`;
        params.forEach((p: any) => {
          const color = p.seriesName === '收入' ? '#06D6A0' : '#EF476F';
          html += `<div style="display:flex;align-items:center;gap:8px;margin-top:4px;">
            <span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:${color}"></span>
            <span>${p.seriesName}:</span>
            <span class="font-mono" style="color:${color}">¥${p.value.toLocaleString()}</span>
          </div>`;
        });
        return html;
      }
    },
    legend: {
      data: ['收入', '支出'],
      bottom: 0,
      icon: 'circle',
      itemWidth: 8,
      itemHeight: 8,
      textStyle: {
        color: textColor
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['1日', '5日', '10日', '15日', '20日', '25日', '30日'],
      axisLine: {
        lineStyle: {
          color: gridColor
        }
      },
      axisLabel: {
        color: textColor
      }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          color: gridColor,
          opacity: 0.5
        }
      },
      axisLabel: {
        color: textColor,
        formatter: (value: number) => {
          if (value >= 10000) return (value / 10000) + 'w';
          if (value >= 1000) return (value / 1000) + 'k';
          return value.toString();
        }
      }
    },
    series: [
      {
        name: '收入',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: [3200, 4500, 2800, 5100, 3900, 6200, 4800],
        lineStyle: {
          color: '#06D6A0',
          width: 3
        },
        itemStyle: {
          color: '#06D6A0'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: isDark.value ? 'rgba(6, 214, 160, 0.4)' : 'rgba(6, 214, 160, 0.3)' },
            { offset: 1, color: isDark.value ? 'rgba(6, 214, 160, 0.05)' : 'rgba(6, 214, 160, 0.05)' }
          ])
        }
      },
      {
        name: '支出',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: [2100, 3200, 4500, 2800, 5100, 3900, 4200],
        lineStyle: {
          color: '#EF476F',
          width: 3
        },
        itemStyle: {
          color: '#EF476F'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: isDark.value ? 'rgba(239, 71, 111, 0.4)' : 'rgba(239, 71, 111, 0.3)' },
            { offset: 1, color: isDark.value ? 'rgba(239, 71, 111, 0.05)' : 'rgba(239, 71, 111, 0.05)' }
          ])
        }
      }
    ]
  };

  trendChart.setOption(option);
};

// 监听主题变化
watch(isDark, () => {
  updateChartTheme();
});

// 窗口大小变化时重绘图表
const handleResize = () => {
  trendChart?.resize();
};

// 页面方法
const handleAction = (action: string) => {
  switch (action) {
    case 'add':
      goToAddTransaction();
      break;
    case 'transfer':
      router.push('/transfer');
      break;
    case 'report':
      goToReport();
      break;
    case 'accounts':
      router.push('/accounts');
      break;
  }
};

const goToAddTransaction = () => {
  router.push('/transaction/add');
};

const goToTransactions = () => {
  router.push('/transactions');
};

const goToReport = () => {
  router.push('/report');
};

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    const [transactionsRes, accountsRes, categoriesRes] = await Promise.all([
      accountingApi.getTransactions({ page: 1, size: 50 }),
      accountingApi.getAccounts(),
      accountingApi.getCategories()
    ]);

    if (transactionsRes.data) {
      personalStore.setTransactions(transactionsRes.data.records);
    }

    if (accountsRes.data) {
      personalStore.setAccounts(accountsRes.data);
    }

    if (categoriesRes.data) {
      personalStore.setCategories(categoriesRes.data);
    }
  } catch (error) {
    console.error('加载数据失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadData();
  initTrendChart();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  trendChart?.dispose();
});
</script>

<style scoped>
/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(-50%);
  }
  50% {
    transform: translateY(-60%);
  }
}

.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-slide-up {
  animation: slideUp 0.5s ease-out both;
}

.animate-float {
  animation: float 3s ease-in-out infinite;
}
</style>
