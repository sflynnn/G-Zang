<template>
  <PageTransition>
    <!-- 自定义组件 -->
    <CustomToast />
    <CustomModal />
    <CustomLoading />

    <view class="create-account-page">
    <!-- Step indicator -->
    <view class="step-indicator">
      <view class="step-line">
        <view
          class="step-progress"
          :style="{ width: (currentStep / (steps.length - 1) * 100) + '%' }"
        ></view>
      </view>
      <view
        v-for="(step, index) in steps"
        :key="index"
        class="step-dot"
        :class="{
          active: currentStep === index,
          completed: currentStep > index,
        }"
        @click="currentStep > index && (currentStep = index)"
      >
        <view v-if="currentStep > index" class="dot-check">
          <AppleIcon name="check" :size="10" color="#fff" />
        </view>
        <text v-else class="dot-number">{{ index + 1 }}</text>
      </view>
    </view>

    <!-- Step 1: Select account type -->
    <view v-if="currentStep === 0" class="step-content">
      <view class="step-header">
        <text class="step-title">选择账户类型</text>
        <text class="step-desc">选择你要创建的账户类型</text>
      </view>

      <view class="type-list">
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
              :class="{ selected: formData.type === item.value }"
              @click="selectType(item)"
            >
              <view class="item-icon">
                <AppleIcon
                  :name="item.icon"
                  :size="28"
                  :color="formData.type === item.value ? '#fff' : 'var(--gzang-primary, #0F4C5C)'"
                />
              </view>
              <text class="item-name">{{ item.name }}</text>
              <view v-if="formData.type === item.value" class="item-check">
                <AppleIcon name="check" :size="14" color="#fff" />
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- Step 2: Select bank or card brand -->
    <view v-if="currentStep === 1" class="step-content">
      <view class="step-header">
        <text class="step-title">
          {{ needsBankSelection ? '选择银行' : '选择卡品牌' }}
        </text>
        <text class="step-desc">
          {{ needsBankSelection ? '选择您的开户银行' : '选择您的信用卡品牌' }}
        </text>
      </view>

      <!-- Bank selection -->
      <view v-if="needsBankSelection" class="bank-list">
        <view
          v-for="bank in displayedBanks"
          :key="bank.code"
          class="bank-item"
          :class="{ selected: formData.bankCode === bank.code }"
          @click="selectBank(bank)"
        >
          <view class="bank-icon" :style="{ background: (bank.color || '#0F4C5C') + '20' }">
            <image v-if="bank.iconPath" :src="bank.iconPath" class="bank-svg" mode="aspectFit" />
            <text v-else class="bank-initial" :style="{ color: bank.color || '#0F4C5C' }">
              {{ bank.name.charAt(0) }}
            </text>
          </view>
          <text class="bank-name">{{ bank.name }}</text>
          <AppleIcon
            v-if="formData.bankCode === bank.code"
            name="check"
            :size="18"
            color="#0F4C5C"
          />
        </view>
      </view>

      <!-- Card brand selection -->
      <view v-if="needsCardBrandSelection" class="brand-grid">
        <view
          v-for="brand in creditCardBrands"
          :key="brand.code"
          class="brand-item"
          :class="{ selected: formData.cardBrand === brand.code }"
          @click="selectCardBrand(brand)"
        >
          <view
            class="brand-logo"
            :style="{ background: (brand.color || '#0F4C5C') + '15', borderColor: formData.cardBrand === brand.code ? (brand.color || '#0F4C5C') : 'transparent' }"
          >
            <text
              v-if="!brand.iconPath"
              class="brand-initial"
              :style="{ color: brand.color || '#0F4C5C' }"
            >
              {{ brand.code.charAt(0) }}
            </text>
            <image v-else :src="brand.iconPath" class="brand-svg" mode="aspectFit" />
          </view>
          <text class="brand-name">{{ brand.name }}</text>
        </view>
      </view>
    </view>

    <!-- Step 3: Account details form -->
    <view v-if="currentStep === 2" class="step-content">
      <view class="step-header">
        <text class="step-title">账户信息</text>
        <text class="step-desc">完善账户详情</text>
      </view>

      <view class="form-container">
        <!-- Icon selector -->
        <view class="form-row icon-row" @click="showIconSelector = true">
          <text class="row-label">图标</text>
          <view class="icon-preview">
            <AppleIcon
              v-if="formData.icon && isAppleIcon(formData.icon)"
              :name="formData.icon"
              :size="32"
              color="#0F4C5C"
            />
            <image
              v-else-if="formData.icon"
              :src="formData.icon"
              class="icon-img"
              mode="aspectFit"
            />
            <view v-else class="icon-placeholder">
              <AppleIcon name="edit" :size="20" color="#9CA3AF" />
            </view>
          </view>
          <AppleIcon name="chevron-right" :size="16" color="#D1D5DB" />
        </view>

        <!-- Name -->
        <view class="form-row">
          <text class="row-label">账户名称 <text class="required">*</text></text>
          <input
            v-model="formData.name"
            class="row-input"
            :placeholder="t('account.namePlaceholder')"
            maxlength="20"
          />
        </view>

        <!-- Type (readonly display) -->
        <view class="form-row readonly">
          <text class="row-label">账户类型</text>
          <view class="row-value">{{ selectedTypeName }}</view>
        </view>

        <!-- Bank (if applicable) -->
        <view v-if="needsBankSelection" class="form-row readonly">
          <text class="row-label">开户银行</text>
          <view class="row-value">{{ formData.bankName || '未选择' }}</view>
        </view>

        <!-- Card brand (if applicable) -->
        <view v-if="needsCardBrandSelection" class="form-row readonly">
          <text class="row-label">卡品牌</text>
          <view class="row-value">{{ selectedCardBrandName || '未选择' }}</view>
        </view>

        <!-- Card number -->
        <view v-if="needsCardSelection" class="form-row">
          <text class="row-label">卡号后4位</text>
          <input
            v-model="formData.cardNumber"
            class="row-input"
            :placeholder="t('account.cardNumberPlaceholder')"
            maxlength="4"
            type="number"
          />
        </view>

        <!-- Currency -->
        <view class="form-row" @click="showCurrencySelector = true">
          <text class="row-label">货币</text>
          <view class="row-value-selector">
            <text class="row-value">{{ selectedCurrencyDisplay }}</text>
            <AppleIcon name="chevron-right" :size="16" color="#D1D5DB" />
          </view>
        </view>

        <!-- Initial balance -->
        <view class="form-row">
          <text class="row-label">{{ t('account.initialBalance') }}</text>
          <view class="amount-input-wrapper">
            <text class="currency-symbol">{{ currentCurrencySymbol }}</text>
            <input
              v-model="initialBalanceStr"
              class="row-input amount-input"
              type="digit"
              placeholder="0.00"
            />
          </view>
        </view>

        <!-- Remark -->
        <view class="form-row remark-row">
          <text class="row-label">备注</text>
          <textarea
            v-model="formData.remark"
            class="row-textarea"
            :placeholder="t('account.remarkPlaceholder')"
            maxlength="200"
            rows="3"
          />
        </view>
      </view>
    </view>

    <!-- Navigation buttons -->
    <view class="nav-bar">
      <button
        v-if="currentStep > 0"
        class="nav-btn nav-btn-back"
        @click="currentStep--"
      >
        <AppleIcon name="chevron-left" :size="18" color="#0F4C5C" />
        <text>上一步</text>
      </button>
      <view v-else class="nav-btn-spacer"></view>

      <button
        v-if="currentStep < steps.length - 1"
        class="nav-btn nav-btn-next"
        :disabled="!canProceed"
        @click="currentStep++"
      >
        <text>下一步</text>
        <AppleIcon name="chevron-right" :size="18" color="#fff" />
      </button>

      <button
        v-else
        class="nav-btn nav-btn-submit"
        :disabled="submitting || !formData.name?.trim()"
        @click="handleSubmit"
      >
        <text>{{ submitting ? '创建中...' : '创建账户' }}</text>
      </button>
    </view>

    <!-- Selector modals -->
    <AccountIconSelector
      v-model:show="showIconSelector"
      :value="formData.icon || null"
      @change="onIconChange"
    />

    <CurrencySelector
      v-model:show="showCurrencySelector"
      :value="formData.currency || null"
      @change="onCurrencyChange"
    />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAccountStore } from '@/stores/account'
import { useBookStore } from '@/stores/book'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import CustomToast from '@/components/common/CustomToast/index.vue'
import CustomModal from '@/components/common/CustomModal/index.vue'
import CustomLoading from '@/components/common/CustomLoading/index.vue'
import AccountIconSelector from '@/components/business/AccountIconSelector/index.vue'
import CurrencySelector from '@/components/business/CurrencySelector/index.vue'
import { AccountType } from '@/types/account'
import type { AccountForm } from '@/types/account'
import type { CreditCardBrand as CreditCardBrandData } from '@/data/credit-cards'
import type { CurrencyItem } from '@/data/currencies'
import { creditCardBrands } from '@/data/credit-cards'
import { useToast } from '@/composables/useToast'

const { t } = useI18n()
const accountStore = useAccountStore()
const bookStore = useBookStore()
const toast = useToast()

const submitting = ref(false)
const currentStep = ref(0)
const initialBalanceStr = ref('')

const steps = ['选择类型', '选择银行/品牌', '完善信息']

// Selector visibility
const showIconSelector = ref(false)
const showCurrencySelector = ref(false)

// Account type groups
const accountTypeGroups = [
  {
    label: '资金账户',
    types: [
      { value: AccountType.Cash, name: '现金', icon: 'wallet' },
      { value: AccountType.BankCard, name: '银行卡', icon: 'bank' },
      { value: AccountType.EWallet, name: '电子钱包', icon: 'wallet' },
    ],
  },
  {
    label: '信用账户',
    types: [
      { value: AccountType.CreditCard, name: '信用卡', icon: 'credit-card' },
    ],
  },
  {
    label: '投资账户',
    types: [
      { value: AccountType.Investment, name: '投资账户', icon: 'chart' },
    ],
  },
  {
    label: '应收/应付',
    types: [
      { value: AccountType.Debt, name: '借款/债务', icon: 'flag' },
    ],
  },
  {
    label: '充值账户',
    types: [
      { value: AccountType.Other, name: '其他账户', icon: 'plus' },
    ],
  },
]

// Form data
const formData = ref<Partial<AccountForm>>({
  name: '',
  type: 0,
  currency: '',
  icon: '',
  color: '',
  bankCode: '',
  bankName: '',
  cardBrand: '',
  cardNumber: '',
  remark: '',
})

// Computed helpers
const currentCurrencySymbol = computed(() => bookStore.currentCurrencySymbol)

const needsBankSelection = computed(() => {
  return formData.value.type === AccountType.BankCard
})

const needsCardBrandSelection = computed(() => {
  return formData.value.type === AccountType.CreditCard
})

const needsCardSelection = computed(() => {
  return formData.value.type === AccountType.CreditCard || formData.value.type === AccountType.BankCard
})

const displayedBanks = computed(() => [
  { code: 'ICBC', name: '中国工商银行', color: '#E30613', iconPath: '/static/icons/icbc.svg' },
  { code: 'ABC', name: '中国农业银行', color: '#2E8B57', iconPath: '/static/icons/abc.svg' },
  { code: 'BOC', name: '中国银行', color: '#C40000', iconPath: '/static/icons/boc.svg' },
  { code: 'CCB', name: '中国建设银行', color: '#003087', iconPath: '/static/icons/ccb.svg' },
  { code: 'COMM', name: '交通银行', color: '#003087', iconPath: '/static/icons/comm.svg' },
  { code: 'CMB', name: '招商银行', color: '#C40000', iconPath: '/static/icons/cmb.svg' },
  { code: 'CMBC', name: '中国民生银行', color: '#003087', iconPath: '/static/icons/cmbc.svg' },
  { code: 'CITIC', name: '中信银行', color: '#E30613', iconPath: '/static/icons/citic.svg' },
  { code: 'CIB', name: '兴业银行', color: '#003087', iconPath: '/static/icons/cib.svg' },
  { code: 'SPD', name: '浦发银行', color: '#003087', iconPath: '/static/icons/spdb.svg' },
  { code: 'HXB', name: '华夏银行', color: '#C40000', iconPath: '/static/icons/hxb.svg' },
  { code: 'PSBC', name: '中国邮政储蓄银行', color: '#003087', iconPath: '/static/icons/psbc.svg' },
])

const selectedTypeName = computed(() => {
  for (const group of accountTypeGroups) {
    const found = group.types.find(t => t.value === formData.value.type)
    if (found) return found.name
  }
  return ''
})

const selectedCardBrandName = computed(() => {
  if (!formData.value.cardBrand) return ''
  const brand = creditCardBrands.find(b => b.code === formData.value.cardBrand)
  return brand?.name || ''
})

const selectedCurrencyDisplay = computed(() => {
  if (!formData.value.currency) return currentCurrencySymbol.value
  return formData.value.currency
})

const canProceed = computed(() => {
  if (currentStep.value === 0) return (formData.value.type ?? 0) > 0
  if (currentStep.value === 1) {
    if (needsBankSelection.value) return !!formData.value.bankCode
    if (needsCardBrandSelection.value) return !!formData.value.cardBrand
    return true
  }
  return true
})

function isAppleIcon(name: string): boolean {
  const appleIcons = ['wallet', 'bank', 'credit-card', 'chart', 'flag', 'plus', 'edit', 'star', 'book', 'housing', 'savings', 'users', 'lock', 'unlock']
  return appleIcons.includes(name)
}

function selectType(item: { value: number; name: string; icon: string }) {
  formData.value.type = item.value
  const iconMap: Record<number, string> = {
    [AccountType.Cash]: 'wallet',
    [AccountType.BankCard]: 'bank',
    [AccountType.CreditCard]: 'credit-card',
    [AccountType.EWallet]: 'wallet',
    [AccountType.Investment]: 'chart',
    [AccountType.Debt]: 'flag',
    [AccountType.Other]: 'plus',
  }
  formData.value.icon = iconMap[item.value] || 'plus'
}

function selectBank(bank: { code: string; name: string; iconPath?: string | null; color?: string }) {
  formData.value.bankCode = bank.code
  formData.value.bankName = bank.name
  formData.value.icon = bank.iconPath || bank.code.toLowerCase()
}

function selectCardBrand(brand: CreditCardBrandData) {
  formData.value.cardBrand = brand.code
}

function onIconChange(iconId: string) {
  formData.value.icon = iconId
}

function onCurrencyChange(currency: CurrencyItem) {
  formData.value.currency = currency.code
}

async function handleSubmit() {
  if (!formData.value.name?.trim()) {
    toast.warning(t('account.nameRequired'))
    return
  }

  submitting.value = true
  try {
    await accountStore.createAccount({
      name: formData.value.name ?? '',
      type: formData.value.type ?? 1,
      initialBalance: initialBalanceStr.value ? parseFloat(initialBalanceStr.value) : undefined,
      currency: formData.value.currency,
      icon: formData.value.icon,
      color: formData.value.color,
      bankCode: formData.value.bankCode,
      bankName: formData.value.bankName,
      cardBrand: formData.value.cardBrand,
      cardNumber: formData.value.cardNumber,
      remark: formData.value.remark,
    })

    toast.success(t('account.createSuccess'))
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error: any) {
    toast.error(error.message || t('account.createFailed'))
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  formData.value.currency = bookStore.currentBook?.currency || 'CNY'
})
</script>

<style lang="scss" scoped>
.create-account-page {
  min-height: 100vh;
  background: #F8F9FA;
  padding-bottom: 160rpx;
  display: flex;
  flex-direction: column;
}

// Step indicator
.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48rpx 60rpx 40rpx;
  position: relative;
  background: #fff;
  flex-shrink: 0;
}

.step-dot {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: #E5E7EB;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
  flex-shrink: 0;
  transition: all 0.3s;

  &.active {
    background: #0F4C5C;
    transform: scale(1.2);
  }

  &.completed {
    background: #06D6A0;
    cursor: pointer;
  }
}

.dot-number {
  font-size: 24rpx;
  font-weight: 600;
  color: #fff;
}

.dot-check {
  display: flex;
  align-items: center;
  justify-content: center;
}

.step-line {
  position: absolute;
  left: 120rpx;
  right: 120rpx;
  height: 6rpx;
  background: #E5E7EB;
  border-radius: 3rpx;
  overflow: hidden;
}

.step-progress {
  height: 100%;
  background: linear-gradient(90deg, #0F4C5C 0%, #06D6A0 100%);
  border-radius: 3rpx;
  transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

// Step content
.step-content {
  flex: 1;
  padding: 24rpx;
  overflow-y: auto;
}

.step-header {
  margin-bottom: 32rpx;
}

.step-title {
  display: block;
  font-size: 44rpx;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 8rpx;
}

.step-desc {
  font-size: 28rpx;
  color: #9CA3AF;
}

// Type list (Step 1)
.type-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.type-group {
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.group-label {
  font-size: 24rpx;
  color: #9CA3AF;
  font-weight: 500;
  margin-bottom: 16rpx;
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
  gap: 10rpx;
  padding: 24rpx 20rpx;
  background: #F9FAFB;
  border-radius: 16rpx;
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

  .selected & {
    background: #0F4C5C;
  }
}

.item-name {
  font-size: 26rpx;
  color: #374151;
  font-weight: 500;
  text-align: center;
}

.item-check {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background: #0F4C5C;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 8rpx rgba(15, 76, 92, 0.3);
}

// Bank list (Step 2)
.bank-list {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.bank-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 28rpx 24rpx;
  background: #fff;
  transition: background 0.15s;
  border-bottom: 1rpx solid #F9FAFB;

  &:last-child {
    border-bottom: none;
  }

  &.selected {
    background: rgba(15, 76, 92, 0.04);
  }
}

.bank-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
}

.bank-svg {
  width: 52rpx;
  height: 52rpx;
}

.bank-initial {
  font-size: 32rpx;
  font-weight: 700;
}

.bank-name {
  flex: 1;
  font-size: 32rpx;
  color: #1F2937;
  font-weight: 500;
}

// Brand grid (Step 2)
.brand-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.brand-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 12rpx;
  background: #F9FAFB;
  border-radius: 20rpx;
  border: 4rpx solid transparent;
  transition: all 0.2s;

  &.selected {
    border-color: #0F4C5C;
    background: rgba(15, 76, 92, 0.06);
  }
}

.brand-logo {
  width: 100rpx;
  height: 100rpx;
  border-radius: 20rpx;
  border: 2rpx solid transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  transition: border-color 0.2s;
}

.brand-svg {
  width: 64rpx;
  height: 64rpx;
}

.brand-initial {
  font-size: 40rpx;
  font-weight: 700;
}

.brand-name {
  font-size: 24rpx;
  color: #374151;
  font-weight: 500;
  text-align: center;
}

// Form (Step 3)
.form-container {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.form-row {
  display: flex;
  align-items: center;
  padding: 32rpx 24rpx;
  border-bottom: 1rpx solid #F9FAFB;
  gap: 24rpx;

  &:last-child {
    border-bottom: none;
  }

  &.readonly {
    background: #FAFAFA;
  }

  &.remark-row {
    align-items: flex-start;
    flex-direction: column;
    gap: 12rpx;
  }
}

.icon-row {
  cursor: pointer;
}

.row-label {
  font-size: 32rpx;
  color: #374151;
  font-weight: 500;
  flex-shrink: 0;
  width: 180rpx;
}

.required {
  color: #EF476F;
  margin-left: 4rpx;
}

.icon-preview {
  flex: 1;
  display: flex;
  justify-content: center;
}

.icon-img {
  width: 60rpx;
  height: 60rpx;
}

.icon-placeholder {
  width: 64rpx;
  height: 64rpx;
  border-radius: 16rpx;
  background: #F3F4F6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.row-input {
  flex: 1;
  height: 80rpx;
  padding: 0;
  font-size: 32rpx;
  color: #1F2937;
  background: transparent;
  border: none;
  outline: none;
  text-align: right;

  &::placeholder {
    color: #D1D5DB;
  }
}

.row-value {
  flex: 1;
  font-size: 32rpx;
  color: #1F2937;
  text-align: right;
}

.row-value-selector {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8rpx;
}

.amount-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8rpx;
}

.currency-symbol {
  font-size: 36rpx;
  font-weight: 700;
  color: #0F4C5C;
}

.amount-input {
  text-align: right;
  min-width: 0;
}

.row-textarea {
  width: 100%;
  min-height: 160rpx;
  padding: 20rpx;
  background: #F9FAFB;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #374151;
  border: 2rpx solid transparent;
  resize: none;
  box-sizing: border-box;
  line-height: 1.6;

  &:focus {
    border-color: #0F4C5C;
  }

  &::placeholder {
    color: #D1D5DB;
  }
}

// Navigation bar
.nav-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + constant(safe-area-inset-bottom));
  background: #fff;
  border-top: 1rpx solid #F3F4F6;
  z-index: 100;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.nav-btn {
  flex: 1;
  height: 100rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  transition: all 0.2s;

  &:disabled {
    opacity: 0.5;
  }
}

.nav-btn-back {
  background: #F3F4F6;
  color: #0F4C5C;
  flex: 0 0 auto;
  padding: 0 48rpx;
}

.nav-btn-next {
  background: #0F4C5C;
  color: #fff;
}

.nav-btn-submit {
  background: #0F4C5C;
  color: #fff;

  &:disabled {
    background: #9CA3AF;
  }
}

.nav-btn-spacer {
  flex: 1;
}
</style>
