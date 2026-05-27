<script setup lang="ts">
import { ref, computed } from 'vue'
import SelectorSheet from '../SelectorSheet/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import { currencies } from '@/data/currencies'
import type { CurrencyItem } from '@/data/currencies'

interface Props {
  show: boolean
  value?: string | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:show': [value: boolean]
  change: [currency: CurrencyItem]
}>()

const searchKeyword = ref('')

const filteredCurrencies = computed(() => {
  if (!searchKeyword.value.trim()) return currencies
  const kw = searchKeyword.value.toLowerCase()
  return currencies.filter(c =>
    c.code.toLowerCase().includes(kw) ||
    c.name.includes(kw) ||
    c.nameEn.toLowerCase().includes(kw) ||
    c.symbol.toLowerCase().includes(kw)
  )
})

function selectCurrency(currency: CurrencyItem) {
  emit('change', currency)
  emit('update:show', false)
}

function isSelected(currency: CurrencyItem): boolean {
  return props.value === currency.code
}
</script>

<template>
  <SelectorSheet
    :show="show"
    title="选择货币"
    height="65vh"
    @update:show="$emit('update:show', $event)"
  >
    <view class="currency-selector">
      <!-- Search -->
      <view class="search-bar">
        <AppleIcon name="search" :size="18" color="#9CA3AF" />
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="搜索货币名称或代码"
          placeholder-class="search-placeholder"
        />
        <view
          v-if="searchKeyword"
          class="search-clear"
          @click="searchKeyword = ''"
        >
          <AppleIcon name="close" :size="16" color="#9CA3AF" />
        </view>
      </view>

      <!-- Currency list -->
      <scroll-view
        class="currency-list"
        scroll-y="true"
        :enhanced="true"
        :bounces="true"
      >
        <view
          v-for="currency in filteredCurrencies"
          :key="currency.code"
          class="currency-item"
          :class="{ selected: isSelected(currency) }"
          @click="selectCurrency(currency)"
        >
          <!-- Symbol -->
          <view class="currency-symbol">{{ currency.symbol }}</view>

          <!-- Info -->
          <view class="currency-info">
            <text class="currency-name">{{ currency.name }}</text>
            <text class="currency-code">{{ currency.code }} · {{ currency.nameEn }}</text>
          </view>

          <!-- Check -->
          <AppleIcon
            v-if="isSelected(currency)"
            name="check"
            :size="20"
            color="#0F4C5C"
          />
        </view>

        <!-- Empty -->
        <view v-if="filteredCurrencies.length === 0" class="empty-state">
          <AppleIcon name="search" :size="40" color="#D1D5DB" />
          <text class="empty-text">未找到相关货币</text>
        </view>
      </scroll-view>
    </view>
  </SelectorSheet>
</template>

<style lang="scss" scoped>
.currency-selector {
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

.currency-list {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.currency-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx;
  border-bottom: 1rpx solid #F9FAFB;
  transition: background 0.15s;

  &:last-child {
    border-bottom: none;
  }

  &.selected {
    background: rgba(15, 76, 92, 0.04);
  }
}

.currency-symbol {
  width: 80rpx;
  height: 80rpx;
  background: rgba(15, 76, 92, 0.08);
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  font-weight: 700;
  color: #0F4C5C;
  flex-shrink: 0;
}

.currency-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.currency-name {
  font-size: 32rpx;
  color: #1F2937;
  font-weight: 500;
}

.currency-code {
  font-size: 24rpx;
  color: #9CA3AF;
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
