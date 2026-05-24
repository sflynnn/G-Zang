<template>
  <view class="date-picker-page">
    <uni-nav-bar 
      left-icon="close" 
      :title="$t('common.selectDate')" 
      @clickLeft="onCancel"
    />
    
    <view class="picker-content">
      <uni-datetime-picker
        v-model="currentDate"
        :start="startDate"
        :end="endDate"
        return-type="string"
        :border="false"
        @change="onChange"
        @maskClick="onMaskClick"
      />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const currentDate = ref(new Date())

const startDate = computed(() => new Date(2020, 0, 1).getTime())
const endDate = computed(() => Date.now())

function onChange(e: string | string[]) {
  if (Array.isArray(e)) return
  const dateStr = e.split(' ')[0]
  
  const pages = getCurrentPages()
  const prevPage = pages[pages.length - 2]
  
  if (prevPage) {
    prevPage.$vm && prevPage.$vm.$trigger && prevPage.$vm.$trigger('confirm', { date: dateStr })
  }
  
  uni.navigateBack()
}

function onMaskClick() {
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
