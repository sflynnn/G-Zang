import { defineStore } from 'pinia';
import { ref, watch, computed } from 'vue';

export const useAppStore = defineStore('app', () => {
  // 主题状态
  const isDark = ref(localStorage.getItem('personal_theme') === 'dark' || false);
  
  // 语言设置
  const language = ref(localStorage.getItem('personal_language') || 'zh');

  // 监听主题变化，应用到DOM
  watch(isDark, (newDark) => {
    const html = document.documentElement;
    if (newDark) {
      html.classList.add('dark');
    } else {
      html.classList.remove('dark');
    }
    localStorage.setItem('personal_theme', newDark ? 'dark' : 'light');
  }, { immediate: true });

  // 初始化时检查系统主题偏好
  const initTheme = () => {
    const savedTheme = localStorage.getItem('personal_theme');
    if (!savedTheme) {
      // 检查系统偏好
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      isDark.value = prefersDark;
    }
  };

  // 设置主题
  const setDarkTheme = (dark: boolean) => {
    isDark.value = dark;
  };

  // 切换主题
  const toggleTheme = () => {
    isDark.value = !isDark.value;
  };

  // 设置语言
  const setLanguage = (lang: string) => {
    language.value = lang;
    localStorage.setItem('personal_language', lang);
  };

  // 计算属性
  const theme = computed(() => isDark.value ? 'dark' : 'light');

  return {
    // 状态
    isDark,
    language,
    theme,
    
    // 方法
    initTheme,
    setDarkTheme,
    toggleTheme,
    setLanguage
  };
});
