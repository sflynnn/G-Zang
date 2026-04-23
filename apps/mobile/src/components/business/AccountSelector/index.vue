<template>
  <view class="account-selector">
    <view class="selector-trigger" @click="showPicker = true">
      <view class="trigger-left">
        <view
          class="account-icon"
          :style="{ backgroundColor: selectedAccount?.color || '#F5F5F5' }"
        >
          <uni-icons
            :type="getAccountIcon(selectedAccount?.type)"
            :size="18"
            color="white"
          />
        </view>
        <view class="trigger-info">
          <text class="trigger-name">{{ selectedAccount?.name || '请选择账户' }}</text>
          <text v-if="selectedAccount" class="trigger-balance amount">
            {{ formatAmount(selectedAccount?.balance) }}
          </text>
        </view>
      </view>
      <uni-icons type="right" size="16" color="#9CA3AF" />
    </view>

    <uni-popup ref="popupRef" type="bottom" :mask-click="true">
      <view class="picker-sheet">
        <view class="sheet-header">
          <text class="sheet-title">选择账户</text>
          <view class="sheet-close" @click="popupRef?.close()">
            <uni-icons type="closeempty" :size="20" color="#666" />
          </view>
        </view>

        <scroll-view scroll-y class="sheet-content">
          <view
            v-for="group in groupedAccounts"
            :key="group.type"
            class="account-group"
          >
            <view class="group-title">{{ group.label }}</view>
            <view class="group-items">
              <view
                v-for="account in group.accounts"
                :key="account.id"
                class="account-item"
                :class="{ 'account-item--selected': selectedId === account.id }"
                @click="handleSelect(account)"
              >
                <view
                  class="item-icon"
                  :style="{ backgroundColor: account.color || '#F5F5F5' }"
                >
                  <uni-icons
                    :type="getAccountIcon(account.type)"
                    :size="18"
                    color="white"
                  />
                </view>
                <view class="item-info">
                  <text class="item-name">{{ account.name }}</text>
                  <text class="item-type">{{ getAccountTypeName(account.type) }}</text>
                </view>
                <text class="item-balance amount">
                  {{ formatAmount(account.balance) }}
                </text>
                <uni-icons
                  v-if="selectedId === account.id"
                  type="checkmark"
                  :size="18"
                  color="var(--gzang-primary)"
                />
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { Account, AccountType } from '@/types/account'

interface Props {
  accounts: Account[]
  modelValue?: number
}

const props = withDefaults(defineProps<Props>(), {
  accounts: () => [],
  modelValue: undefined
})

const emit = defineEmits<{
  'update:modelValue': [id: number]
  'change': [account: Account]
}>()

const popupRef = ref<any>(null)
const showPicker = ref(false)
const selectedId = ref<number | undefined>(props.modelValue)

watch(() => props.modelValue, (val) => {
  selectedId.value = val
})

const selectedAccount = computed(() => {
  return props.accounts.find(a => a.id === selectedId.value)
})

const groupedAccounts = computed(() => {
  const groups: Record<string, Account[]> = {}

  for (const account of props.accounts) {
    const type = account.type || 'OTHER'
    if (!groups[type]) {
      groups[type] = []
    }
    groups[type].push(account)
  }

  const typeLabels: Record<string, string> = {
    CASH: '现金',
    BANK_CARD: '银行卡',
    CREDIT_CARD: '信用卡',
    E_WALLET: '电子钱包',
    INVESTMENT: '投资账户',
    DEBT: '债务',
    OTHER: '其他'
  }

  return Object.entries(groups).map(([type, accounts]) => ({
    type,
    label: typeLabels[type] || '其他',
    accounts
  }))
})

const formatAmount = (amount?: number): string => {
  return (amount || 0).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const getAccountIcon = (type?: AccountType | string): string => {
  const iconMap: Record<string, string> = {
    '1': 'wallet',
    '2': 'card',
    '3': 'cloud',
    '4': 'color',
    '5': 'cloud',
    '6': 'chartbars',
    '7': 'minus',
    '8': 'circle',
    CASH: 'wallet',
    BANK_CARD: 'card',
    CREDIT_CARD: 'color',
    E_WALLET: 'cloud',
    INVESTMENT: 'chartbars',
    DEBT: 'minus',
    OTHER: 'circle'
  }
  return iconMap[String(type || '')] || 'circle'
}

const getAccountTypeName = (type?: AccountType | string): string => {
  const nameMap: Record<string, string> = {
    '1': '现金账户',
    '2': '储蓄卡',
    '3': '电子支付',
    '4': '信用卡',
    '5': '电子钱包',
    '6': '投资账户',
    '7': '债务账户',
    '8': '其他',
    CASH: '现金账户',
    BANK_CARD: '储蓄卡',
    CREDIT_CARD: '信用卡',
    E_WALLET: '电子钱包',
    INVESTMENT: '投资账户',
    DEBT: '债务账户',
    OTHER: '其他'
  }
  return nameMap[String(type || '')] || '其他'
}

const handleSelect = (account: Account) => {
  selectedId.value = account.id
  emit('update:modelValue', account.id)
  emit('change', account)
  popupRef.value?.close()
}
</script>

<style scoped lang="scss">
.selector-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  background-color: var(--gzang-surface);
  border-radius: 16rpx;
  transition: background-color 0.15s;

  &:active {
    background-color: #f5f5f5;
  }
}

.trigger-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.account-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.trigger-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.trigger-name {
  font-size: 30rpx;
  color: var(--gzang-text-primary);
  font-weight: 500;
}

.trigger-balance {
  font-size: 24rpx;
  color: var(--gzang-text-secondary);
}

.picker-sheet {
  background-color: var(--gzang-surface);
  border-radius: 24rpx 24rpx 0 0;
  max-height: 70vh;
}

.sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid var(--gzang-border);
}

.sheet-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--gzang-text-primary);
}

.sheet-close {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sheet-content {
  max-height: 60vh;
  padding-bottom: env(safe-area-inset-bottom);
}

.account-group {
  padding: 0 24rpx;
}

.group-title {
  padding: 24rpx 0 12rpx;
  font-size: 26rpx;
  color: var(--gzang-text-secondary);
  font-weight: 500;
}

.group-items {
  display: flex;
  flex-direction: column;
}

.account-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid var(--gzang-border);
  transition: background-color 0.15s;

  &:last-child {
    border-bottom: none;
  }

  &--selected {
    background-color: rgba(15, 76, 92, 0.04);
  }

  &:active {
    opacity: 0.7;
  }
}

.item-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16rpx;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.item-name {
  font-size: 30rpx;
  color: var(--gzang-text-primary);
  font-weight: 500;
}

.item-type {
  font-size: 24rpx;
  color: var(--gzang-text-secondary);
}

.item-balance {
  font-size: 28rpx;
  color: var(--gzang-text-primary);
  margin-right: 12rpx;
}
</style>
