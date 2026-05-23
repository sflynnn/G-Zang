<template>
  <div class="report-page min-h-screen p-4 md:p-6 lg:p-8">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">{{ $t('report.title') }}</h1>
      <p class="text-sm text-text-secondary dark:text-dark-text-secondary mt-1">{{ $t('report.subtitle') }}</p>
    </div>

    <!-- 时间范围选择 -->
    <div class="flex items-center gap-3 mb-6">
      <NDatePicker v-model:value="dateRange" type="daterange" clearable class="!w-80" />
      <button
        v-for="p in periodOptions"
        :key="p.value"
        @click="selectPeriod(p.value)"
        class="px-3 py-1.5 rounded-lg text-sm transition-all"
        :class="selectedPeriod === p.value ? 'bg-secondary text-white' : 'bg-surface dark:bg-dark-surface text-text-secondary hover:bg-gray-50 dark:hover:bg-dark-bg'"
      >
        {{ p.label }}
      </button>
    </div>

    <!-- 收支总览 -->
    <div class="grid grid-cols-3 gap-4 mb-6">
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <p class="text-xs text-text-secondary mb-2">{{ $t('report.totalIncome') }}</p>
        <p class="text-2xl font-bold font-mono text-success">+{{ formatCurrency(stats.totalIncome) }}</p>
        <p class="text-xs text-text-secondary mt-1">{{ stats.incomeCount }} {{ $t('report.records') }}</p>
      </div>
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <p class="text-xs text-text-secondary mb-2">{{ $t('report.totalExpense') }}</p>
        <p class="text-2xl font-bold font-mono text-danger">-{{ formatCurrency(stats.totalExpense) }}</p>
        <p class="text-xs text-text-secondary mt-1">{{ stats.expenseCount }} {{ $t('report.records') }}</p>
      </div>
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <p class="text-xs text-text-secondary mb-2">{{ $t('report.netBalance') }}</p>
        <p class="text-2xl font-bold font-mono" :class="stats.netBalance >= 0 ? 'text-success' : 'text-danger'">
          {{ stats.netBalance >= 0 ? '+' : '' }}{{ formatCurrency(stats.netBalance) }}
        </p>
        <p class="text-xs text-text-secondary mt-1">{{ $t('report.net') }}</p>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
      <!-- 支出分类饼图 -->
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-6 shadow-gzang">
        <h3 class="text-lg font-semibold mb-4 text-text-primary dark:text-dark-text-primary">{{ $t('report.expenseByCategory') }}</h3>
        <div ref="expenseChartRef" class="w-full h-72"></div>
      </div>

      <!-- 收支趋势 -->
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-6 shadow-gzang">
        <h3 class="text-lg font-semibold mb-4 text-text-primary dark:text-dark-text-primary">{{ $t('report.trend') }}</h3>
        <div ref="trendChartRef" class="w-full h-72"></div>
      </div>
    </div>

    <!-- 分类明细 -->
    <div class="bg-surface dark:bg-dark-surface rounded-2xl p-6 shadow-gzang">
      <h3 class="text-lg font-semibold mb-4 text-text-primary dark:text-dark-text-primary">{{ $t('report.categoryDetail') }}</h3>
      <div class="space-y-4">
        <div
          v-for="cat in categoryStats"
          :key="cat.categoryId"
          class="flex items-center gap-4"
        >
          <div class="w-8 h-8 rounded-lg flex items-center justify-center text-sm" :style="{ backgroundColor: cat.color + '20', color: cat.color }">
            {{ cat.icon || '📦' }}
          </div>
          <div class="flex-1">
            <div class="flex justify-between items-center mb-1">
              <span class="text-sm font-medium text-text-primary dark:text-dark-text-primary">{{ cat.categoryName }}</span>
              <span class="text-sm font-mono text-text-primary dark:text-dark-text-primary">{{ formatCurrency(cat.amount) }}</span>
            </div>
            <div class="h-2 bg-gray-100 dark:bg-dark-bg rounded-full overflow-hidden">
              <div
                class="h-full rounded-full transition-all"
                :style="{ width: cat.percent + '%', backgroundColor: cat.color }"
              />
            </div>
            <p class="text-xs text-text-secondary mt-1">{{ cat.count }} {{ $t('report.transactions') }}, {{ cat.percent }}%</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { NDatePicker } from 'naive-ui';
import { useI18n } from 'vue-i18n';
import * as echarts from 'echarts';
import { accountingApi } from '@gzang/shared';
import { usePersonalStore } from '@/stores/personal';

type ECharts = echarts.ECharts;

const { t } = useI18n();
const personalStore = usePersonalStore();

const dateRange = ref<[number, number] | null>(null);
const selectedPeriod = ref('month');
const expenseChartRef = ref<HTMLElement | null>(null);
const trendChartRef = ref<HTMLElement | null>(null);
let expenseChart: ECharts | null = null;
let trendChart: ECharts | null = null;

const periodOptions = [
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '本年', value: 'year' },
];

const stats = ref({ totalIncome: 0, totalExpense: 0, netBalance: 0, incomeCount: 0, expenseCount: 0 });
const categoryStats = ref<any[]>([]);

const formatCurrency = (v: number) => v.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

const selectPeriod = (p: string) => {
  selectedPeriod.value = p;
  const now = new Date();
  let start: Date, end: Date = now;
  if (p === 'week') { start = new Date(now); start.setDate(now.getDate() - 7); }
  else if (p === 'month') { start = new Date(now.getFullYear(), now.getMonth(), 1); }
  else { start = new Date(now.getFullYear(), 0, 1); }
  dateRange.value = [start.getTime(), end.getTime()];
  loadData();
};

const getDateRange = () => {
  if (dateRange.value) return { startDate: new Date(dateRange.value[0]).toISOString(), endDate: new Date(dateRange.value[1]).toISOString() };
  return {};
};

const loadData = async () => {
  const params = { ...getDateRange(), page: 1, size: 1000 };
  try {
    const res = await accountingApi.getTransactions(params);
    if (res.data) {
      const txs = res.data.records;
      const incomeTxs = txs.filter((t: any) => t.type === 1);
      const expenseTxs = txs.filter((t: any) => t.type === 2);
      stats.value = {
        totalIncome: incomeTxs.reduce((s: number, t: any) => s + t.amount, 0),
        totalExpense: expenseTxs.reduce((s: number, t: any) => s + t.amount, 0),
        netBalance: 0,
        incomeCount: incomeTxs.length,
        expenseCount: expenseTxs.length,
      };
      stats.value.netBalance = stats.value.totalIncome - stats.value.totalExpense;
      updateCategoryStats(expenseTxs);
      updateCharts(expenseTxs, txs);
    }
  } catch (error) { console.error('加载数据失败:', error); }
};

const updateCategoryStats = (expenseTxs: any[]) => {
  const map = new Map<number, any>();
  for (const tx of expenseTxs) {
    if (!map.has(tx.categoryId)) {
      const cat = personalStore.categories.find(c => c.id === tx.categoryId);
      map.set(tx.categoryId, { categoryId: tx.categoryId, categoryName: cat?.categoryName || '未知', color: '#0F4C5C', icon: (cat as any)?.icon || '📦', amount: 0, count: 0 });
    }
    const item = map.get(tx.categoryId);
    item.amount += tx.amount;
    item.count++;
  }
  const total = Array.from(map.values()).reduce((s, i) => s + i.amount, 0);
  categoryStats.value = Array.from(map.values())
    .map((i: any) => ({ ...i, percent: total > 0 ? Math.round((i.amount / total) * 100) : 0 }))
    .sort((a, b) => b.amount - a.amount);
};

const updateCharts = (expenseTxs: any[], allTxs: any[]) => {
  if (expenseChart) {
    const pieData = categoryStats.value.map((c: any) => ({ name: c.categoryName, value: c.amount }));
    expenseChart.setOption({ series: [{ data: pieData }] });
  }
  if (trendChart) {
    const days = Array.from({ length: 7 }, (_, i) => {
      const d = new Date(); d.setDate(d.getDate() - (6 - i));
      return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' });
    });
    const incomeData = Array(7).fill(0);
    const expenseData = Array(7).fill(0);
    allTxs.forEach((tx: any) => {
      const day = new Date(tx.transactionTime).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' });
      const idx = days.indexOf(day);
      if (idx >= 0) { tx.type === 1 ? incomeData[idx] += tx.amount : expenseData[idx] += tx.amount; }
    });
    trendChart.setOption({
      xAxis: { data: days },
      series: [
        { data: incomeData },
        { data: expenseData },
      ]
    });
  }
};

const initCharts = () => {
  if (expenseChartRef.value) {
    expenseChart = echarts.init(expenseChartRef.value);
    expenseChart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
      legend: { bottom: 0, type: 'scroll' },
      series: [{ type: 'pie', radius: ['40%', '70%'], avoidLabelOverlap: false, label: { show: false }, emphasis: { label: { show: true, fontSize: 14 } }, data: [] }],
    });
  }
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value);
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: [t('report.income'), t('report.expense')], bottom: 0 },
      grid: { top: 10, bottom: 40, left: 50, right: 20 },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value', axisLabel: { formatter: (v: number) => v >= 1000 ? (v / 1000).toFixed(1) + 'k' : v } },
      series: [
        { name: t('report.income'), type: 'bar', data: [], itemStyle: { color: '#06D6A0' }, barWidth: '35%' },
        { name: t('report.expense'), type: 'bar', data: [], itemStyle: { color: '#EF476F' }, barWidth: '35%' },
      ],
    });
  }
};

const handleResize = () => { expenseChart?.resize(); trendChart?.resize(); };

watch(dateRange, loadData);

onMounted(() => { initCharts(); window.addEventListener('resize', handleResize); selectPeriod('month'); });
onUnmounted(() => { window.removeEventListener('resize', handleResize); expenseChart?.dispose(); trendChart?.dispose(); });
</script>

<style scoped>
.text-success { @apply text-[#06D6A0]; }
.text-danger { @apply text-[#EF476F]; }
.bg-secondary { @apply bg-[#FB8B24]; }
</style>
