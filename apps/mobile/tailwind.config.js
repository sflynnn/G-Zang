/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.{vue,js,ts,jsx,tsx}',
    './src/**/*.vue',
  ],
  theme: {
    extend: {
      colors: {
        // G-Zang 品牌色 - 归藏青
        primary: {
          DEFAULT: '#0F4C5C',
          light: '#186a7d',
          dark: '#0a3644',
        },
        // G-Zang 品牌色 - 琉璃金
        secondary: {
          DEFAULT: '#FB8B24',
          light: '#fca03d',
          dark: '#e67a1a',
        },
        // 功能色
        success: '#06D6A0',
        danger: '#EF476F',
        warning: '#FFD166',
        info: '#118AB2',
        // 中性色
        background: '#F8F9FA',
        surface: '#FFFFFF',
        'text-primary': '#1F2937',
        'text-secondary': '#6B7280',
        border: '#E5E7EB',
      },
      fontFamily: {
        ui: ['Inter', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'sans-serif'],
        cn: ['PingFang SC', 'Microsoft YaHei', 'Hiragino Sans GB', 'sans-serif'],
        mono: ['Roboto Mono', 'JetBrains Mono', 'SF Mono', 'monospace'],
      },
      spacing: {
        '1': '0.25rem',
        '2': '0.5rem',
        '3': '0.75rem',
        '4': '1rem',
        '5': '1.25rem',
        '6': '1.5rem',
        '8': '2rem',
        '10': '2.5rem',
        '12': '3rem',
        '16': '4rem',
      },
      borderRadius: {
        sm: '0.25rem',
        md: '0.5rem',
        lg: '0.75rem',
        xl: '1rem',
        '2xl': '1.5rem',
      },
      boxShadow: {
        sm: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
        md: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
        lg: '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)',
        xl: '0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)',
      },
    },
  },
  plugins: [],
  corePlugins: {
    preflight: false, // 关闭 preflight，避免与 uni-app 样式冲突
  },
}
