<template>
  <view class="date-picker-page">
    <uni-nav-bar 
      left-icon="close" 
      title="选择日期" 
      @clickLeft="onCancel"
    />
    
    <view class="picker-content">
      <van-datetime-picker
        v-model="currentDate"
        type="date"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onConfirm"
        @cancel="onCancel"
      />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const currentDate = ref(Date.now())
const minDate = ref(new Date(2020, 0, 1).getTime())
const maxDate = ref(Date.now())

function onConfirm(value: number) {
  const date = new Date(value)
  const dateStr = date.toISOString().split('T')[0]
  
  // 获取页面栈
  const pages = getCurrentPages()
  const prevPage = pages[pages.length - 2]
  
  if (prevPage) {
    // 触发上一页的 confirm 事件
    prevPage.$vm && prevPage.$vm.$trigger && prevPage.$vm.$trigger('confirm', { date: dateStr })
  }
  
  uni.navigateBack()
}

function onCancel() {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.date-picker-page {
  min-height: 100vh;
  background: var(--gzang-bg);
}

.picker-content {
  padding-top: 20px;
}
</style>
