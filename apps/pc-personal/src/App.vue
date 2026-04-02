<template>
  <div 
    class="personal-app min-h-screen transition-colors duration-300 dark"
  >
    <!-- 主应用背景层 -->
    <div 
      class="fixed inset-0 -z-10 transition-colors duration-300 bg-background dark:bg-dark-bg"
    />
    
    <!-- 子应用头部 -->
    <header 
      class="sticky top-0 z-50 backdrop-blur-lg border-b transition-colors duration-300 bg-surface/80 dark:bg-dark-surface/80 border-border dark:border-dark-border"
    >
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-14">
          <!-- 左侧：Logo和标题 -->
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-lg bg-gradient-primary flex items-center justify-center">
              <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
            <div>
              <h1 class="text-base font-semibold text-text-primary dark:text-dark-text-primary">
                {{ $t('app.personalTitle') }}
              </h1>
              <p class="text-xs text-text-secondary dark:text-dark-text-secondary">
                {{ $t('app.personalSubtitle') }}
              </p>
            </div>
          </div>

          <!-- 右侧：操作按钮 -->
          <div class="flex items-center gap-2">
            <!-- 主题切换 -->
            <button
              @click="toggleTheme"
              class="p-2 rounded-lg transition-colors duration-200 hover:bg-gray-100 dark:hover:bg-dark-surface-elevated text-text-secondary dark:text-dark-text-secondary hover:text-text-primary dark:hover:text-dark-text-primary"
              :title="$t('common.toggleTheme')"
            >
              <svg v-if="isDark" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"/>
              </svg>
              <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"/>
              </svg>
            </button>

            <!-- 通知 -->
            <button
              class="p-2 rounded-lg transition-colors duration-200 relative hover:bg-gray-100 dark:hover:bg-dark-surface-elevated text-text-secondary dark:text-dark-text-secondary hover:text-text-primary dark:hover:text-dark-text-primary"
              :title="$t('common.notifications')"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
              </svg>
              <!-- 通知徽章 -->
              <span class="absolute top-1 right-1 w-2 h-2 bg-secondary rounded-full"></span>
            </button>

            <!-- 用户头像 -->
            <button
              class="flex items-center gap-2 p-1.5 rounded-lg transition-colors duration-200 hover:bg-gray-100 dark:hover:bg-dark-surface-elevated"
            >
              <div class="w-8 h-8 rounded-full bg-gradient-secondary flex items-center justify-center">
                <span class="text-white text-sm font-medium">我</span>
              </div>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 内容区域 -->
    <main class="flex-1">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useAppStore } from './stores/app';

const appStore = useAppStore();

// 响应式主题状态
const isDark = computed(() => appStore.isDark);

// 切换主题
const toggleTheme = () => {
  appStore.toggleTheme();
};

// 初始化主题
onMounted(() => {
  appStore.initTheme();
});
</script>

<style scoped>
.personal-app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 深色模式过渡效果 */
* {
  transition-property: background-color, border-color, color;
  transition-duration: 200ms;
  transition-timing-function: ease-out;
}

/* 排除动画类的过渡 */
.animate-*, 
[class*="animate-"],
button,
input,
select,
textarea {
  transition-property: all;
}
</style>
