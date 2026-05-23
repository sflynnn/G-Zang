<template>
  <view class="type-selector">
    <view 
      v-for="type in types" 
      :key="type.key"
      class="type-item"
      :class="{ 
        active: modelValue === type.key,
        [`type-${type.key}`]: true 
      }"
      @click="handleSelect(type.key)"
    >
      <view class="type-icon">
        <AppleIcon :name="type.icon" :size="18" />
      </view>
      <text class="type-label">{{ t(`accounting.${type.i18nKey}`) }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

interface TransactionType {
  key: 1 | 2 | 3
  icon: string
  i18nKey: string
}

interface Props {
  modelValue: 1 | 2 | 3
}

defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: 1 | 2 | 3]
  'change': [value: 1 | 2 | 3]
}>()

const { t } = useI18n()

const types: TransactionType[] = [
  { key: 2, icon: 'expense', i18nKey: 'expense' },
  { key: 1, icon: 'income', i18nKey: 'income' },
  { key: 3, icon: 'transfer', i18nKey: 'transfer' }
]

const handleSelect = (key: 1 | 2 | 3) => {
  emit('update:modelValue', key)
  emit('change', key)
}
</script>

<style lang="scss" scoped>
.type-selector {
  display: flex;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: 6rpx;
  gap: 6rpx;
  box-shadow: var(--apple-shadow-sm);
}

.type-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 20rpx 16rpx;
  border-radius: var(--apple-radius-lg);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  .type-icon {
    color: var(--gzang-text-secondary);
    transition: color var(--apple-duration-fast) var(--apple-ease-out);
  }
  
  .type-label {
    font-size: var(--apple-text-sm);
    font-weight: var(--apple-font-medium);
    color: var(--gzang-text-secondary);
    transition: color var(--apple-duration-fast) var(--apple-ease-out);
  }
  
  // 支出 - 支出红
  &.type-2 {
    &.active {
      background: var(--gzang-danger);
      box-shadow: 0 4rpx 12rpx rgba(239, 71, 111, 0.3);
      
      .type-icon,
      .type-label {
        color: white;
      }
    }
  }
  
  // 收入 - 利润绿
  &.type-1 {
    &.active {
      background: var(--gzang-success);
      box-shadow: 0 4rpx 12rpx rgba(6, 214, 160, 0.3);
      
      .type-icon,
      .type-label {
        color: white;
      }
    }
  }
  
  // 转账 - 归藏青
  &.type-3 {
    &.active {
      background: var(--gzang-primary);
      box-shadow: 0 4rpx 12rpx rgba(15, 76, 92, 0.3);
      
      .type-icon,
      .type-label {
        color: white;
      }
    }
  }
  
  &:active {
    transform: scale(0.98);
  }
}
</style>
