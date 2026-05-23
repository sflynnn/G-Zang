import { defineStore } from 'pinia';
import { ref, computed, watch } from 'vue';

export const useAppStore = defineStore('app', () => {
  const isDark = ref(localStorage.getItem('business_theme') === 'dark' || false);

  watch(isDark, (dark) => {
    const html = document.documentElement;
    if (dark) html.classList.add('dark');
    else html.classList.remove('dark');
    localStorage.setItem('business_theme', dark ? 'dark' : 'light');
  }, { immediate: true });

  const initTheme = () => {
    const saved = localStorage.getItem('business_theme');
    if (!saved) {
      isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches;
    }
  };

  const toggleTheme = () => { isDark.value = !isDark.value; };
  const theme = computed(() => isDark.value ? 'dark' : 'light');

  return { isDark, theme, initTheme, toggleTheme };
});
