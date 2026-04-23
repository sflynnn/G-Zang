<template>
  <view class="quick-record">
    <view class="record-header">
      <text class="record-title">快速记账</text>
      <text class="record-subtitle">选择金额，一键记账</text>
    </view>

    <view class="amount-presets">
      <view
        v-for="amount in presetAmounts"
        :key="amount"
        class="preset-item"
        :class="{ 'preset-item--active': selectedAmount === amount }"
        @click="selectedAmount = amount"
      >
        <text class="preset-value">¥{{ amount }}</text>
      </view>
    </view>

    <view class="amount-custom">
      <view class="custom-input-wrapper">
        <text class="currency-symbol">¥</text>
        <input
          v-model="customAmount"
          type="digit"
          class="custom-input"
          placeholder="自定义金额"
          @focus="selectedAmount = 0"
        />
      </view>
    </view>

    <view class="category-section">
      <text class="section-label">选择分类</text>
      <view class="category-row">
        <view
          v-for="cat in recentCategories"
          :key="cat.id"
          class="category-chip"
          :class="{ 'category-chip--active': selectedCategory?.id === cat.id }"
          @click="selectedCategory = cat"
        >
          <uni-icons
            :type="getIconType(cat.icon)"
            :size="16"
            :color="selectedCategory?.id === cat.id ? 'white' : 'var(--gzang-text-secondary)'"
          />
          <text class="chip-name">{{ cat.name }}</text>
        </view>
      </view>
    </view>

    <view class="record-actions">
      <button
        class="btn btn--primary btn--block"
        :disabled="!canSubmit"
        @click="handleSubmit"
      >
        立即记账
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { Category } from '@/types/transaction'

interface Props {
  recentCategories: Category[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  submit: [{ amount: number; categoryId: number }]
}>()

const presetAmounts = [10, 50, 100, 200, 500, 1000]
const selectedAmount = ref(0)
const customAmount = ref('')
const selectedCategory = ref<Category | undefined>(undefined)

const displayAmount = computed(() => {
  if (customAmount.value) {
    return parseFloat(customAmount.value) || 0
  }
  return selectedAmount.value
})

const canSubmit = computed(() => {
  return displayAmount.value > 0 && selectedCategory.value
})

const getIconType = (icon?: string): string => {
  const iconMap: Record<string, string> = {
    food: 'paperplane',
    transport: 'location',
    shopping: 'cart',
    entertainment: 'star',
    medical: 'first-aid-box',
    education: 'bookmark',
    housing: 'home',
    communication: 'phone',
    salary: 'wallet',
    investment: 'chartbars',
    gift: 'gift',
    other: 'circle'
  }
  return iconMap[icon || ''] || 'circle'
}

const handleSubmit = () => {
  if (!canSubmit.value || !selectedCategory.value) return

  emit('submit', {
    amount: displayAmount.value,
    categoryId: selectedCategory.value.id
  })
}
</script>

<style scoped lang="scss">
.quick-record {
  padding: 24rpx;
}

.record-header {
  text-align: center;
  margin-bottom: 32rpx;
}

.record-title {
  font-size: 36rpx;
  font-weight: 700;
  color: var(--gzang-primary);
}

.record-subtitle {
  font-size: 26rpx;
  color: var(--gzang-text-secondary);
  margin-top: 8rpx;
}

.amount-presets {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.preset-item {
  padding: 24rpx;
  background-color: var(--gzang-surface);
  border-radius: 16rpx;
  text-align: center;
  border: 2rpx solid var(--gzang-border);
  transition: all 0.2s;

  &--active {
    border-color: var(--gzang-secondary);
    background-color: rgba(251, 139, 36, 0.06);

    .preset-value {
      color: var(--gzang-secondary);
      font-weight: 700;
    }
  }

  &:active {
    transform: scale(0.98);
  }
}

.preset-value {
  font-size: 32rpx;
  color: var(--gzang-text-primary);
  font-family: 'Roboto Mono', monospace;
}

.amount-custom {
  margin-bottom: 24rpx;
}

.custom-input-wrapper {
  display: flex;
  align-items: center;
  background-color: var(--gzang-surface);
  border-radius: 16rpx;
  padding: 0 24rpx;
  border: 2rpx solid var(--gzang-border);
}

.currency-symbol {
  font-size: 36rpx;
  color: var(--gzang-text-secondary);
  margin-right: 8rpx;
}

.custom-input {
  flex: 1;
  height: 96rpx;
  font-size: 36rpx;
  font-family: 'Roboto Mono', monospace;
  color: var(--gzang-primary);

  &::placeholder {
    color: var(--gzang-text-secondary);
    font-size: 28rpx;
    font-family: inherit;
  }
}

.category-section {
  margin-bottom: 32rpx;
}

.section-label {
  font-size: 28rpx;
  color: var(--gzang-text-secondary);
  margin-bottom: 16rpx;
  display: block;
}

.category-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.category-chip {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 12rpx 20rpx;
  background-color: var(--gzang-surface);
  border-radius: 32rpx;
  border: 2rpx solid var(--gzang-border);
  transition: all 0.2s;

  &--active {
    border-color: var(--gzang-primary);
    background-color: var(--gzang-primary);

    .chip-name {
      color: white;
    }
  }
}

.chip-name {
  font-size: 26rpx;
  color: var(--gzang-text-secondary);
}

.record-actions {
  padding-top: 8rpx;
}
</style>
