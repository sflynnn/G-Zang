<script setup lang="ts">
import { ref, computed } from 'vue'
import SelectorSheet from '../SelectorSheet/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import { banksByLetter, bankLetterGroups, searchBanks } from '@/data/banks'
import type { BankItem } from '@/data/banks'

interface Props {
  show: boolean
  value?: BankItem | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:show': [value: boolean]
  change: [bank: BankItem]
}>()

const searchKeyword = ref('')

const filteredBanks = computed(() => {
  return searchBanks(searchKeyword.value)
})

const displayGroups = computed(() => {
  const groups: Array<{ letter: string; banks: BankItem[] }> = []
  if (searchKeyword.value.trim()) {
    return [{ letter: '#', banks: filteredBanks.value }]
  }
  bankLetterGroups.forEach(letter => {
    const banks = filteredBanks.value.filter(b => b.letter === letter)
    if (banks.length > 0) {
      groups.push({ letter, banks })
    }
  })
  return groups
})

const showIndexBar = computed(() => !searchKeyword.value.trim())

const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ#'.split('')

function scrollToLetter(letter: string) {
  const target = displayGroups.value.find(g => g.letter === letter)
  if (!target) return

  const query = uni.createSelectorQuery()
  query.select(`#letter-${letter}`).boundingClientRect()
  query.select('.sheet-content').boundingClientRect()
  query.exec((res) => {
    if (res[0] && res[1]) {
      const top = res[0].top - res[1].top + (res[1].scrollTop || 0)
      uni.pageScrollTo({ scrollTop: top, duration: 200 })
    }
  })
}

function selectBank(bank: BankItem) {
  emit('change', bank)
  emit('update:show', false)
  searchKeyword.value = ''
}

function isSelected(bank: BankItem): boolean {
  return props.value?.code === bank.code
}

function getBankInitials(bank: BankItem): string {
  return bank.name.charAt(0)
}

function getBankColor(bank: BankItem): string {
  return bank.color || '#0F4C5C'
}
</script>

<template>
  <SelectorSheet
    :show="show"
    title="选择银行"
    height="80vh"
    @update:show="$emit('update:show', $event)"
  >
    <view class="bank-selector">
      <!-- Search bar -->
      <view class="search-bar">
        <AppleIcon name="search" :size="18" color="#9CA3AF" />
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="搜索银行名称"
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

      <!-- Letter index bar -->
      <view v-if="showIndexBar" class="index-bar">
        <view
          v-for="letter in letters"
          :key="letter"
          class="index-item"
          :class="{ active: bankLetterGroups.includes(letter) }"
          @click="scrollToLetter(letter)"
        >
          {{ letter }}
        </view>
      </view>

      <!-- Bank list -->
      <view class="bank-list">
        <view
          v-for="group in displayGroups"
          :key="group.letter"
          :id="`letter-${group.letter}`"
          class="letter-group"
        >
          <view class="letter-header">{{ group.letter }}</view>
          <view class="bank-items">
            <view
              v-for="bank in group.banks"
              :key="bank.code"
              class="bank-item"
              :class="{ selected: isSelected(bank) }"
              @click="selectBank(bank)"
            >
              <!-- Bank logo/icon -->
              <view
                class="bank-icon"
                :style="{ background: getBankColor(bank) + '20' }"
              >
                <image
                  v-if="bank.iconPath"
                  :src="bank.iconPath"
                  class="bank-svg"
                  mode="aspectFit"
                />
                <text v-else class="bank-initial" :style="{ color: getBankColor(bank) }">
                  {{ getBankInitials(bank) }}
                </text>
              </view>

              <!-- Bank name -->
              <text class="bank-name">{{ bank.name }}</text>

              <!-- Check mark -->
              <AppleIcon
                v-if="isSelected(bank)"
                name="check"
                :size="20"
                color="#0F4C5C"
              />
            </view>
          </view>
        </view>

        <!-- Empty state -->
        <view v-if="displayGroups.length === 0" class="empty-state">
          <AppleIcon name="search" :size="40" color="#D1D5DB" />
          <text class="empty-text">未找到相关银行</text>
        </view>
      </view>
    </view>
  </SelectorSheet>
</template>

<style lang="scss" scoped>
.bank-selector {
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

.index-bar {
  position: fixed;
  right: 12rpx;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8rpx 0;
}

.index-item {
  font-size: 22rpx;
  color: #9CA3AF;
  padding: 2rpx 6rpx;
  font-weight: 500;
  min-width: 36rpx;
  text-align: center;
  line-height: 1.6;

  &.active {
    color: #0F4C5C;
    font-weight: 700;
  }
}

.bank-list {
  flex: 1;
  overflow-y: auto;
}

.letter-group {
  padding: 0 24rpx;
}

.letter-header {
  font-size: 26rpx;
  font-weight: 600;
  color: #0F4C5C;
  padding: 16rpx 0 12rpx;
  background: #F8F9FA;
  position: sticky;
  top: 0;
  z-index: 5;
  letter-spacing: 1rpx;
}

.bank-items {
  display: flex;
  flex-direction: column;
}

.bank-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #F9FAFB;
  transition: background 0.15s;

  &:last-child {
    border-bottom: none;
  }

  &.selected {
    background: rgba(15, 76, 92, 0.04);
    margin: 0 -24rpx;
    padding: 24rpx;
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
