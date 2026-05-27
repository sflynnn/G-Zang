<template>
  <view class="amount-keyboard" :class="{ 'amount-keyboard--fixed': fixed, 'amount-keyboard--simple': simple }">
    <!-- Amount Display -->
    <view class="keyboard-display">
      <text class="keyboard-currency">{{ currencySymbol }}</text>
      <text class="keyboard-amount">{{ displayAmount }}</text>
    </view>

    <!-- Quick Amount Buttons (Optional) -->
    <view v-if="!simple && quickAmounts.length > 0" class="quick-amounts">
      <view 
        v-for="amount in quickAmounts" 
        :key="amount"
        class="quick-amount-btn"
        :class="{ active: inputValue === String(amount) }"
        @click="handleQuickAmount(amount)"
      >
        {{ currencySymbol }}{{ amount }}
      </view>
      <view class="quick-amount-btn custom" @click="toggleKeyboard">
        <AppleIcon name="edit" :size="14" />
        <text>{{ customLabel }}</text>
      </view>
    </view>

    <!-- Number Keyboard -->
    <view v-show="showKeyboard || simple" class="keyboard-grid">
      <view
        v-for="key in keys"
        :key="key.value"
        class="keyboard-key"
        :class="{
          'keyboard-key--action': key.action,
          'keyboard-key--delete': key.value === 'delete',
          'keyboard-key--confirm': key.value === 'confirm'
        }"
        @click="handleKeyClick(key)"
      >
        <uni-icons
          v-if="key.value === 'delete'"
          type="scan"
          :size="20"
          color="currentColor"
        />
        <text v-else-if="key.action === 'confirm'" class="key-text key-text--confirm">
          {{ key.label }}
        </text>
        <text v-else class="key-text">{{ key.label }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()

interface Props {
  modelValue?: string | number
  maxLength?: number
  maxAmount?: number
  fixed?: boolean
  simple?: boolean
  currencySymbol?: string
  quickAmounts?: number[]
  customLabel?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '0',
  maxLength: 10,
  maxAmount: 999999999,
  fixed: false,
  simple: false,
  currencySymbol: '¥',
  quickAmounts: () => [10, 50, 100, 200],
  customLabel: '自定义'
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  'confirm': [value: string]
  'cancel': []
}>()

const showKeyboard = ref(false)

const keys = [
  { label: '1', value: '1' },
  { label: '2', value: '2' },
  { label: '3', value: '3' },
  { label: '删除', value: 'delete', action: 'delete' },
  { label: '4', value: '4' },
  { label: '5', value: '5' },
  { label: '6', value: '6' },
  { label: '7', value: '7' },
  { label: '8', value: '8' },
  { label: '9', value: '9' },
  { label: '.', value: '.' },
  { label: '0', value: '0' },
  { label: '00', value: '00' },
]

const inputValue = ref(String(props.modelValue))

watch(() => props.modelValue, (val) => {
  inputValue.value = String(val)
})

const displayAmount = computed(() => {
  const val = inputValue.value
  if (val === '' || val === '0') return '0.00'

  const parts = val.split('.')
  const intPart = parts[0] || '0'
  const decPart = parts[1] || ''

  const formatted = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
  return decPart ? `${formatted}.${decPart}` : `${formatted}.00`
})

const handleQuickAmount = (amount: number) => {
  inputValue.value = String(amount)
  emit('update:modelValue', String(amount))
}

const toggleKeyboard = () => {
  showKeyboard.value = !showKeyboard.value
}

const handleKeyClick = (key: typeof keys[0]) => {
  if (key.action === 'delete') {
    handleDelete()
  } else if (key.action === 'confirm') {
    handleConfirm()
  } else {
    handleInput(key.value)
  }
}

const handleInput = (value: string) => {
  let current = inputValue.value

  if (value === '.') {
    if (current.includes('.')) return
    current += '.'
  } else if (value === '00') {
    if (current === '0' || current === '') return
    if (current.includes('.')) {
      const parts = current.split('.')
      if (parts[1].length >= 2) return
      current += '00'
    } else {
      if (current.length >= props.maxLength - 2) return
      current += '00'
    }
  } else {
    if (current === '0' && value !== '.') {
      current = value
    } else {
      if (current.replace('.', '').length >= props.maxLength) return
      if (current.includes('.')) {
        const parts = current.split('.')
        if (parts[1].length >= 2) return
      }
      const newVal = parseFloat(current + value)
      if (newVal > props.maxAmount) return
      current += value
    }
  }

  inputValue.value = current
  emit('update:modelValue', current)
}

const handleDelete = () => {
  const current = inputValue.value
  if (current.length <= 1) {
    inputValue.value = '0'
  } else {
    inputValue.value = current.slice(0, -1)
  }
  emit('update:modelValue', inputValue.value)
}

const handleConfirm = () => {
  const val = parseFloat(inputValue.value) || 0
  if (val > 0) {
    emit('confirm', inputValue.value)
    showKeyboard.value = false
  } else {
    toast.warning('请输入金额')
  }
}
</script>

<style scoped lang="scss">
.amount-keyboard {
  background-color: var(--gzang-surface);

  &--fixed {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 100;
  }
}

.keyboard-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
  padding: 24rpx;
  background-color: var(--gzang-surface);
  border-bottom: 1rpx solid var(--gzang-border);
}

.keyboard-currency {
  font-size: 32rpx;
  color: var(--gzang-text-secondary);
  margin-right: 8rpx;
  font-weight: 500;
}

.keyboard-amount {
  font-size: 64rpx;
  font-weight: 600;
  color: var(--gzang-primary);
  font-family: var(--font-mono);
  font-variant-numeric: tabular-nums;
  letter-spacing: -1px;
}

// Quick Amounts
.quick-amounts {
  display: flex;
  gap: 16rpx;
  padding: 16rpx 24rpx;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  
  &::-webkit-scrollbar {
    display: none;
  }
}

.quick-amount-btn {
  flex-shrink: 0;
  padding: 16rpx 28rpx;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.active {
    background: var(--gzang-secondary);
    color: white;
  }
  
  &.custom {
    display: flex;
    align-items: center;
    gap: 8rpx;
    background: var(--gzang-surface);
    border: 1.5rpx dashed var(--gzang-border);
  }
  
  &:active {
    transform: scale(0.95);
  }
}

// Keyboard Grid
.keyboard-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rpx;
  background-color: var(--gzang-border);
  padding: 1rpx;
}

.keyboard-key {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 96rpx;
  background-color: var(--gzang-surface);
  transition: background-color 0.1s;

  &:active {
    background-color: #f0f0f0;
  }

  &--action {
    background-color: #f5f5f5;
  }

  &--delete {
    color: var(--gzang-text-secondary);
  }

  &--confirm {
    background-color: var(--gzang-secondary);
    color: white;
  }
}

.key-text {
  font-size: 40rpx;
  color: var(--gzang-text-primary);

  &--confirm {
    font-size: 32rpx;
    font-weight: 600;
  }
}
</style>
