<template>
  <view class="chart-page">
    <uni-nav-bar left-icon="back" title="支出构成" @clickLeft="goBack" />
    
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading" />
    </view>
    
    <template v-else>
      <!-- 饼图区域 -->
      <view class="pie-chart-section">
        <view class="pie-container">
          <view class="pie-chart" :style="pieStyle"></view>
          <view class="pie-center">
            <view class="center-label">总支出</view>
            <view class="center-amount">{{ currentCurrency }}{{ formatAmount(totalExpense) }}</view>
          </view>
        </view>
      </view>

      <!-- 图例 -->
      <view class="legend-section">
        <view class="legend-title">支出分类</view>
        <view 
          v-for="(item, index) in categoryData" 
          :key="index"
          class="legend-item"
        >
          <view class="legend-left">
            <view class="legend-dot" :style="{ background: item.color }"></view>
            <view class="legend-icon">{{ item.icon }}</view>
            <view class="legend-name">{{ item.name }}</view>
          </view>
          <view class="legend-right">
            <view class="legend-percent">{{ item.percent }}%</view>
            <view class="legend-amount">{{ currentCurrency }}{{ formatAmount(item.amount) }}</view>
          </view>
        </view>
      </view>

      <!-- 详细数据 -->
      <view class="detail-section">
        <view class="detail-title">详细数据</view>
        <view class="detail-list">
          <view 
            v-for="(item, index) in categoryData" 
            :key="index"
            class="detail-item"
          >
            <view class="detail-header">
              <view class="detail-icon">{{ item.icon }}</view>
              <view class="detail-name">{{ item.name }}</view>
              <view class="detail-percent">{{ item.percent }}%</view>
            </view>
            <view class="detail-bar-wrapper">
              <view 
                class="detail-bar" 
                :style="{ 
                  width: item.percent + '%',
                  background: item.color 
                }"
              ></view>
            </view>
            <view class="detail-footer">
              <text class="detail-amount">{{ currentCurrency }}{{ formatAmount(item.amount) }}</text>
              <text class="detail-count">{{ item.count }} 笔交易</text>
            </view>
          </view>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useReportStore } from '@/stores/report'

const reportStore = useReportStore()

const loading = ref(false)
const categoryData = ref<any[]>([])
const totalExpense = ref(0)

const colors = ['#FB8B24', '#0F4C5C', '#06D6A0', '#EF476F', '#FFD166', '#3A86FF', '#8338EC', '#118AB2']
const icons = ['🍜', '🚇', '🛒', '🎮', '💊', '📚', '🏠', '📱']

const pieStyle = computed(() => {
  if (categoryData.value.length === 0) return {}
  
  let gradient = ''
  let currentPercent = 0
  
  categoryData.value.forEach((item, index) => {
    const start = currentPercent
    currentPercent += item.percent
    gradient += `${item.color} ${start}% ${currentPercent}%`
    if (index < categoryData.value.length - 1) {
      gradient += ', '
    }
  })
  
  return {
    background: `conic-gradient(${gradient})`
  }
})

const currentCurrency = '¥'

onMounted(async () => {
  await loadData()
})

async function loadData() {
  loading.value = true
  try {
    await reportStore.fetchCategoryReport({
      type: 2 // expense
    })
    
    const report = reportStore.categoryReport || []
    let total = 0
    
    categoryData.value = report.map((item: any, index: number) => {
      total += parseFloat(item.totalAmount) || 0
      return {
        name: item.categoryName || '未分类',
        amount: parseFloat(item.totalAmount) || 0,
        count: item.transactionCount || 0,
        color: colors[index % colors.length],
        icon: icons[index % icons.length],
        percent: 0
      }
    })
    
    // Calculate percentages
    categoryData.value.forEach(item => {
      item.percent = total > 0 ? Math.round((item.amount / total) * 100) : 0
    })
    
    // Sort by amount descending
    categoryData.value.sort((a, b) => b.amount - a.amount)
    
    totalExpense.value = total
  } catch (error) {
    // ignore
  } finally {
    loading.value = false
  }
}

function formatAmount(amount: any): string {
  if (!amount && amount !== 0) return '0.00'
  const num = typeof amount === 'string' ? parseFloat(amount) : amount
  return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function goBack() {
  uni.navigateBack()
}
</script>

<style scoped lang="scss">
.chart-page {
  min-height: 100vh;
  background-color: #F8F9FA;
}

.loading-container {
  padding: 200rpx 0;
}

.pie-chart-section {
  background: #fff;
  padding: 48rpx;
  margin-bottom: 24rpx;
}

.pie-container {
  position: relative;
  width: 400rpx;
  height: 400rpx;
  margin: 0 auto;
}

.pie-chart {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  background: #fff;
  width: 200rpx;
  height: 200rpx;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.center-label {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 8rpx;
}

.center-amount {
  font-size: 32rpx;
  font-weight: 700;
  color: #1F2937;
}

.legend-section {
  background: #fff;
  margin: 0 24rpx;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.legend-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 24rpx;
}

.legend-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #F3F4F6;
  
  &:last-child {
    border-bottom: none;
  }
}

.legend-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.legend-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
}

.legend-icon {
  font-size: 28rpx;
}

.legend-name {
  font-size: 28rpx;
  color: #1F2937;
}

.legend-right {
  text-align: right;
}

.legend-percent {
  font-size: 28rpx;
  font-weight: 600;
  color: #1F2937;
}

.legend-amount {
  font-size: 24rpx;
  color: #9CA3AF;
  margin-top: 4rpx;
}

.detail-section {
  background: #fff;
  margin: 0 24rpx;
  border-radius: 16rpx;
  padding: 24rpx;
}

.detail-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 24rpx;
}

.detail-list {
  .detail-item {
    margin-bottom: 32rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 12rpx;
}

.detail-icon {
  font-size: 28rpx;
}

.detail-name {
  flex: 1;
  font-size: 28rpx;
  color: #1F2937;
}

.detail-percent {
  font-size: 28rpx;
  font-weight: 600;
  color: #0F4C5C;
}

.detail-bar-wrapper {
  height: 16rpx;
  background: #F3F4F6;
  border-radius: 8rpx;
  overflow: hidden;
  margin-bottom: 12rpx;
}

.detail-bar {
  height: 100%;
  border-radius: 8rpx;
  transition: width 0.3s;
}

.detail-footer {
  display: flex;
  justify-content: space-between;
}

.detail-amount {
  font-size: 26rpx;
  color: #666;
}

.detail-count {
  font-size: 24rpx;
  color: #9CA3AF;
}
</style>
