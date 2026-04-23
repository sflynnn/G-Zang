<template>
  <view class="accounts-page apple-style">
    <!-- Large Title Navigation -->
    <view class="nav-large-title">
      <view class="nav-header">
        <text class="nav-title">{{ t('account.title') }}</text>
        <view class="nav-add" @click="goToCreate">
          <AppleIcon name="plus" :size="20" color="var(--gzang-primary)" />
        </view>
      </view>
    </view>

    <scroll-view class="main-content" scroll-y="true">
      <!-- Net Assets Card -->
      <view class="net-assets-card">
        <view class="card-header">
          <text class="card-label">{{ t('account.totalBalance') }}</text>
          <view class="card-trend" :class="trend >= 0 ? 'trend-up' : 'trend-down'">
            <AppleIcon :name="trend >= 0 ? 'trend-up' : 'trend-down'" :size="12" :color="trend >= 0 ? 'var(--gzang-success)' : 'var(--gzang-danger)'" />
            <text>{{ Math.abs(trendPercent) }}%</text>
          </view>
        </view>
        <view class="card-amount">
          <text class="currency">{{ currencySymbol }}</text>
          <text class="amount-value amount">{{ formatAmount(netAssets) }}</text>
        </view>
        <view class="card-sub">
          <view class="sub-item">
            <text class="sub-label">资产</text>
            <text class="sub-value income">{{ currencySymbol }}{{ formatAmount(totalAssets) }}</text>
          </view>
          <view class="sub-divider"></view>
          <view class="sub-item">
            <text class="sub-label">负债</text>
            <text class="sub-value expense">{{ currencySymbol }}{{ formatAmount(totalLiabilities) }}</text>
          </view>
        </view>
      </view>

      <!-- Account Groups -->
      <view v-for="group in accountGroups" :key="group.type" class="account-group">
        <view class="group-header">
          <text class="group-title">{{ group.label }}</text>
          <text class="group-total">{{ currencySymbol }}{{ formatAmount(group.total) }}</text>
        </view>
        <view class="group-list">
          <view 
            v-for="account in group.accounts" 
            :key="account.id"
            class="account-item"
            @click="goToDetail(account)"
          >
            <view class="account-icon" :style="{ background: getAccountColor(account.type) + '20' }">
              <AppleIcon :name="getAccountIcon(account.type)" :size="20" :color="getAccountColor(account.type)" />
            </view>
            <view class="account-info">
              <text class="account-name">{{ account.name }}</text>
              <text class="account-type">{{ getAccountTypeName(account.type) }}</text>
            </view>
            <text class="account-balance amount" :class="getBalanceClass(account)">
              {{ formatBalance(account) }}
            </text>
          </view>
        </view>
      </view>

      <!-- Empty State -->
      <view v-if="accounts.length === 0 && !loading" class="empty-state">
        <view class="empty-icon-wrapper">
          <AppleIcon name="wallet" :size="48" color="var(--gzang-text-tertiary)" />
        </view>
        <text class="empty-title">还没有账户</text>
        <text class="empty-desc">添加你的第一个账户开始管理资金</text>
        <button class="create-btn" @click="goToCreate">
          <AppleIcon name="plus" :size="16" color="white" />
          <text>添加账户</text>
        </button>
      </view>

      <!-- Add Account Button -->
      <view v-if="accounts.length > 0" class="add-account-btn" @click="goToCreate">
        <AppleIcon name="plus" :size="18" color="var(--gzang-primary)" />
        <text class="btn-text">添加账户</text>
      </view>

      <view class="bottom-safe-area"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAccountingStore } from '@/stores/accounting'
import { useBookStore } from '@/stores/book'
import type { Account } from '@/types/account'
import { AccountType } from '@/types/account'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { t } = useI18n()

const accountingStore = useAccountingStore()
const bookStore = useBookStore()

const loading = ref(false)

const accounts = computed(() => accountingStore.accounts)
const currencySymbol = computed(() => bookStore.currentCurrencySymbol)

const totalAssets = computed(() =>
  accounts.value
    .filter(a => a.balance > 0 && a.type !== AccountType.CreditCard && a.type !== AccountType.Debt)
    .reduce((sum, a) => sum + a.balance, 0)
)

const totalLiabilities = computed(() =>
  accounts.value
    .filter(a => a.balance < 0 || a.type === AccountType.CreditCard || a.type === AccountType.Debt)
    .reduce((sum, a) => sum + Math.abs(a.balance), 0)
)

const netAssets = computed(() => totalAssets.value - totalLiabilities.value)

const trend = computed(() => 5.2)
const trendPercent = computed(() => Math.abs(trend.value))

const accountGroups = computed(() => {
  const groups: Array<{ type: string; label: string; total: number; accounts: Account[] }> = []

  const assetAccounts = accounts.value.filter(a =>
    a.type === AccountType.Cash ||
    a.type === AccountType.BankCard ||
    a.type === AccountType.EWallet ||
    a.type === AccountType.Investment
  )
  if (assetAccounts.length > 0) {
    groups.push({
      type: 'asset',
      label: '资产',
      total: assetAccounts.reduce((sum, a) => sum + a.balance, 0),
      accounts: assetAccounts,
    })
  }

  const liabilityAccounts = accounts.value.filter(a =>
    a.type === AccountType.CreditCard ||
    a.type === AccountType.Debt
  )
  if (liabilityAccounts.length > 0) {
    groups.push({
      type: 'liability',
      label: '负债',
      total: liabilityAccounts.reduce((sum, a) => sum + Math.abs(a.balance), 0),
      accounts: liabilityAccounts,
    })
  }

  return groups
})

const formatAmount = (amount: number) => {
  return Math.abs(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const formatBalance = (account: Account) => {
  const prefix = account.balance >= 0 ? '' : '-'
  return `${prefix}${currencySymbol.value}${formatAmount(account.balance)}`
}

const getBalanceClass = (account: Account) => {
  if (account.balance < 0) return 'negative'
  return 'positive'
}

const getAccountIcon = (type: string) => {
  const iconMap: Record<string, string> = {
    [AccountType.Cash]: 'wallet',
    [AccountType.BankCard]: 'bank',
    [AccountType.CreditCard]: 'credit-card',
    [AccountType.EWallet]: 'wallet',
    [AccountType.Investment]: 'chart',
    [AccountType.Debt]: 'flag',
    [AccountType.Other]: 'book',
  }
  return iconMap[type] || 'wallet'
}

const getAccountColor = (type: string) => {
  const colorMap: Record<string, string> = {
    [AccountType.Cash]: '#06D6A0',
    [AccountType.BankCard]: '#0F4C5C',
    [AccountType.CreditCard]: '#EF476F',
    [AccountType.EWallet]: '#FB8B24',
    [AccountType.Investment]: '#118AB2',
    [AccountType.Debt]: '#EF476F',
    [AccountType.Other]: '#6B7280',
  }
  return colorMap[type] || '#6B7280'
}

const getAccountTypeName = (type: string) => {
  const nameMap: Record<string, string> = {
    [AccountType.Cash]: '现金',
    [AccountType.BankCard]: '银行卡',
    [AccountType.CreditCard]: '信用卡',
    [AccountType.EWallet]: '电子钱包',
    [AccountType.Investment]: '投资账户',
    [AccountType.Debt]: '债务',
    [AccountType.Other]: '其他',
  }
  return nameMap[type] || '其他'
}

const goToCreate = () => uni.navigateTo({ url: '/pages/accounts/create' })
const goToDetail = (account: Account) => uni.navigateTo({ url: `/pages/accounts/detail?id=${account.id}` })

const mockAccounts: Account[] = [
  { id: 1, name: '建设银行', type: AccountType.BankCard, balance: 12500.00, currency: 'CNY', isActive: true, createTime: '2024-01-01T00:00:00Z' },
  { id: 2, name: '支付宝', type: AccountType.EWallet, balance: 3200.00, currency: 'CNY', isActive: true, createTime: '2024-01-01T00:00:00Z' },
  { id: 3, name: '现金', type: AccountType.Cash, balance: 800.00, currency: 'CNY', isActive: true, createTime: '2024-01-01T00:00:00Z' },
  { id: 4, name: '招商信用卡', type: AccountType.CreditCard, balance: -2500.00, currency: 'CNY', isActive: true, createTime: '2024-01-01T00:00:00Z' },
]

onMounted(async () => {
  loading.value = true
  accountingStore.setAccounts(mockAccounts)
  loading.value = false
})
</script>

<style lang="scss" scoped>
.accounts-page {
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
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-3);
}

.nav-title {
  font-size: var(--apple-text-4xl);
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
}

.nav-add {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--apple-shadow-xs);
}

.main-content {
  padding: 0 var(--apple-space-4);
}

.net-assets-card {
  background: linear-gradient(145deg, var(--gzang-primary) 0%, var(--gzang-primary-light) 100%);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-5);
  margin-bottom: var(--apple-space-5);
  color: white;
  box-shadow: 0 8px 32px rgba(15, 76, 92, 0.25);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-3);
}

.card-label {
  font-size: var(--apple-text-sm);
  opacity: 0.8;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-xs);
  font-weight: var(--apple-font-semibold);
  
  &.trend-up { background: rgba(6, 214, 160, 0.2); }
  &.trend-down { background: rgba(239, 71, 111, 0.2); }
}

.card-amount {
  display: flex;
  align-items: baseline;
  margin-bottom: var(--apple-space-4);
}

.currency {
  font-size: var(--apple-text-xl);
  font-weight: var(--apple-font-semibold);
  margin-right: 4px;
}

.amount-value {
  font-size: 36px;
  font-weight: var(--apple-font-bold);
  letter-spacing: -1px;
}

.card-sub {
  display: flex;
  align-items: center;
  padding-top: var(--apple-space-4);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.sub-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.sub-label {
  font-size: var(--apple-text-xs);
  opacity: 0.7;
  margin-bottom: 4px;
}

.sub-value {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  
  &.income { color: var(--gzang-success); }
  &.expense { color: rgba(255, 209, 102, 1); }
}

.sub-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
}

.account-group {
  margin-bottom: var(--apple-space-5);
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-3);
  padding: 0 var(--apple-space-2);
}

.group-title {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.group-total {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-primary);
}

.group-list {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  overflow: hidden;
  box-shadow: var(--apple-shadow-sm);
}

.account-item {
  display: flex;
  align-items: center;
  padding: var(--apple-space-4);
  border-bottom: 0.5px solid var(--gzang-border);
  transition: background-color var(--apple-duration-fast);
  
  &:last-child { border-bottom: none; }
  &:active { background: var(--gzang-bg); }
}

.account-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--apple-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--apple-space-3);
}

.account-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.account-name {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
  margin-bottom: 2px;
}

.account-type {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-secondary);
}

.account-balance {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  
  &.positive { color: var(--gzang-text-primary); }
  &.negative { color: var(--gzang-danger); }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--apple-space-10) var(--apple-space-4);
}

.empty-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--gzang-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--apple-space-4);
}

.empty-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
  margin-bottom: var(--apple-space-2);
}

.empty-desc {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  margin-bottom: var(--apple-space-5);
}

.create-btn {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-3) var(--apple-space-5);
  background: var(--gzang-primary);
  color: white;
  border: none;
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  box-shadow: 0 4px 12px rgba(15, 76, 92, 0.3);
}

.add-account-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-4);
  background: var(--gzang-surface);
  border: 1.5px dashed var(--gzang-primary);
  border-radius: var(--apple-radius-xl);
  margin-top: var(--apple-space-4);
  transition: all var(--apple-duration-fast);
  
  &:active { background: rgba(15, 76, 92, 0.05); }
}

.btn-text {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-primary);
}

.bottom-safe-area {
  height: var(--apple-space-4);
}
</style>
