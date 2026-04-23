<template>
  <view class="app">
    <slot />
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  uni.hideTabBar({ animation: false }).catch(() => {})
})
</script>

<style lang="scss">
@use '@/styles/apple/index.scss';

.app {
  height: 100vh;
  background-color: var(--gzang-bg);
}
</style>
