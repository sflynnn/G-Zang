<template>
  <div class="dashboard min-h-screen bg-background dark:bg-background-dark p-4">
    <!-- 页面标题 -->
    <div class="mb-6 animate-fade-in">
      <h1 class="text-2xl font-bold text-text-primary dark:text-text-primary-dark">{{ $t('dashboard.title') }}</h1>
      <p class="text-text-secondary dark:text-text-secondary-dark mt-1">{{ $t('dashboard.subtitle') }}</p>
    </div>

    <!-- 统计卡片网格 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <!-- 总用户数 -->
      <div
        class="bg-surface dark:bg-surface-dark rounded-xl shadow-gzang dark:shadow-lg p-5 animate-slide-up hover:shadow-gzang-md transition-shadow"
        style="animation-delay: 0ms"
      >
        <div class="flex items-start justify-between">
          <div>
            <p class="text-text-secondary dark:text-text-secondary-dark text-sm mb-1">{{ $t('dashboard.totalUsers') }}</p>
            <p class="text-3xl font-bold text-text-primary dark:text-text-primary-dark font-mono tabular-nums">
              {{ stats.users.toLocaleString() }}
            </p>
            <p class="text-success text-sm mt-2 flex items-center gap-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"/>
              </svg>
              +12.5%
            </p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center">
            <svg class="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 总公司数 -->
      <div
        class="bg-surface dark:bg-surface-dark rounded-xl shadow-gzang dark:shadow-lg p-5 animate-slide-up hover:shadow-gzang-md transition-shadow"
        style="animation-delay: 100ms"
      >
        <div class="flex items-start justify-between">
          <div>
            <p class="text-text-secondary dark:text-text-secondary-dark text-sm mb-1">{{ $t('dashboard.totalCompanies') }}</p>
            <p class="text-3xl font-bold text-text-primary dark:text-text-primary-dark font-mono tabular-nums">
              {{ stats.companies }}
            </p>
            <p class="text-success text-sm mt-2 flex items-center gap-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"/>
              </svg>
              +5.2%
            </p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-secondary/10 flex items-center justify-center">
            <svg class="w-6 h-6 text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 总余额 -->
      <div
        class="bg-surface dark:bg-surface-dark rounded-xl shadow-gzang dark:shadow-lg p-5 animate-slide-up hover:shadow-gzang-md transition-shadow"
        style="animation-delay: 200ms"
      >
        <div class="flex items-start justify-between">
          <div>
            <p class="text-text-secondary dark:text-text-secondary-dark text-sm mb-1">{{ $t('dashboard.totalBalance') }}</p>
            <p class="text-3xl font-bold text-text-primary dark:text-text-primary-dark font-mono tabular-nums">
              {{ formatCurrency(stats.totalBalance) }}
            </p>
            <p class="text-success text-sm mt-2 flex items-center gap-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"/>
              </svg>
              +8.3%
            </p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-success/10 flex items-center justify-center">
            <svg class="w-6 h-6 text-success" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 总交易数 -->
      <div
        class="bg-surface dark:bg-surface-dark rounded-xl shadow-gzang dark:shadow-lg p-5 animate-slide-up hover:shadow-gzang-md transition-shadow"
        style="animation-delay: 300ms"
      >
        <div class="flex items-start justify-between">
          <div>
            <p class="text-text-secondary dark:text-text-secondary-dark text-sm mb-1">{{ $t('dashboard.totalTransactions') }}</p>
            <p class="text-3xl font-bold text-text-primary dark:text-text-primary-dark font-mono tabular-nums">
              {{ stats.transactions.toLocaleString() }}
            </p>
            <p class="text-danger text-sm mt-2 flex items-center gap-1">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3"/>
              </svg>
              -3.1%
            </p>
          </div>
          <div class="w-12 h-12 rounded-xl bg-danger/10 flex items-center justify-center">
            <svg class="w-6 h-6 text-danger" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
      <!-- 月度收支趋势图 -->
      <div
        class="lg:col-span-2 bg-surface dark:bg-surface-dark rounded-xl shadow-gzang dark:shadow-lg p-6 animate-slide-up"
        style="animation-delay: 400ms"
      >
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="text-lg font-semibold text-text-primary dark:text-text-primary-dark">{{ $t('dashboard.monthlyTrend') }}</h3>
            <p class="text-text-secondary dark:text-text-secondary-dark text-sm mt-1">{{ $t('dashboard.trendSubtitle') }}</p>
          </div>
          <div class="flex gap-2">
            <button
              v-for="period in ['week', 'month', 'year'] as const"
              :key="period"
              @click="selectedPeriod = period"
              :class="[
                'px-3 py-1.5 text-sm rounded-lg transition-colors',
                selectedPeriod === period
                  ? 'bg-primary text-white'
                  : 'bg-gray-100 dark:bg-gray-700 text-text-secondary dark:text-text-secondary-dark hover:bg-gray-200 dark:hover:bg-gray-600'
              ]"
            >
              {{ $t(`dashboard.${period === 'week' ? 'thisWeek' : period === 'month' ? 'thisMonth' : 'thisYear'}`) }}
            </button>
          </div>
        </div>
        <div ref="trendChartRef" class="w-full h-72"></div>
      </div>

      <!-- 分类统计饼图 -->
      <div
        class="bg-surface dark:bg-surface-dark rounded-xl shadow-gzang dark:shadow-lg p-6 animate-slide-up"
        style="animation-delay: 500ms"
      >
        <div class="mb-6">
          <h3 class="text-lg font-semibold text-text-primary dark:text-text-primary-dark">{{ $t('dashboard.categoryDistribution') }}</h3>
          <p class="text-text-secondary dark:text-text-secondary-dark text-sm mt-1">{{ $t('dashboard.categorySubtitle') }}</p>
        </div>
        <div ref="pieChartRef" class="w-full h-52 mb-4"></div>
        <!-- 图例 -->
        <div class="space-y-3">
          <div
            v-for="item in categoryData"
            :key="item.key"
            class="flex items-center justify-between"
          >
            <div class="flex items-center gap-2">
              <span
                class="w-3 h-3 rounded-full"
                :style="{ backgroundColor: item.color }"
              ></span>
              <span class="text-sm text-text-secondary dark:text-text-secondary-dark">{{ $t(`dashboard.categories.${item.key}`) }}</span>
            </div>
            <span class="font-mono text-sm font-medium text-text-primary dark:text-text-primary-dark">
              {{ item.value }}%
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部区域：活动时间线和快捷统计 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 最近活动时间线 -->
      <div
        class="lg:col-span-2 bg-surface dark:bg-surface-dark rounded-xl shadow-gzang dark:shadow-lg p-6 animate-slide-up"
        style="animation-delay: 600ms"
      >
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="text-lg font-semibold text-text-primary dark:text-text-primary-dark">{{ $t('dashboard.recentActivities') }}</h3>
            <p class="text-text-secondary dark:text-text-secondary-dark text-sm mt-1">{{ $t('dashboard.activitySubtitle') }}</p>
          </div>
          <button
            @click="router.push('/audit-logs')"
            class="text-sm text-secondary hover:text-secondary-dark transition-colors font-medium"
          >
            {{ $t('dashboard.viewAll') }}
          </button>
        </div>

        <div class="relative">
          <!-- 时间线竖线 -->
          <div class="absolute left-4 top-0 bottom-0 w-px bg-border dark:bg-border-dark"></div>

          <!-- 活动列表 -->
          <div class="space-y-4">
            <div
              v-for="activity in recentActivities"
              :key="activity.id"
              class="relative flex items-start gap-4 pl-20"
            >
              <!-- 时间线节点 -->
              <div
                class="absolute left-2.5 w-3 h-3 rounded-full border-2 border-surface dark:border-surface-dark"
                :class="{
                  'bg-success': activity.type === 'success',
                  'bg-primary': activity.type === 'info',
                  'bg-secondary': activity.type === 'warning',
                  'bg-danger': activity.type === 'error'
                }"
              ></div>

              <!-- 活动内容 -->
              <div class="flex-1 pb-4">
                <div class="flex items-start justify-between">
                  <div>
                    <p class="font-medium text-text-primary dark:text-text-primary-dark">{{ activity.title }}</p>
                    <p class="text-sm text-text-secondary dark:text-text-secondary-dark mt-0.5">{{ activity.content }}</p>
                  </div>
                  <span class="text-xs text-text-secondary dark:text-text-secondary-dark whitespace-nowrap">
                    {{ activity.time }}
                  </span>
                </div>
                <div class="flex items-center gap-2 mt-2">
                  <span
                    class="px-2 py-0.5 text-xs rounded-full"
                    :class="{
                      'bg-success/10 text-success': activity.type === 'success',
                      'bg-primary/10 text-primary': activity.type === 'info',
                      'bg-secondary/10 text-secondary': activity.type === 'warning',
                      'bg-danger/10 text-danger': activity.type === 'error'
                    }"
                  >
                    {{ activity.typeLabel }}
                  </span>
                  <span class="text-xs text-text-secondary dark:text-text-secondary-dark">
                    {{ activity.user }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷统计面板 -->
      <div
        class="bg-surface dark:bg-surface-dark rounded-xl shadow-gzang dark:shadow-lg p-6 animate-slide-up"
        style="animation-delay: 700ms"
      >
        <div class="mb-6">
          <h3 class="text-lg font-semibold text-text-primary dark:text-text-primary-dark">{{ $t('dashboard.operationOverview') }}</h3>
          <p class="text-text-secondary dark:text-text-secondary-dark text-sm mt-1">{{ $t('dashboard.operationSubtitle') }}</p>
        </div>

        <div class="space-y-6">
          <!-- 活跃用户 -->
          <div>
            <div class="flex justify-between items-center mb-2">
              <span class="text-sm text-text-secondary dark:text-text-secondary-dark">{{ $t('dashboard.activeUsers') }}</span>
              <span class="font-mono text-sm font-semibold text-text-primary dark:text-text-primary-dark">89.2%</span>
            </div>
            <div class="h-2 bg-gray-100 dark:bg-gray-700 rounded-full overflow-hidden">
              <div class="h-full bg-primary rounded-full" style="width: 89.2%"></div>
            </div>
          </div>

          <!-- 交易成功率 -->
          <div>
            <div class="flex justify-between items-center mb-2">
              <span class="text-sm text-text-secondary dark:text-text-secondary-dark">{{ $t('dashboard.transactionSuccess') }}</span>
              <span class="font-mono text-sm font-semibold text-text-primary dark:text-text-primary-dark">99.5%</span>
            </div>
            <div class="h-2 bg-gray-100 dark:bg-gray-700 rounded-full overflow-hidden">
              <div class="h-full bg-success rounded-full" style="width: 99.5%"></div>
            </div>
          </div>

          <!-- 系统健康 -->
          <div>
            <div class="flex justify-between items-center mb-2">
              <span class="text-sm text-text-secondary dark:text-text-secondary-dark">{{ $t('dashboard.systemHealth') }}</span>
              <span class="font-mono text-sm font-semibold text-success">{{ $t('dashboard.excellent') }}</span>
            </div>
            <div class="h-2 bg-gray-100 dark:bg-gray-700 rounded-full overflow-hidden">
              <div class="h-full bg-success rounded-full" style="width: 98%"></div>
            </div>
          </div>

          <!-- 本月收入 -->
          <div class="pt-4 border-t border-border dark:border-border-dark">
            <div class="flex justify-between items-center mb-2">
              <span class="text-sm text-text-secondary dark:text-text-secondary-dark">{{ $t('dashboard.monthlyRevenue') }}</span>
              <span class="font-mono text-lg font-bold text-success">
                ¥{{ formatCurrency(monthlyRevenue) }}
              </span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm text-text-secondary dark:text-text-secondary-dark">{{ $t('dashboard.monthlyExpense') }}</span>
              <span class="font-mono text-lg font-bold text-danger">
                ¥{{ formatCurrency(monthlyExpense) }}
              </span>
            </div>
          </div>

          <!-- 快捷操作 -->
          <div class="pt-4 border-t border-border dark:border-border-dark">
            <p class="text-sm font-medium text-text-primary dark:text-text-primary-dark mb-3">{{ $t('dashboard.quickActions') }}</p>
            <div class="grid grid-cols-2 gap-2">
              <button class="btn-outline text-sm py-2" @click="goToQuickAction('users')">
                {{ $t('dashboard.userManagement') }}
              </button>
              <button class="btn-outline text-sm py-2" @click="goToQuickAction('companies')">
                {{ $t('dashboard.companyManagement') }}
              </button>
              <button class="btn-outline text-sm py-2" @click="goToQuickAction('permissions')">
                {{ $t('dashboard.permissionConfig') }}
              </button>
              <button class="btn-outline text-sm py-2" @click="goToQuickAction('system')">
                {{ $t('dashboard.systemSettings') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';

const { t, tm, locale } = useI18n();
const router = useRouter();

type ECharts = echarts.ECharts;

// 统计数据
const stats = ref({
  users: 0,
  companies: 0,
  totalBalance: 0,
  transactions: 0
});

// 时间选择
const selectedPeriod = ref<'week' | 'month' | 'year'>('month');

// 月度收支数据
const monthlyRevenue = 1250000;
const monthlyExpense = 890000;

// 分类数据
const categoryData = computed(() => [
  { key: 'food', value: 35, color: '#06D6A0' },
  { key: 'transport', value: 20, color: '#FB8B24' },
  { key: 'shopping', value: 25, color: '#118AB2' },
  { key: 'entertainment', value: 12, color: '#FFD166' },
  { key: 'other', value: 8, color: '#EF476F' }
]);

// 最近活动
const timeAgo = (minutes: number) => {
  if (minutes < 60) return t('dashboard.timeMinutesAgo', { n: minutes });
  return t('dashboard.timeHourAgo', { n: Math.floor(minutes / 60) });
};

const recentActivities = computed(() => [
  {
    id: 1,
    title: t('dashboard.activityTitleNewUser'),
    content: t('dashboard.activityContentNewUser'),
    time: timeAgo(2),
    type: 'success',
    typeLabel: t('dashboard.activityTypes.register'),
    user: t('dashboard.activitySystemUser')
  },
  {
    id: 2,
    title: t('dashboard.activityTitleTransaction'),
    content: t('dashboard.activityContentTransaction'),
    time: timeAgo(15),
    type: 'info',
    typeLabel: t('dashboard.activityTypes.transaction'),
    user: t('dashboard.activityUserLiSi')
  },
  {
    id: 3,
    title: t('dashboard.activityTitleCompanyCreated'),
    content: t('dashboard.activityContentCompanyCreated'),
    time: timeAgo(60),
    type: 'warning',
    typeLabel: t('dashboard.activityTypes.create'),
    user: t('dashboard.activityAdmin')
  },
  {
    id: 4,
    title: t('dashboard.activityTitlePermissionChanged'),
    content: t('dashboard.activityContentPermissionChanged'),
    time: timeAgo(120),
    type: 'info',
    typeLabel: t('dashboard.activityTypes.permission'),
    user: t('dashboard.activityAdmin')
  },
  {
    id: 5,
    title: t('dashboard.activityTitleSecurityAlert'),
    content: t('dashboard.activityContentSecurityAlert'),
    time: timeAgo(180),
    type: 'error',
    typeLabel: t('dashboard.activityTypes.alert'),
    user: t('dashboard.activitySystemUser')
  }
]);

// 图表引用
const trendChartRef = ref<HTMLElement | null>(null);
const pieChartRef = ref<HTMLElement | null>(null);
let trendChart: ECharts | null = null;
let pieChart: ECharts | null = null;

// 语言变化时重绘图表
watch(locale, async () => {
  await nextTick();
  if (trendChart) {
    initTrendChart();
  }
  if (pieChart) {
    initPieChart();
  }
});

// 格式化货币
const formatCurrency = (amount: number) => {
  return amount.toLocaleString('zh-CN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  });
};

// 快捷操作跳转
const goToQuickAction = (action: string) => {
  const routeMap: Record<string, string> = {
    users: '/users',
    companies: '/companies',
    permissions: '/permissions',
    system: '/system'
  };
  if (routeMap[action]) {
    router.push(routeMap[action]);
  }
};

// 初始化趋势图
const initTrendChart = () => {
  if (!trendChartRef.value) return;

  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value);
  }
  const months = tm('dashboard.months') as string[];
  const incomeLabel = t('dashboard.chartIncome');
  const expenseLabel = t('dashboard.chartExpense');

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#fff',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      textStyle: {
        color: '#1F2937'
      },
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params: any) => {
        const date = params[0].axisValue;
        let html = `<div class="font-medium">${date}</div>`;
        params.forEach((p: any) => {
          const color = p.seriesName === incomeLabel ? '#06D6A0' : '#EF476F';
          html += `<div style="display:flex;align-items:center;gap:8px;margin-top:4px;">
            <span style="display:inline-block;width:8px;height:8px;border-radius:2px;background:${color}"></span>
            <span>${p.seriesName}:</span>
            <span class="font-mono" style="color:${color}">¥${p.value.toLocaleString()}</span>
          </div>`;
        });
        return html;
      }
    },
    legend: {
      data: [incomeLabel, expenseLabel],
      bottom: 0,
      icon: 'rect',
      itemWidth: 12,
      itemHeight: 4,
      textStyle: {
        color: '#6B7280'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: {
        lineStyle: {
          color: '#E5E7EB'
        }
      },
      axisLabel: {
        color: '#6B7280'
      }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          color: '#F3F4F6'
        }
      },
      axisLabel: {
        color: '#6B7280',
        formatter: (value: number) => {
          if (value >= 1000000) return (value / 1000000).toFixed(1) + 'M';
          if (value >= 1000) return (value / 1000) + 'k';
          return value.toString();
        }
      }
    },
    series: [
      {
        name: incomeLabel,
        type: 'bar',
        barWidth: '35%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#06D6A0' },
            { offset: 1, color: 'rgba(6, 214, 160, 0.3)' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        data: [850000, 920000, 880000, 1050000, 980000, 1120000, 1050000, 1180000, 1200000, 1150000, 1250000, 1300000]
      },
      {
        name: expenseLabel,
        type: 'bar',
        barWidth: '35%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#EF476F' },
            { offset: 1, color: 'rgba(239, 71, 111, 0.3)' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        data: [650000, 720000, 680000, 800000, 750000, 850000, 800000, 880000, 900000, 850000, 890000, 950000]
      }
    ]
  };

  trendChart.setOption(option, { notMerge: true });
};

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return;

  if (!pieChart) {
    pieChart = echarts.init(pieChartRef.value);
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      backgroundColor: '#fff',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      textStyle: {
        color: '#1F2937'
      },
      formatter: '{b}: {c}%'
    },
    series: [
      {
        type: 'pie',
        radius: ['55%', '80%'],
        center: ['50%', '50%'],
        avoidLabelOverlap: false,
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        labelLine: {
          show: false
        },
        data: categoryData.value.map(item => ({
          value: item.value,
          name: t(`dashboard.categories.${item.key}`),
          itemStyle: {
            color: item.color
          }
        }))
      }
    ]
  };

  pieChart.setOption(option, { notMerge: true });
};

// 窗口大小变化时重绘图表
const handleResize = () => {
  trendChart?.resize();
  pieChart?.resize();
};

// 获取统计数据
const fetchStats = async () => {
  try {
    // TODO: 调用API获取统计数据
    // 这里先模拟数据
    stats.value = {
      users: 1250,
      companies: 45,
      totalBalance: 1250000.50,
      transactions: 15680
    };
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
};

onMounted(() => {
  fetchStats();
  initTrendChart();
  initPieChart();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  trendChart?.dispose();
  pieChart?.dispose();
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

.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-slide-up {
  animation: slideUp 0.5s ease-out both;
}

/* 数字等宽对齐 */
.tabular-nums {
  font-variant-numeric: tabular-nums;
}
</style>
