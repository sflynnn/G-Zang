<template>
  <div class="p-4 md:p-6 lg:p-8">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">{{ $t('costCenter.title') }}</h1>
    </div>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div v-for="center in costCenters" :key="center.id" class="bg-surface dark:bg-dark-surface rounded-2xl p-6 shadow-gzang">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-xl flex items-center justify-center text-2xl" :style="{ backgroundColor: center.color + '15' }">
              {{ center.icon }}
            </div>
            <div>
              <p class="font-semibold text-text-primary dark:text-dark-text-primary">{{ center.name }}</p>
              <p class="text-xs text-text-secondary">{{ center.manager }}</p>
            </div>
          </div>
        </div>
        <div class="grid grid-cols-3 gap-4">
          <div class="text-center p-3 rounded-xl bg-gray-50 dark:bg-dark-bg">
            <p class="text-xs text-text-secondary mb-1">总成本</p>
            <p class="font-mono font-bold text-text-primary dark:text-dark-text-primary">{{ formatCurrency(center.totalCost) }}</p>
          </div>
          <div class="text-center p-3 rounded-xl bg-gray-50 dark:bg-dark-bg">
            <p class="text-xs text-text-secondary mb-1">已分摊</p>
            <p class="font-mono font-bold text-success">{{ formatCurrency(center.allocated) }}</p>
          </div>
          <div class="text-center p-3 rounded-xl bg-gray-50 dark:bg-dark-bg">
            <p class="text-xs text-text-secondary mb-1">待分摊</p>
            <p class="font-mono font-bold text-warning">{{ formatCurrency(center.unallocated) }}</p>
          </div>
        </div>
        <div class="mt-4">
          <div class="flex justify-between text-xs mb-1">
            <span class="text-text-secondary">分摊进度</span>
            <span class="font-mono" :class="center.allocateRate > 80 ? 'text-success' : 'text-text-secondary'">{{ center.allocateRate }}%</span>
          </div>
          <div class="h-2 bg-gray-100 dark:bg-dark-bg rounded-full overflow-hidden">
            <div class="h-full rounded-full bg-success" :style="{ width: center.allocateRate + '%' }" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const formatCurrency = (v: number) => v.toLocaleString('zh-CN', { minimumFractionDigits: 2 });

const costCenters = ref([
  { id: 1, name: '研发成本中心', manager: '张明', icon: '💻', color: '#0F4C5C', totalCost: 120000, allocated: 96000, unallocated: 24000, allocateRate: 80 },
  { id: 2, name: '营销成本中心', manager: '李华', icon: '📢', color: '#FB8B24', totalCost: 80000, allocated: 72000, unallocated: 8000, allocateRate: 90 },
  { id: 3, name: '运营成本中心', manager: '王芳', icon: '⚙️', color: '#06D6A0', totalCost: 60000, allocated: 45000, unallocated: 15000, allocateRate: 75 },
  { id: 4, name: '管理成本中心', manager: '刘强', icon: '📊', color: '#118AB2', totalCost: 50000, allocated: 40000, unallocated: 10000, allocateRate: 80 },
]);
</script>
