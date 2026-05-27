<template>
  <view class="month-picker">
    <view class="picker-content">
      <!-- 左箭头 -->
      <view class="arrow-btn prev" @click="handlePrev">
        <text class="arrow-icon">‹</text>
      </view>

      <!-- 月份显示 -->
      <view class="month-display" @click="showPicker">
        <text class="month-text">{{ displayMonth }}</text>
      </view>

      <!-- 右箭头 -->
      <view class="arrow-btn next" :class="{ disabled: isNextDisabled }" @click="handleNext">
        <text class="arrow-icon">›</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { modal } from '@/composables/useModal'

interface Props {
  currentMonth: string  // 格式: 'YYYY-MM'
  minMonth?: string    // 最小月份，格式: 'YYYY-MM'
  maxMonth?: string    // 最大月份，格式: 'YYYY-MM'
}

const props = withDefaults(defineProps<Props>(), {
  minMonth: '2020-01',
  maxMonth: ''
})

const emit = defineEmits<{
  prev: []
  next: []
  change: [month: string]
}>()

// 解析当前月份
const currentYear = computed(() => parseInt(props.currentMonth.split('-')[0]))
const currentMon = computed(() => parseInt(props.currentMonth.split('-')[1]))

// 显示格式：2026年5月
const displayMonth = computed(() => {
  return `${currentYear.value}年${currentMon.value}月`
})

// 判断是否超过最大月份
const isNextDisabled = computed(() => {
  if (!props.maxMonth) return false
  return props.currentMonth >= props.maxMonth
})

// 判断是否早于最小月份
const isPrevDisabled = computed(() => {
  if (!props.minMonth) return false
  return props.currentMonth <= props.minMonth
})

// 格式化月份
const formatMonth = (year: number, month: number): string => {
  return `${year}-${String(month).padStart(2, '0')}`
}

// 上一个月
const handlePrev = () => {
  if (isPrevDisabled.value) return
  
  let year = currentYear.value
  let month = currentMon.value - 1
  
  if (month < 1) {
    month = 12
    year--
  }
  
  const newMonth = formatMonth(year, month)
  emit('prev')
  emit('change', newMonth)
}

// 下一个月
const handleNext = () => {
  if (isNextDisabled.value) return
  
  let year = currentYear.value
  let month = currentMon.value + 1
  
  if (month > 12) {
    month = 1
    year++
  }
  
  const newMonth = formatMonth(year, month)
  emit('next')
  emit('change', newMonth)
}

// 显示原生月份选择器
const showPicker = () => {
  const months = generateMonthList()
  const currentIndex = getCurrentIndex()
  
  modal.show({
    title: '选择月份',
    message: '功能开发中，请使用左右箭头切换月份',
    showCancel: false,
    confirmText: '我知道了',
  })
}

// 生成月份列表
const generateMonthList = (): string[] => {
  const months: string[] = []
  const [minYear, minMonth] = props.minMonth.split('-').map(Number)
  const maxYear = props.maxMonth ? parseInt(props.maxMonth.split('-')[0]) : new Date().getFullYear()
  const maxM = props.maxMonth ? parseInt(props.maxMonth.split('-')[1]) : new Date().getMonth() + 1
  
  for (let y = minYear; y <= maxYear; y++) {
    const start = y === minYear ? minMonth : 1
    const end = y === maxYear ? maxM : 12
    
    for (let m = start; m <= end; m++) {
      months.push(formatMonth(y, m))
    }
  }
  
  return months
}

// 获取当前月份在列表中的索引
const getCurrentIndex = (): number => {
  const months = generateMonthList()
  return months.indexOf(props.currentMonth)
}

// 生成选择器数据
const generateMonthRange = (): string[] => {
  return generateMonthList()
}
</script>

<style lang="scss" scoped>
.month-picker {
  display: flex;
  align-items: center;
  justify-content: center;
}

.picker-content {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.arrow-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gzang-bg);
  border-radius: 50%;
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &:active {
    transform: scale(0.9);
    opacity: 0.8;
  }
  
  &.disabled {
    opacity: 0.4;
    pointer-events: none;
  }
}

.arrow-icon {
  font-size: 28rpx;
  font-weight: 300;
  color: var(--gzang-primary);
  line-height: 1;
}

.month-display {
  min-width: 120rpx;
  padding: 8rpx 16rpx;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-full);
  text-align: center;
}

.month-text {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
}
</style>
