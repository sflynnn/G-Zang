<template>
  <div class="p-4 md:p-6 lg:p-8">
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">{{ $t('report.title') }}</h1>
      <button class="btn-primary flex items-center gap-2">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" /></svg>
        导出报表
      </button>
    </div>

    <!-- 报表类型选择 -->
    <div class="flex gap-3 mb-6">
      <button v-for="tab in reportTabs" :key="tab.value" @click="activeTab = tab.value"
        class="px-4 py-2 rounded-xl text-sm font-medium transition-all"
        :class="activeTab === tab.value ? 'bg-secondary text-white' : 'bg-surface dark:bg-dark-surface text-text-secondary hover:bg-gray-50 dark:hover:bg-dark-bg'">
        {{ tab.label }}
      </button>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div v-for="stat in currentStats" :key="stat.label" class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <p class="text-xs text-text-secondary mb-1">{{ stat.label }}</p>
        <p class="text-2xl font-bold font-mono" :style="{ color: stat.color }">{{ stat.value }}</p>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-6 shadow-gzang">
        <h3 class="text-lg font-semibold mb-4 text-text-primary dark:text-dark-text-primary">{{ activeTab === 'income' ? '收入趋势' : activeTab === 'expense' ? '支出分类' : '利润趋势' }}</h3>
        <div ref="chartRef" class="w-full h-72"></div>
      </div>
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-6 shadow-gzang">
        <h3 class="text-lg font-semibold mb-4 text-text-primary dark:text-dark-text-primary">月度对比</h3>
        <div ref="barChartRef" class="w-full h-72"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';

const activeTab = ref('income');
const chartRef = ref<HTMLElement | null>(null);
const barChartRef = ref<HTMLElement | null>(null);
let chart: any = null;
let barChart: any = null;

const reportTabs = [
  { label: '收入报表', value: 'income' },
  { label: '支出报表', value: 'expense' },
  { label: '利润报表', value: 'profit' },
];

const statsData = {
  income: { total: '+156,800.00', compare: '+12.5%', budget: '120,000', rate: '130.6%', label: ['总收入', '环比增长', '预算目标', '完成率'], value: ['¥156,800', '+12.5%', '¥120,000', '130.6%'], color: ['#06D6A0', '#06D6A0', '#0F4C5C', '#06D6A0'] },
  expense: { total: '-76,500.00', compare: '+8.2%', budget: '100,000', rate: '76.5%', label: ['总支出', '环比增长', '预算额度', '使用率'], value: ['¥76,500', '+8.2%', '¥100,000', '76.5%'], color: ['#EF476F', '#EF476F', '#0F4C5C', '#FB8B24'] },
  profit: { total: '+80,300.00', compare: '+18.3%', budget: '50,000', rate: '160.6%', label: ['净利润', '环比增长', '预算目标', '完成率'], value: ['¥80,300', '+18.3%', '¥50,000', '160.6%'], color: ['#06D6A0', '#06D6A0', '#0F4C5C', '#06D6A0'] },
};

const currentStats = computed<{ label: string; value: string; color: string }[]>(() => {
  const entry = statsData[activeTab.value as keyof typeof statsData]
  return entry.label.map((l, i) => ({ label: l, value: entry.value[i], color: entry.color[i] }))
})

const initCharts = () => {
  if (chartRef.value) {
    chart = echarts.init(chartRef.value);
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月'] },
      yAxis: { type: 'value' },
      series: [{ data: [28000, 32000, 25000, 38000, 42000], type: 'line', smooth: true, areaStyle: { color: 'rgba(6, 214, 160, 0.1)' }, itemStyle: { color: '#06D6A0' } }]
    });
  }
  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value);
    barChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['收入', '支出'], bottom: 0 },
      xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月'] },
      yAxis: { type: 'value' },
      series: [
        { name: '收入', data: [28000, 32000, 25000, 38000, 42000], type: 'bar', itemStyle: { color: '#06D6A0' } },
        { name: '支出', data: [18000, 22000, 15000, 25000, 28000], type: 'bar', itemStyle: { color: '#EF476F' } },
      ]
    });
  }
};

const handleResize = () => { chart?.resize(); barChart?.resize(); };

onMounted(() => { initCharts(); window.addEventListener('resize', handleResize); });
onUnmounted(() => { window.removeEventListener('resize', handleResize); chart?.dispose(); barChart?.dispose(); });
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center justify-center gap-2 px-4 py-2 bg-secondary text-white rounded-xl font-medium text-sm transition-all hover:bg-secondary-dark active:scale-95; }
</style>
