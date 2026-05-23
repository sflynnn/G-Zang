<template>
  <div class="business-app min-h-screen">
    <div class="fixed inset-0 -z-10 transition-colors duration-300 bg-background dark:bg-dark-bg" />
    <header class="sticky top-0 z-50 backdrop-blur-lg border-b bg-surface/80 dark:bg-dark-surface/80 border-border dark:border-dark-border">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-14">
          <div class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-[#0F4C5C] to-[#186a7d] flex items-center justify-center">
              <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h-6m-6 0h6m-6 0H5a2 2 0 00-2-2m14 0v-7a2 2 0 00-2-2H7a2 2 0 00-2 2v7m14 0H5" />
              </svg>
            </div>
            <div>
              <h1 class="text-base font-semibold text-text-primary dark:text-dark-text-primary">{{ $t('app.businessTitle') }}</h1>
              <p class="text-xs text-text-secondary dark:text-dark-text-secondary">{{ $t('app.businessSubtitle') }}</p>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <button @click="toggleTheme" class="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-dark-surface text-text-secondary dark:text-dark-text-secondary">
              <svg v-if="isDark" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"/>
              </svg>
              <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </header>
    <main class="flex-1">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useAppStore } from '@/stores/app';

const appStore = useAppStore();
const isDark = computed(() => appStore.isDark);

const toggleTheme = () => appStore.toggleTheme();

onMounted(() => appStore.initTheme());
watch(isDark, (dark) => {
  const html = document.documentElement;
  dark ? html.classList.add('dark') : html.classList.remove('dark');
}, { immediate: true });
</script>

<style scoped>
.business-app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
</style>
