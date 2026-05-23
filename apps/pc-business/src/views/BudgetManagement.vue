<template>
  <div class="p-4 md:p-6 lg:p-8">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">{{ $t('budget.title') }}</h1>
    </div>

    <!-- 总预算概览 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div class="bg-gradient-to-br from-primary to-primary-light rounded-2xl p-5 text-white">
        <p class="text-white/70 text-sm mb-1">本月总预算</p>
        <p class="text-3xl font-bold font-mono">{{ formatCurrency(totalBudget) }}</p>
        <p class="text-white/60 text-sm mt-1">已使用 {{ formatCurrency(totalUsed) }} ({{ usageRate }}%)</p>
      </div>
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <p class="text-xs text-text-secondary mb-1">剩余预算</p>
        <p class="text-2xl font-bold font-mono text-success">{{ formatCurrency(totalBudget - totalUsed) }}</p>
        <div class="h-2 bg-gray-100 dark:bg-dark-bg rounded-full mt-2 overflow-hidden">
          <div class="h-full rounded-full bg-success transition-all" :style="{ width: Math.min(usageRate, 100) + '%' }" />
        </div>
      </div>
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <p class="text-xs text-text-secondary mb-1">预警状态</p>
        <div class="flex items-center gap-2 mt-1">
          <span class="w-3 h-3 rounded-full" :class="usageRate > 90 ? 'bg-danger animate-pulse' : usageRate > 70 ? 'bg-warning' : 'bg-success'" />
          <span class="text-sm font-medium" :class="usageRate > 90 ? 'text-danger' : usageRate > 70 ? 'text-warning' : 'text-success'">
            {{ usageRate > 90 ? '预算已超支' : usageRate > 70 ? '预算接近上限' : '预算充足' }}
          </span>
        </div>
      </div>
    </div>

    <!-- 预算列表 -->
    <div class="bg-surface dark:bg-dark-surface rounded-2xl p-6 shadow-gzang">
      <h3 class="text-lg font-semibold mb-4 text-text-primary dark:text-dark-text-primary">部门预算明细</h3>
      <div class="space-y-4">
        <div v-for="item in budgetList" :key="item.id" class="border border-border dark:border-dark-border rounded-xl p-4">
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center">
                <svg class="w-5 h-5 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
              </div>
              <div>
                <p class="font-semibold text-text-primary dark:text-dark-text-primary">{{ item.department }}</p>
                <p class="text-xs text-text-secondary">{{ item.period }}</p>
              </div>
            </div>
            <div class="text-right">
              <p class="font-mono font-bold" :class="item.usageRate > 100 ? 'text-danger' : 'text-text-primary dark:text-dark-text-primary'">
                {{ formatCurrency(item.used) }} / {{ formatCurrency(item.budget) }}
              </p>
              <span class="text-xs px-2 py-0.5 rounded-full" :class="item.usageRate > 100 ? 'bg-danger/10 text-danger' : 'bg-gray-100 dark:bg-dark-bg text-text-secondary'">
                {{ item.usageRate }}%
              </span>
            </div>
          </div>
          <div class="h-2 bg-gray-100 dark:bg-dark-bg rounded-full overflow-hidden">
            <div
              class="h-full rounded-full transition-all duration-500"
              :class="item.usageRate > 100 ? 'bg-danger' : item.usageRate > 80 ? 'bg-warning' : 'bg-primary'"
              :style="{ width: Math.min(item.usageRate, 100) + '%' }"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

const totalBudget = 100000;
const totalUsed = 76500;

const usageRate = computed(() => Math.round((totalUsed / totalBudget) * 100));

const formatCurrency = (v: number) => v.toLocaleString('zh-CN', { minimumFractionDigits: 2 });

const budgetList = ref([
  { id: 1, department: '研发部', period: '2026年5月', budget: 40000, used: 32000, usageRate: 80 },
  { id: 2, department: '市场部', period: '2026年5月', budget: 30000, used: 28500, usageRate: 95 },
  { id: 3, department: '行政部', period: '2026年5月', budget: 20000, used: 12000, usageRate: 60 },
  { id: 4, department: '销售部', period: '2026年5月', budget: 10000, used: 4000, usageRate: 40 },
]);
</script>
