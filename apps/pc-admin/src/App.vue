<template>
  <n-config-provider 
    :theme="theme" 
    :theme-overrides="themeOverrides"
    :locale="locale"
    :date-locale="dateLocale"
  >
    <n-message-provider>
      <n-notification-provider>
        <n-dialog-provider>
          <n-loading-bar-provider>
            <GlobalLoading />
            <router-view />
          </n-loading-bar-provider>
        </n-dialog-provider>
      </n-notification-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue';
import { darkTheme, zhCN, dateZhCN, enUS, dateEnUS } from 'naive-ui';
import { useAppStore } from '@/stores/app';
import { useI18n } from 'vue-i18n';
import GlobalLoading from '@/components/GlobalLoading.vue';

const appStore = useAppStore();
const { locale: i18nLocale } = useI18n();

// Naive UI 暗色主题配置覆盖
const darkThemeOverrides = {
  common: {
    primaryColor: '#0F4C5C',
    primaryColorHover: '#186a7d',
    primaryColorPressed: '#0a3644',
    primaryColorSuppl: '#0F4C5C',
    primaryColorSuccess: '#06D6A0',
    primaryColorWarning: '#FFD166',
    primaryColorError: '#EF476F',
    primaryColorInfo: '#118AB2',
    
    infoColor: '#118AB2',
    infoColorHover: '#1496c1',
    infoColorPressed: '#0d7290',
    infoColorSuppl: '#118AB2',
    
    successColor: '#06D6A0',
    successColorHover: '#10e3a5',
    successColorPressed: '#05b88a',
    successColorSuppl: '#06D6A0',
    
    warningColor: '#FFD166',
    warningColorHover: '#ffe085',
    warningColorPressed: '#e6bc5c',
    warningColorSuppl: '#FFD166',
    
    errorColor: '#EF476F',
    errorColorHover: '#f16583',
    errorColorPressed: '#d63d5f',
    errorColorSuppl: '#EF476F',
    
    textColorBase: '#F9FAFB',
    textColor1: '#F9FAFB',
    textColor2: '#E5E7EB',
    textColor3: '#9CA3AF',
    textColorDisabled: '#6B7280',
    
    placeholderColor: '#6B7280',
    placeholderColorDisabled: '#4B5563',
    
    bodyColor: '#111827',
    cardColor: '#1F2937',
    modalColor: '#1F2937',
    popoverColor: '#1F2937',
    tableColor: '#1F2937',
    tableColorHover: '#374151',
    tableColorStriped: '#252D3B',
    inputColor: '#1F2937',
    inputColorDisabled: '#252D3B',
    actionColor: '#374151',
    hoverColor: '#374151',
    borderColor: '#374151',
    dividerColor: '#374151',
    borderRadius: '8px',
    borderRadiusSmall: '6px',
    
    heightNavbar: '64px',
    heightLogo: '64px',
    
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif",
    fontFamilyMono: "'JetBrains Mono', 'Fira Code', monospace",
    fontSize: '14px',
    fontSizeMini: '12px',
    fontSizeTiny: '12px',
    fontSizeSmall: '13px',
    fontSizeMedium: '14px',
    fontSizeLarge: '15px',
    fontSizeHuge: '16px',
  },
  Layout: {
    siderColor: '#0F4C5C',
    headerColor: '#1F2937',
    bodyColor: '#111827',
    textColor: '#F9FAFB',
    textColorPrimary: '#F9FAFB',
    textColorSecondary: '#9CA3AF',
    headerBorderColor: '#374151',
    siderBorderColor: '#374151',
  },
  Menu: {
    darkColor: '#0F4C5C',
    darkColorHover: '#186a7d',
    darkColorPressed: '#0a3644',
    darkColorActive: '#0F4C5C',
    darkItemColorActive: 'rgba(251, 139, 36, 0.15)',
    darkItemColorActiveHover: 'rgba(251, 139, 36, 0.2)',
    darkItemTextColorActive: '#FB8B24',
    darkItemTextColorActiveHover: '#FB8B24',
    darkSubmenuItemColorActive: 'rgba(251, 139, 36, 0.1)',
    darkItemColorHover: 'rgba(255, 255, 255, 0.08)',
    darkItemTextColor: '#E5E7EB',
    darkSubmenuItemTextColor: '#9CA3AF',
    itemIconColor: '#9CA3AF',
    itemIconColorHover: '#E5E7EB',
    itemIconColorActive: '#FB8B24',
    itemTextColor: '#E5E7EB',
    itemTextColorHover: '#FFFFFF',
    itemTextColorActive: '#FB8B24',
    itemColorActive: 'rgba(251, 139, 36, 0.15)',
    itemColorHover: 'rgba(255, 255, 255, 0.08)',
  },
  Button: {
    // Default 类型按钮配置
    colorDefault: '#1F2937',
    colorHoverDefault: 'rgba(251, 139, 36, 0.15)',
    colorPressedDefault: 'rgba(251, 139, 36, 0.2)',
    colorFocusDefault: 'rgba(251, 139, 36, 0.15)',
    colorDisabledDefault: '#252D3B',
    borderColorDefault: '#374151',
    borderColorHoverDefault: '#FB8B24',
    borderColorPressedDefault: '#FB8B24',
    borderColorFocusDefault: '#FB8B24',
    borderColorDisabledDefault: '#374151',
    textColorDefault: '#F9FAFB',
    textColorHoverDefault: '#FB8B24',
    textColorPressedDefault: '#FB8B24',
    textColorFocusDefault: '#FB8B24',
    textColorDisabledDefault: '#6B7280',
    
    // Primary 类型按钮配置 - 深色主题使用归藏色
    textColorPrimary: '#FFFFFF',
    textColorHoverPrimary: '#FFFFFF',
    textColorPressedPrimary: '#FFFFFF',
    textColorFocusPrimary: '#FFFFFF',
    colorPrimary: '#0F4C5C',
    colorHoverPrimary: '#186a7d',
    colorPressedPrimary: '#0a3644',
    colorFocusPrimary: '#0F4C5C',
    borderPrimary: '#0F4C5C',
    borderHoverPrimary: '#186a7d',
    borderPressedPrimary: '#0a3644',
    borderFocusPrimary: '#0F4C5C',
    
    // Tertiary 类型按钮配置
    colorTertiary: 'transparent',
    colorHoverTertiary: 'rgba(251, 139, 36, 0.15)',
    colorPressedTertiary: 'rgba(251, 139, 36, 0.2)',
    colorFocusTertiary: 'rgba(251, 139, 36, 0.15)',
    textColorTertiary: '#F9FAFB',
    textColorHoverTertiary: '#FB8B24',
    textColorPressedTertiary: '#FB8B24',
    textColorFocusTertiary: '#FB8B24',
    
    borderRadiusSmall: '6px',
    borderRadiusMedium: '8px',
    borderRadiusLarge: '10px',
  },
  Card: {
    color: '#1F2937',
    colorModal: '#1F2937',
    borderColor: '#374151',
    titleTextColor: '#F9FAFB',
    textColor: '#9CA3AF',
    borderRadius: '12px',
  },
  Input: {
    color: '#1F2937',
    colorFocus: '#1F2937',
    colorDisabled: '#252D3B',
    borderColor: '#374151',
    borderColorHover: '#4B5563',
    borderColorFocus: '#0F4C5C',
    boxShadowFocus: '0 0 0 2px rgba(15, 76, 92, 0.2)',
    textColor: '#F9FAFB',
    placeholderColor: '#6B7280',
  },
  Select: {
    peers: {
      InternalSelection: {
        color: '#1F2937',
        colorActive: '#1F2937',
        colorDisabled: '#252D3B',
        borderColor: '#374151',
        borderColorHover: '#4B5563',
        borderColorFocus: '#0F4C5C',
        textColor: '#F9FAFB',
        placeholderColor: '#6B7280',
        boxShadowFocus: '0 0 0 2px rgba(15, 76, 92, 0.2)',
      },
      InternalSelectMenu: {
        color: '#1F2937',
        optionColorPending: '#374151',
        optionColorActive: 'rgba(15, 76, 92, 0.15)',
        optionTextColor: '#E5E7EB',
        optionTextColorActive: '#F9FAFB',
        optionTextColorPending: '#F9FAFB',
      }
    }
  },
  Table: {
    thColor: '#374151',
    tdColor: '#1F2937',
    tdColorHover: '#252D3B',
    thTextColor: '#F9FAFB',
    tdTextColor: '#E5E7EB',
    borderColor: '#374151',
    thColorModal: '#374151',
    tdColorModal: '#1F2937',
  },
  DataTable: {
    thColor: '#374151',
    tdColor: '#1F2937',
    tdColorHover: '#252D3B',
    tdColorStriped: '#252D3B',
    thTextColor: '#F9FAFB',
    tdTextColor: '#E5E7EB',
    borderColor: '#374151',
  },
  Tag: {
    borderRadius: '6px',
    colorBordered: '#374151',
  },
  Dialog: {
    color: '#1F2937',
    textColor: '#F9FAFB',
    titleTextColor: '#F9FAFB',
  },
  Modal: {
    color: '#1F2937',
    textColor: '#F9FAFB',
    titleTextColor: '#F9FAFB',
  },
  Message: {
    color: '#1F2937',
    textColor: '#F9FAFB',
  },
  Notification: {
    color: '#1F2937',
    textColor: '#F9FAFB',
    titleTextColor: '#F9FAFB',
  },
  Dropdown: {
    color: '#1F2937',
    colorHover: '#374151',
    optionColorHover: '#374151',
    optionTextColor: '#E5E7EB',
    optionTextColorHover: '#F9FAFB',
  },
  Tooltip: {
    color: '#374151',
    textColor: '#F9FAFB',
  },
  Popover: {
    color: '#1F2937',
    textColor: '#F9FAFB',
  },
  Badge: {
    color: '#EF476F',
  },
  Switch: {
    railColorActive: '#0F4C5C',
    iconColor: '#FB8B24',
  },
  Radio: {
    buttonColorActive: '#0F4C5C',
    buttonTextColorActive: '#FFFFFF',
  },
  Checkbox: {
    colorChecked: '#0F4C5C',
    borderChecked: '#0F4C5C',
  },
  Tabs: {
    tabTextColorActiveLine: '#FB8B24',
    tabTextColorLine: '#9CA3AF',
    barColor: '#FB8B24',
    tabColor: '#0F4C5C',
  },
  Tree: {
    nodeColorHover: '#374151',
    nodeColorActive: 'rgba(15, 76, 92, 0.15)',
    nodeTextColor: '#E5E7EB',
    nodeTextColorHover: '#F9FAFB',
    nodeTextColorActive: '#FB8B24',
  },
  DatePicker: {
    panelColor: '#1F2937',
    itemColorHover: '#374151',
    itemColorActive: '#0F4C5C',
    itemTextColor: '#E5E7EB',
    itemTextColorActive: '#FFFFFF',
    itemTextColorHover: '#F9FAFB',
    calendarTitleTextColor: '#F9FAFB',
  },
  Pagination: {
    itemColor: '#1F2937',
    itemColorHover: '#374151',
    itemColorActive: '#0F4C5C',
    itemBorderColor: '#374151',
    itemBorderColorHover: '#4B5563',
    itemBorderColorActive: '#0F4C5C',
    itemTextColor: '#E5E7EB',
    itemTextColorHover: '#F9FAFB',
    itemTextColorActive: '#FFFFFF',
  }
};

// 浅色主题配置覆盖
const lightThemeOverrides = {
  common: {
    primaryColor: '#0F4C5C',
    primaryColorHover: '#186a7d',
    primaryColorPressed: '#0a3644',
    primaryColorSuppl: '#0F4C5C',
    primaryColorSuccess: '#06D6A0',
    primaryColorWarning: '#FFD166',
    primaryColorError: '#EF476F',
    primaryColorInfo: '#118AB2',
    
    borderRadius: '8px',
    borderRadiusSmall: '6px',
    
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif",
    fontFamilyMono: "'JetBrains Mono', 'Fira Code', monospace",
  },
  Menu: {
    itemColorActive: 'rgba(251, 139, 36, 0.1)',
    itemColorActiveHover: 'rgba(251, 139, 36, 0.15)',
    itemTextColorActive: '#FB8B24',
    itemTextColorActiveHover: '#FB8B24',
  },
  Button: {
    // Default 类型按钮配置
    colorDefault: '#FFFFFF',
    colorHoverDefault: 'rgba(251, 139, 36, 0.08)',
    colorPressedDefault: 'rgba(251, 139, 36, 0.12)',
    colorFocusDefault: 'rgba(251, 139, 36, 0.08)',
    colorDisabledDefault: '#F8F9FA',
    borderColorDefault: '#E5E7EB',
    borderColorHoverDefault: '#0F4C5C',
    borderColorPressedDefault: '#0F4C5C',
    borderColorFocusDefault: '#0F4C5C',
    borderColorDisabledDefault: '#E5E7EB',
    textColorDefault: '#0F4C5C',
    textColorHoverDefault: '#0F4C5C',
    textColorPressedDefault: '#0F4C5C',
    textColorFocusDefault: '#0F4C5C',
    textColorDisabledDefault: '#9CA3AF',
    
    // Primary 类型按钮配置
    colorPrimary: '#FB8B24',
    colorHoverPrimary: '#fca03d',
    colorPressedPrimary: '#e67a1a',
    colorFocusPrimary: '#FB8B24',
    borderPrimary: '#FB8B24',
    borderHoverPrimary: '#fca03d',
    borderPressedPrimary: '#e67a1a',
    borderFocusPrimary: '#FB8B24',
    textColorPrimary: '#FFFFFF',
    textColorHoverPrimary: '#FFFFFF',
    textColorPressedPrimary: '#FFFFFF',
    textColorFocusPrimary: '#FFFFFF',
    
    // Tertiary 类型按钮配置
    colorTertiary: 'transparent',
    colorHoverTertiary: 'rgba(251, 139, 36, 0.08)',
    colorPressedTertiary: 'rgba(251, 139, 36, 0.12)',
    colorFocusTertiary: 'rgba(251, 139, 36, 0.08)',
    textColorTertiary: '#0F4C5C',
    textColorHoverTertiary: '#FB8B24',
    textColorPressedTertiary: '#e67a1a',
    textColorFocusTertiary: '#FB8B24',
    
    borderRadiusSmall: '6px',
    borderRadiusMedium: '8px',
    borderRadiusLarge: '10px',
  },
  Card: {
    borderRadius: '12px',
  },
  Tabs: {
    tabTextColorActiveLine: '#FB8B24',
    barColor: '#FB8B24',
  }
};

// 主题
const theme = computed(() => {
  return appStore.isDark ? darkTheme : null;
});

const themeOverrides = computed(() => {
  return appStore.isDark ? darkThemeOverrides : lightThemeOverrides;
});

// 语言设置
const locale = computed(() => {
  return appStore.language === 'zh-CN' ? zhCN : enUS;
});

const dateLocale = computed(() => {
  return appStore.language === 'zh-CN' ? dateZhCN : dateEnUS;
});

// 监听语言变化，同步到 i18n
watch(() => appStore.language, (newLang) => {
  i18nLocale.value = newLang;
  document.documentElement.setAttribute('lang', newLang);
}, { immediate: true });

// 初始化
onMounted(() => {
  // 主题已在 store 初始化时通过 watch(isDark, ..., { immediate: true }) 自动应用
  
  // 监听系统主题变化
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
  const handleThemeChange = (e: MediaQueryListEvent) => {
    const savedTheme = localStorage.getItem('admin_theme');
    if (!savedTheme) {
      appStore.setDarkTheme?.(e.matches);
    }
  };
  
  try {
    mediaQuery.addEventListener('change', handleThemeChange);
  } catch (e) {
    mediaQuery.addListener(handleThemeChange);
  }
});

// 监听主题变化，同步到 html 元素
watch(() => appStore.isDark, (isDark) => {
  const html = document.documentElement;
  if (isDark) {
    html.classList.add('dark');
    html.setAttribute('data-theme', 'dark');
  } else {
    html.classList.remove('dark');
    html.setAttribute('data-theme', 'light');
  }
}, { immediate: true });
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

/* 深色主题切换过渡效果 */
html {
  transition: background-color 0.3s ease, color 0.3s ease;
}

/* 深色主题下的过渡效果 */
.dark,
.dark * {
  transition-property: background-color, border-color, color, box-shadow;
  transition-duration: 0.2s;
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

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

.dark ::-webkit-scrollbar-thumb {
  background: #4b5563;
}

.dark ::-webkit-scrollbar-thumb:hover {
  background: #6b7280;
}
</style>
