<script setup lang="ts">
import SelectorSheet from '../SelectorSheet/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import { creditCardBrands } from '@/data/credit-cards'
import type { CreditCardBrand } from '@/data/credit-cards'

interface Props {
  show: boolean
  value?: CreditCardBrand | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:show': [value: boolean]
  change: [brand: CreditCardBrand]
}>()

function selectBrand(brand: CreditCardBrand) {
  emit('change', brand)
  emit('update:show', false)
}

function isSelected(brand: CreditCardBrand): boolean {
  return props.value?.code === brand.code
}

function getBrandColor(brand: CreditCardBrand): string {
  return brand.color || '#0F4C5C'
}
</script>

<template>
  <SelectorSheet
    :show="show"
    title="选择卡品牌"
    height="65vh"
    @update:show="$emit('update:show', $event)"
  >
    <view class="card-brand-selector">
      <view class="brand-grid">
        <view
          v-for="brand in creditCardBrands"
          :key="brand.code"
          class="brand-item"
          :class="{ selected: isSelected(brand) }"
          @click="selectBrand(brand)"
        >
          <!-- Brand logo -->
          <view
            class="brand-logo"
            :style="{
              background: getBrandColor(brand) + '15',
              borderColor: isSelected(brand) ? getBrandColor(brand) : 'transparent'
            }"
          >
            <image
              v-if="brand.iconPath"
              :src="brand.iconPath"
              class="brand-svg"
              mode="aspectFit"
            />
            <text
              v-else
              class="brand-initial"
              :style="{ color: getBrandColor(brand) }"
            >
              {{ brand.code.charAt(0) }}
            </text>
          </view>

          <!-- Brand name -->
          <text class="brand-name">{{ brand.name }}</text>

          <!-- Check badge -->
          <view
            v-if="isSelected(brand)"
            class="check-badge"
            :style="{ background: getBrandColor(brand) }"
          >
            <AppleIcon name="check" :size="14" color="#fff" />
          </view>
        </view>
      </view>

      <!-- Tips -->
      <view class="tips">
        <AppleIcon name="info" :size="16" color="#9CA3AF" />
        <text class="tips-text">卡品牌影响刷卡费率结算，请选择正确的发卡组织</text>
      </view>
    </view>
  </SelectorSheet>
</template>

<style lang="scss" scoped>
.card-brand-selector {
  padding: 24rpx;
}

.brand-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
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
  position: relative;
  transition: all 0.2s;

  &.selected {
    background: rgba(15, 76, 92, 0.06);
  }
}

.brand-logo {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
  border: 2rpx solid transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.2s;
  overflow: hidden;
}

.brand-svg {
  width: 64rpx;
  height: 64rpx;
}

.brand-initial {
  font-size: 44rpx;
  font-weight: 700;
}

.brand-name {
  font-size: 24rpx;
  color: #374151;
  font-weight: 500;
  text-align: center;
}

.check-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 8rpx rgba(15, 76, 92, 0.3);
}

.tips {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  margin-top: 32rpx;
  padding: 24rpx;
  background: #F9FAFB;
  border-radius: 16rpx;
}

.tips-text {
  font-size: 24rpx;
  color: #9CA3AF;
  flex: 1;
  line-height: 1.6;
}
</style>
