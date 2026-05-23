<template>
  <PageTransition>
    <view class="budget-detail-page apple-style">
      <!-- Navigation -->
      <view class="nav-large-title">
        <view class="nav-header">
          <view class="nav-back" @click="goBack">
            <AppleIcon name="left" :size="20" color="var(--gzang-text-primary)" />
          </view>
          <text class="nav-title">预算详情</text>
          <view class="nav-actions">
            <view class="nav-action" @click="handleEdit">
              <AppleIcon name="edit" :size="18" color="var(--gzang-text-secondary)" />
            </view>
          </view>
        </view>
      </view>

      <scroll-view class="main-content" scroll-y="true">
        <!-- Loading -->
        <view v-if="loading" class="loading-state">
          <uni-load-more status="loading" />
        </view>

        <template v-else-if="budget">
          <!-- Header Card -->
          <view class="header-card">
            <view class="category-badge">
              <view class="category-icon" :style="{ background: (budget.categoryColor || '#0F4C5C') + '20' }">
                <AppleIcon :name="budget.categoryIcon || 'category'" :size="24" :color="budget.categoryColor || '#0F4C5C'" />
              </view>
              <text class="category-name">{{ budget.name || budget.categoryName }}</text>
            </view>
            <view class="period-badge">
              <text>{{ budget.periodTypeName }}</text>
            </view>
          </view>

          <!-- Amount Card -->
          <view class="amount-card">
            <view class="amount-section">
              <text class="amount-label">预算金额</text>
              <text class="amount-value primary">{{ currencySymbol }}{{ formatAmount(budget.amount) }}</text>
            </view>
            <view class="amount-divider"></view>
            <view class="amount-section">
              <text class="amount-label">已花费</text>
              <text class="amount-value expense">{{ currencySymbol }}{{ formatAmount(budget.usedAmount) }}</text>
            </view>
            <view class="amount-divider"></view>
            <view class="amount-section">
              <text class="amount-label">剩余</text>
              <text class="amount-value" :class="remaining >= 0 ? 'income' : 'expense'">
                {{ currencySymbol }}{{ formatAmount(Math.abs(remaining)) }}
              </text>
            </view>
          </view>

          <!-- Progress -->
          <view class="progress-card">
            <view class="progress-header">
              <text class="progress-title">执行进度</text>
              <text class="progress-rate" :class="statusClass">{{ Math.round(budget.usageRate) }}%</text>
            </view>
            <view class="progress-track">
              <view
                class="progress-fill"
                :class="statusClass"
                :style="{ width: Math.min(budget.usageRate, 100) + '%' }"
              ></view>
              <view v-if="budget.usageRate > 100" class="progress-overflow" :style="{ left: '100%' }"></view>
            </view>
            <view class="progress-labels">
              <text>0%</text>
              <text>50%</text>
              <text>100%</text>
            </view>
          </view>

          <!-- Warning Alert -->
          <view v-if="budget.isWarning" class="alert-card warning">
            <AppleIcon name="warning" :size="18" color="#FB8B24" />
            <text class="alert-text">预算即将超支，当前已使用 {{ Math.round(budget.usageRate) }}%，超过预警阈值 {{ budget.warningThreshold }}%</text>
          </view>

          <!-- Period Info -->
          <view class="info-card">
            <view class="info-row">
              <text class="info-label">周期开始</text>
              <text class="info-value">{{ budget.periodStart || '-' }}</text>
            </view>
            <view class="info-divider"></view>
            <view class="info-row">
              <text class="info-label">周期结束</text>
              <text class="info-value">{{ budget.periodEnd || '-' }}</text>
            </view>
            <view v-if="budget.remark" class="info-divider"></view>
            <view v-if="budget.remark" class="info-row">
              <text class="info-label">备注</text>
              <text class="info-value">{{ budget.remark }}</text>
            </view>
          </view>

          <!-- Actions -->
          <view class="actions-card">
            <button class="action-btn danger" @click="handleDelete">
              <AppleIcon name="trash" :size="16" color="var(--gzang-danger)" />
              <text>删除预算</text>
            </button>
          </view>
        </template>

        <!-- Not Found -->
        <view v-else class="empty-state">
          <view class="empty-icon-wrapper">
            <AppleIcon name="chart" :size="48" color="var(--gzang-text-tertiary)" />
          </view>
          <text class="empty-title">预算不存在</text>
        </view>

        <view class="bottom-safe-area"></view>
      </scroll-view>
    </view>
  </PageTransition>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import PageTransition from '@/components/common/PageTransition/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import { getBudget, deleteBudget, type Budget } from '@/api/budget'
import { useBookStore } from '@/stores/book'

const bookStore = useBookStore()

const loading = ref(false)
const budget = ref<Budget | null>(null)
const budgetId = ref<number>(0)

onLoad((options: any) => {
  if (options?.id) {
    budgetId.value = parseInt(String(options.id))
  }
})

const currencySymbol = computed(() => bookStore.currentCurrencySymbol)

const remaining = computed(() => {
  if (!budget.value) return 0
  return budget.value.amount - budget.value.usedAmount
})

const statusClass = computed(() => {
  if (!budget.value) return ''
  const rate = budget.value.usageRate
  if (rate > 100) return 'danger'
  if (rate >= 80) return 'warning'
  return 'success'
})

function formatAmount(amount: number): string {
  return Math.abs(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function goBack() { uni.navigateBack() }

function handleEdit() {
  uni.navigateTo({ url: `/pages/budget/create?id=${budgetId.value}` })
}

async function handleDelete() {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个预算吗？此操作不可恢复。',
    confirmColor: '#EF476F',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteBudget(budgetId.value)
          uni.showToast({ title: '删除成功', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 1500)
        } catch (e) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

async function loadBudget() {
  loading.value = true
  try {
    budget.value = await getBudget(budgetId.value)
  } catch (e) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (!budgetId.value) {
    loading.value = false
    return
  }
  await bookStore.fetchBooks()
  await loadBudget()
})
</script>

<style lang="scss" scoped>
.budget-detail-page {
  min-height: 100vh;
  background: var(--gzang-bg);
}

.nav-large-title {
  background: var(--gzang-bg);
  padding: 0 var(--apple-space-4);
  padding-top: calc(constant(safe-area-inset-top) + var(--apple-space-3));
}

.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--apple-space-3);
}

.nav-back {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  font-size: var(--apple-text-xl);
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
}

.nav-actions {
  display: flex;
  gap: var(--apple-space-2);
}

.nav-action {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-content {
  padding: 0 var(--apple-space-4);
}

.header-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--apple-space-4);
}

.category-badge {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
}

.category-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--apple-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-name {
  font-size: var(--apple-text-xl);
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
}

.period-badge {
  background: var(--gzang-primary);
  color: white;
  padding: 6px 14px;
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-xs);
  font-weight: var(--apple-font-semibold);
}

.amount-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-5);
  display: flex;
  align-items: center;
  margin-bottom: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
}

.amount-section {
  flex: 1;
  text-align: center;
}

.amount-label {
  display: block;
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-secondary);
  margin-bottom: 6px;
}

.amount-value {
  display: block;
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-bold);

  &.primary { color: var(--gzang-primary); }
  &.income { color: var(--gzang-success); }
  &.expense { color: var(--gzang-danger); }
}

.amount-divider {
  width: 1px;
  height: 40px;
  background: var(--gzang-border);
}

.progress-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-5);
  margin-bottom: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-4);
}

.progress-title {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.progress-rate {
  font-size: var(--apple-text-2xl);
  font-weight: var(--apple-font-bold);

  &.success { color: var(--gzang-success); }
  &.warning { color: rgba(255, 209, 102, 1); }
  &.danger { color: var(--gzang-danger); }
}

.progress-track {
  height: 12px;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-full);
  overflow: visible;
  position: relative;
}

.progress-fill {
  height: 100%;
  border-radius: var(--apple-radius-full);
  transition: width 0.5s var(--apple-ease-out);

  &.success { background: var(--gzang-success); }
  &.warning { background: rgba(255, 209, 102, 1); }
  &.danger { background: var(--gzang-danger); }
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  margin-top: var(--apple-space-2);
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}

.alert-card {
  display: flex;
  align-items: flex-start;
  gap: var(--apple-space-3);
  padding: var(--apple-space-4);
  border-radius: var(--apple-radius-xl);
  margin-bottom: var(--apple-space-4);

  &.warning {
    background: rgba(251, 139, 36, 0.1);
    border: 1px solid rgba(251, 139, 36, 0.3);
  }
}

.alert-text {
  flex: 1;
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  line-height: 1.5;
}

.info-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  margin-bottom: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--apple-space-3) 0;
}

.info-label {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
}

.info-value {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
}

.info-divider {
  height: 1px;
  background: var(--gzang-border);
}

.actions-card {
  margin-top: var(--apple-space-4);
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-4);
  border-radius: var(--apple-radius-xl);
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  border: none;

  &.danger {
    background: rgba(239, 71, 111, 0.1);
    color: var(--gzang-danger);
  }
}

.loading-state {
  padding: var(--apple-space-10) 0;
}

.empty-state {
  padding: var(--apple-space-10) 0;
  text-align: center;
}

.empty-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--gzang-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--apple-space-4);
}

.empty-title {
  font-size: var(--apple-text-lg);
  color: var(--gzang-text-secondary);
}

.bottom-safe-area {
  height: var(--apple-space-4);
}
</style>
