<template>
  <view class="sub-category-grid" :class="{ compact }">
    <view 
      v-for="(category, index) in categories" 
      :key="category.id"
      class="sub-category-item"
      :class="{ selected: modelValue === category.id }"
      :style="{ animationDelay: `${index * 30}ms` }"
      @click="handleSelect(category)"
    >
      <view 
        class="sub-icon-wrapper"
        :style="{ background: getIconBg(category.color) }"
      >
        <text class="sub-icon">{{ category.icon }}</text>
      </view>
      <text class="sub-name" :class="{ active: modelValue === category.id }">
        {{ category.name }}
      </text>
      <!-- 选中指示器 -->
      <view v-if="modelValue === category.id" class="selected-indicator">
        <text class="check-icon">✓</text>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="categories.length === 0" class="empty-state">
      <text class="empty-text">暂无子分类</text>
    </view>
  </view>
</template>

<script setup lang="ts">
export interface SubCategory {
  id: number
  name: string
  icon: string
  color: string
  parentId?: number
}

interface Props {
  modelValue?: number
  categories: SubCategory[]
  compact?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: undefined,
  categories: () => [],
  compact: false
})

const emit = defineEmits<{
  'update:modelValue': [id: number]
  'change': [category: SubCategory]
}>()

const getIconBg = (color: string) => {
  return `${color}20`
}

const handleSelect = (category: SubCategory) => {
  emit('update:modelValue', category.id)
  emit('change', category)
}
</script>

<style lang="scss" scoped>
.sub-category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
  padding: 8rpx 0;

  &.compact {
    gap: 16rpx;
  }
}

.sub-category-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 8rpx;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  animation: fadeInUp 0.3s ease-out both;

  &.selected {
    background: linear-gradient(135deg, var(--gzang-secondary) 0%, #ff9a3c 100%);
    box-shadow: 0 8rpx 24rpx rgba(251, 139, 36, 0.3);
    transform: scale(1.05);

    .sub-name {
      color: white;
      font-weight: var(--apple-font-semibold);
    }

    .sub-icon-wrapper {
      background: rgba(255, 255, 255, 0.3) !important;
    }
  }

  &:active {
    transform: scale(0.95);
  }
}

.compact .sub-category-item {
  padding: 16rpx 4rpx;
}

.sub-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  border-radius: var(--apple-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
  transition: background var(--apple-duration-fast);
}

.compact .sub-icon-wrapper {
  width: 64rpx;
  height: 64rpx;
  margin-bottom: 8rpx;
}

.sub-icon {
  font-size: 40rpx;
  line-height: 1;
}

.compact .sub-icon {
  font-size: 32rpx;
}

.sub-name {
  font-size: var(--apple-text-xs);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
  text-align: center;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.selected-indicator {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  width: 36rpx;
  height: 36rpx;
  background: var(--gzang-success);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 8rpx rgba(6, 214, 160, 0.3);
}

.check-icon {
  font-size: 20rpx;
  color: white;
  font-weight: bold;
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60rpx 0;
}

.empty-text {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-tertiary);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
