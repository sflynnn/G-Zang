<template>
  <view class="category-grid-wrapper">
    <!-- Category Tabs -->
    <view v-if="showTabs" class="category-tabs">
      <view 
        class="tab-item"
        :class="{ active: activeTab === 1 }"
        @click="activeTab = 1"
      >
        <AppleIcon name="expense" :size="14" />
        <text>{{ t('accounting.expense') }}</text>
      </view>
      <view 
        class="tab-item"
        :class="{ active: activeTab === 2 }"
        @click="activeTab = 2"
      >
        <AppleIcon name="income" :size="14" />
        <text>{{ t('accounting.income') }}</text>
      </view>
    </view>

    <!-- Category Grid -->
    <view class="category-grid" :class="{ compact: compact }">
      <view 
        v-for="category in currentCategories"
        :key="category.id"
        class="category-item"
        :class="{ selected: modelValue === category.id }"
        @click="handleSelect(category)"
      >
        <view 
          class="category-icon"
          :style="{ background: getIconBg(category.color) }"
        >
          <AppleIcon :name="category.icon" :size="iconSize" :color="getIconColor(category)" />
        </view>
        <text class="category-name">{{ category.name }}</text>
        <text v-if="showAmount && category.amount" class="category-amount">
          {{ formatAmount(category.amount) }}
        </text>
      </view>
    </view>

    <!-- Error Message -->
    <text v-if="error" class="error-text">{{ error }}</text>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

export interface Category {
  id: number
  name: string
  icon: string
  color: string
  amount?: number
  type?: 1 | 2 // 1: income, 2: expense
}

interface Props {
  modelValue?: number | undefined
  expenseCategories: Category[]
  incomeCategories: Category[]
  currencySymbol?: string
  showTabs?: boolean
  showAmount?: boolean
  compact?: boolean
  error?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: undefined,
  currencySymbol: '¥',
  showTabs: true,
  showAmount: true,
  compact: false
})

const emit = defineEmits<{
  'update:modelValue': [value: number | undefined]
  'change': [category: Category]
}>()

const { t } = useI18n()

// Active tab: 1 = expense, 2 = income
const activeTab = ref<1 | 2>(2)

// Sync with transaction type
const iconSize = computed(() => props.compact ? 18 : 22)

const currentCategories = computed(() => {
  return activeTab.value === 1 ? props.expenseCategories : props.incomeCategories
})

const getIconBg = (color: string) => {
  return `${color}20` // 12% opacity
}

const getIconColor = (category: Category) => {
  if (props.modelValue === category.id) {
    // When selected, return white for dark backgrounds
    return '#FFFFFF'
  }
  return category.color
}

const formatAmount = (amount: number) => {
  if (amount >= 10000) {
    return (amount / 10000).toFixed(1) + 'w'
  }
  if (amount >= 1000) {
    return (amount / 1000).toFixed(1) + 'k'
  }
  return props.currencySymbol + amount.toFixed(0)
}

const handleSelect = (category: Category) => {
  emit('update:modelValue', category.id)
  emit('change', category)
}

// Watch for external type changes
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    const category = [...props.expenseCategories, ...props.incomeCategories].find(c => c.id === newVal)
    if (category?.type === 1) {
      activeTab.value = 2
    } else if (category?.type === 2) {
      activeTab.value = 1
    }
  }
})
</script>

<style lang="scss" scoped>
.category-grid-wrapper {
  width: 100%;
}

// Category Tabs
.category-tabs {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.active {
    background: var(--gzang-secondary);
    color: white;
  }
  
  &:active {
    transform: scale(0.96);
  }
}

// Category Grid
.category-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16rpx;
  
  &.compact {
    gap: 12rpx;
  }
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 8rpx;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.selected {
    transform: scale(1.05);
    box-shadow: 0 4rpx 16rpx rgba(251, 139, 36, 0.25);
    
    .category-icon {
      background: var(--gzang-secondary) !important;
    }
    
    .category-name {
      color: var(--gzang-secondary);
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.compact .category-item {
  padding: 12rpx 4rpx;
}

.category-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: var(--apple-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8rpx;
  transition: background var(--apple-duration-fast) var(--apple-ease-out);
}

.compact .category-icon {
  width: 56rpx;
  height: 56rpx;
}

.category-name {
  font-size: var(--apple-text-xs);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
  text-align: center;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-amount {
  font-size: 20rpx;
  font-family: var(--font-mono);
  color: var(--gzang-text-secondary);
  margin-top: 4rpx;
}

// Error Text
.error-text {
  display: block;
  font-size: var(--apple-text-xs);
  color: var(--gzang-danger);
  margin-top: 12rpx;
  text-align: center;
}
</style>
