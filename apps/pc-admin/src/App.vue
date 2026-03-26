<template>
  <n-config-provider :theme="theme">
    <n-message-provider>
      <n-notification-provider>
        <n-dialog-provider>
          <n-loading-bar-provider>
            <router-view />
            <GlobalLoading />
          </n-loading-bar-provider>
        </n-dialog-provider>
      </n-notification-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { darkTheme } from 'naive-ui';
import { useAppStore } from '@/stores/app';
import GlobalLoading from '@/components/GlobalLoading.vue';

// 在组件挂载后设置全局API（确保Provider已经准备好）
onMounted(() => {
  // 这里可以安全地设置全局API，因为Provider已经挂载
  console.log('App mounted, message providers should be ready');
});

// 使用暗色主题
const theme = computed(() => {
  const appStore = useAppStore();
  return appStore.isDark ? darkTheme : null;
});
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
