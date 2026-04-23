/**
 * G-Zang Mobile i18n Configuration
 * 归藏财务管理系统 - 移动端国际化配置
 */

import { createI18n } from 'vue-i18n';
import zhCN from '@/locales/zh-CN';
import enUS from '@/locales/en-US';

export type LocaleType = 'zh-CN' | 'en-US';

export const availableLocales: { label: string; value: LocaleType }[] = [
  { label: '简体中文', value: 'zh-CN' },
  { label: 'English', value: 'en-US' },
];

// 从本地存储读取保存的语言设置，默认为中文
const savedLocale = uni.getStorageSync('language') as LocaleType;
const systemLocale = (typeof navigator !== 'undefined' && navigator.language?.startsWith('en')) ? 'en-US' : 'zh-CN';

export const i18n = createI18n({
  legacy: false,
  locale: savedLocale || systemLocale,
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS,
  },
  globalInjection: true,
  datetimeFormats: {
    'zh-CN': {
      short: {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
      },
      long: {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
      },
      monthDay: {
        month: 'long',
        day: 'numeric',
      },
    },
    'en-US': {
      short: {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      },
      long: {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
      },
      monthDay: {
        month: 'long',
        day: 'numeric',
      },
    },
  },
  numberFormats: {
    'zh-CN': {
      currency: {
        style: 'currency',
        currency: 'CNY',
        currencyDisplay: 'symbol',
      },
      decimal: {
        style: 'decimal',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      },
      percent: {
        style: 'percent',
        useGrouping: false,
      },
    },
    'en-US': {
      currency: {
        style: 'currency',
        currency: 'USD',
        currencyDisplay: 'symbol',
      },
      decimal: {
        style: 'decimal',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      },
      percent: {
        style: 'percent',
        useGrouping: false,
      },
    },
  },
});

export default i18n;

/**
 * 切换语言
 */
export function setLocale(locale: LocaleType) {
  i18n.global.locale.value = locale;
  uni.setStorageSync('language', locale);

  // 更新日期时间格式化语言
  const dateLocales: Record<LocaleType, string> = {
    'zh-CN': 'zh-cn',
    'en-US': 'en',
  };

  return { locale, dateLocale: dateLocales[locale] };
}

/**
 * 获取当前语言
 */
export function getLocale(): LocaleType {
  return i18n.global.locale.value as LocaleType;
}

/**
 * 获取当前日期格式化语言
 */
export function getDateLocale(): string {
  const locale = getLocale();
  const dateLocales: Record<LocaleType, string> = {
    'zh-CN': 'zh-cn',
    'en-US': 'en',
  };
  return dateLocales[locale];
}
