<template>
  <view class="bills-page">
    <view class="nav-bar">
      <view class="nav-title">账单</view>
      <view class="nav-right">
        <view class="nav-action" @click="goToSearch">
          <uni-icons type="search" size="20" color="#333"></uni-icons>
        </view>
      </view>
    </view>

    <view class="main-content">
      <text class="placeholder">账单列表页面</text>
    </view>
  </view>
</template>

<script setup lang="ts">
const goToSearch = () => {
  uni.navigateTo({ url: '/pages/search/index' })
}
</script>

<style lang="scss" scoped>
.bills-page {
  height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e5e5e5;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.nav-right {
  display: flex;
  align-items: center;
}

.nav-action {
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.2s;

  &:active {
    background-color: #f0f0f0;
  }
}

.main-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder {
  font-size: 16px;
  color: #999;
}
</style>
