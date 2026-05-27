<script setup lang="ts">
import SelectorSheet from '../SelectorSheet/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import type { AccountType } from '@/types/account'

interface Props {
  show: boolean
  value?: AccountType | number | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:show': [value: boolean]
  change: [type: AccountType | number]
}>()

const accountTypeGroups = [
  {
    label: '资金账户',
    types: [
      { value: 1, name: '现金', icon: 'wallet' },
      { value: 2, name: '银行卡', icon: 'bank' },
      { value: 5, name: '电子钱包', icon: 'wallet' },
    ],
  },
  {
    label: '信用账户',
    types: [
      { value: 4, name: '信用卡', icon: 'credit-card' },
    ],
  },
  {
    label: '投资账户',
    types: [
      { value: 6, name: '投资账户', icon: 'chart' },
    ],
  },
  {
    label: '应收/应付',
    types: [
      { value: 7, name: '借款/债务', icon: 'flag' },
    ],
  },
  {
    label: '充值账户',
    types: [
      { value: 8, name: '其他账户', icon: 'plus' },
    ],
  },
]

function selectType(type: AccountType | number) {
  emit('change', type)
  emit('update:show', false)
}

function isSelected(type: AccountType | number): boolean {
  return props.value === type
}
</script>

<template>
  <SelectorSheet
    :show="show"
    title="选择账户类型"
    :height="'65vh'"
    @update:show="$emit('update:show', $event)"
  >
    <view class="type-selector">
      <view
        v-for="group in accountTypeGroups"
        :key="group.label"
        class="type-group"
      >
        <view class="group-label">{{ group.label }}</view>
        <view class="group-items">
          <view
            v-for="item in group.types"
            :key="item.name"
            class="type-item"
            :class="{ selected: isSelected(item.value) }"
            @click="selectType(item.value)"
          >
            <view
              class="item-icon"
              :class="{ selected: isSelected(item.value) }"
            >
              <AppleIcon
                :name="item.icon"
                :size="28"
                :color="isSelected(item.value) ? '#fff' : 'var(--gzang-primary, #0F4C5C)'"
              />
            </view>
            <text class="item-name">{{ item.name }}</text>
            <view v-if="isSelected(item.value)" class="check-mark">
              <AppleIcon name="check" :size="14" color="#fff" />
            </view>
          </view>
        </view>
      </view>
    </view>
  </SelectorSheet>
</template>

<style lang="scss" scoped>
.type-selector {
  padding: 24rpx;
}

.type-group {
  margin-bottom: 32rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.group-label {
  font-size: 24rpx;
  color: #9CA3AF;
  font-weight: 500;
  margin-bottom: 16rpx;
  padding-left: 8rpx;
  letter-spacing: 1rpx;
}

.group-items {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 20rpx;
  background: #F9FAFB;
  border-radius: 20rpx;
  border: 4rpx solid transparent;
  min-width: 160rpx;
  flex: 1;
  position: relative;
  transition: all 0.2s;

  &.selected {
    border-color: #0F4C5C;
    background: rgba(15, 76, 92, 0.06);
  }
}

.item-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: rgba(15, 76, 92, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;

  &.selected {
    background: #0F4C5C;
  }
}

.item-name {
  font-size: 26rpx;
  color: #374151;
  font-weight: 500;
  text-align: center;
}

.check-mark {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: #0F4C5C;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 8rpx rgba(15, 76, 92, 0.3);
}
</style>
