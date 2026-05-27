<script setup lang="ts">
import { ref, computed } from 'vue'
import SelectorSheet from '../SelectorSheet/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import {
  accountIconsByCategory,
  categoryLabels,
  searchIcons,
} from '@/data/account-icons'
import type { AccountIcon, AccountIconCategory } from '@/data/account-icons'

interface Props {
  show: boolean
  value?: string | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:show': [value: boolean]
  change: [iconId: string, icon: AccountIcon]
}>()

const activeCategory = ref<AccountIconCategory | 'all'>('all')
const searchKeyword = ref('')

const displayIcons = computed(() => {
  if (searchKeyword.value.trim()) {
    return searchIcons(searchKeyword.value)
  }
  if (activeCategory.value === 'all') {
    return Object.values(accountIconsByCategory).flat()
  }
  return accountIconsByCategory[activeCategory.value] || []
})

const categories = computed(() => {
  const cats: Array<{ key: AccountIconCategory | 'all'; label: string }> = [
    { key: 'all', label: '全部' },
  ]
  ;(Object.keys(categoryLabels) as AccountIconCategory[]).forEach(key => {
    cats.push({ key, label: categoryLabels[key] })
  })
  return cats
})

function selectIcon(icon: AccountIcon) {
  emit('change', icon.id, icon)
  emit('update:show', false)
}

function isSelected(icon: AccountIcon): boolean {
  return props.value === icon.id
}
</script>

<template>
  <SelectorSheet
    :show="show"
    title="选择图标"
    height="75vh"
    @update:show="$emit('update:show', $event)"
  >
    <view class="icon-selector">
      <!-- Search bar -->
      <view class="search-bar">
        <AppleIcon name="search" :size="18" color="#9CA3AF" />
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="搜索图标"
          placeholder-class="search-placeholder"
          @confirm="() => {}"
        />
        <view
          v-if="searchKeyword"
          class="search-clear"
          @click="searchKeyword = ''"
        >
          <AppleIcon name="close" :size="16" color="#9CA3AF" />
        </view>
      </view>

      <!-- Category tabs -->
      <scroll-view
        class="category-tabs"
        scroll-x="true"
        :enhanced="true"
        :bounces="false"
      >
        <view class="tabs-inner">
          <view
            v-for="cat in categories"
            :key="cat.key"
            class="tab-item"
            :class="{ active: activeCategory === cat.key }"
            @click="activeCategory = cat.key; searchKeyword = ''"
          >
            {{ cat.label }}
          </view>
        </view>
      </scroll-view>

      <!-- Icon grid -->
      <scroll-view
        class="icon-grid-wrapper"
        scroll-y="true"
        :enhanced="true"
        :bounces="true"
      >
        <view class="icon-grid">
          <view
            v-for="icon in displayIcons"
            :key="icon.id"
            class="icon-cell"
            :class="{ selected: isSelected(icon) }"
            @click="selectIcon(icon)"
          >
            <!-- AppleIcon (stroke) -->
            <AppleIcon
              v-if="icon.fillType === 'stroke'"
              :name="icon.src"
              :size="28"
              :color="isSelected(icon) ? '#fff' : 'var(--gzang-primary, #0F4C5C)'"
            />
            <!-- SVG image (solid) -->
            <image
              v-else-if="icon.src.startsWith('/static/')"
              :src="icon.src"
              class="icon-img"
              mode="aspectFit"
            />
            <text v-else class="icon-fallback">{{ icon.id.charAt(0).toUpperCase() }}</text>

            <!-- Check mark -->
            <view v-if="isSelected(icon)" class="check-mark">
              <AppleIcon name="check" :size="12" color="#fff" />
            </view>
          </view>
        </view>

        <!-- Empty -->
        <view v-if="displayIcons.length === 0" class="empty-state">
          <AppleIcon name="search" :size="40" color="#D1D5DB" />
          <text class="empty-text">未找到相关图标</text>
        </view>
      </scroll-view>
    </view>
  </SelectorSheet>
</template>

<style lang="scss" scoped>
.icon-selector {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin: 0 24rpx 16rpx;
  padding: 16rpx 20rpx;
  background: #F3F4F6;
  border-radius: 16rpx;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #374151;
  background: transparent;
  border: none;
  outline: none;
}

.search-placeholder {
  color: #9CA3AF;
}

.search-clear {
  padding: 4rpx;
}

.category-tabs {
  flex-shrink: 0;
  padding: 0 16rpx;
}

.tabs-inner {
  display: flex;
  gap: 12rpx;
  padding: 8rpx 8rpx 16rpx;
  white-space: nowrap;
}

.tab-item {
  display: inline-flex;
  align-items: center;
  padding: 10rpx 24rpx;
  font-size: 26rpx;
  color: #6B7280;
  background: #F3F4F6;
  border-radius: 100rpx;
  white-space: nowrap;
  transition: all 0.2s;
  flex-shrink: 0;

  &.active {
    background: #0F4C5C;
    color: #fff;
    font-weight: 600;
  }
}

.icon-grid-wrapper {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16rpx;
  padding: 0 24rpx 24rpx;
}

.icon-cell {
  aspect-ratio: 1;
  background: #F9FAFB;
  border-radius: 16rpx;
  border: 4rpx solid transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: all 0.2s;

  &.selected {
    border-color: #0F4C5C;
    background: #0F4C5C;
  }
}

.icon-img {
  width: 52rpx;
  height: 52rpx;
}

.icon-fallback {
  font-size: 28rpx;
  font-weight: 600;
  color: #9CA3AF;
}

.check-mark {
  position: absolute;
  top: -6rpx;
  right: -6rpx;
  width: 32rpx;
  height: 32rpx;
  border-radius: 50%;
  background: #0F4C5C;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 6rpx rgba(15, 76, 92, 0.3);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  padding: 100rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #9CA3AF;
}
</style>
