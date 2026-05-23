<template>
  <view class="app">
    <slot />
  </view>

  <!-- 全局路由 Loading 遮罩（路由转场、异步操作） -->
  <PageLoading
    :is-visible="loadingVisible"
    :type="loadingType"
    :text="loadingText"
    :theme="loadingTheme"
    :closable="loadingClosable"
    @close="loading.hide()"
  />

  <!-- 应用启动页（冷启动时一次性展示） -->
  <AppStartup
    :visible="startupVisible"
    @ready="onStartupReady"
    @exit="onStartupExit"
  />
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useLoading } from '@/composables/useLoading'
import PageLoading from '@/components/common/PageLoading/index.vue'
import AppStartup from '@/components/common/AppStartup/index.vue'

const loading = useLoading()
const startupVisible = ref(false)
const startupReady = ref(false)

const loadingVisible = computed(() => loading.isVisible.value)
const loadingType = computed(() => loading.loadingType.value)
const loadingText = computed(() => loading.loadingText.value)
const loadingTheme = computed(() => loading.loadingTheme.value)
const loadingClosable = computed(() => loading.overlayClosable.value)

onMounted(() => {
  uni.hideTabBar({ animation: false }).catch(() => {})

  // 仅首次冷启动显示启动页
  const hasShownStartup = uni.getStorageSync('__app_startup_shown__')
  if (!hasShownStartup) {
    startupVisible.value = true
    uni.setStorageSync('__app_startup_shown__', true)
  }
})

const onStartupReady = () => {
  startupReady.value = true
}

const onStartupExit = () => {
  startupVisible.value = false
}
</script>

<style lang="scss">
@use '@/styles/apple/index.scss';

.app {
  height: 100vh;
  background-color: var(--gzang-bg);
  position: relative;
  overflow: hidden;
}
</style>
