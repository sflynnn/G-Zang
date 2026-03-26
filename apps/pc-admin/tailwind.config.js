/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#0F4C5C',      // 归藏青 - 侧边栏背景、标题文本
        secondary: '#FB8B24',    // 琉璃金 - CTA按钮、重要操作
        success: '#06D6A0',      // 利润绿 - 成功状态、统计图表
        danger: '#EF476F',       // 支出红 - 错误状态、警告信息
        background: '#F8F9FA',   // 浅灰背景 - 主内容区背景
        surface: '#FFFFFF',      // 白色 - 卡片、表格背景
        // 扩展颜色用于深色主题
        'primary-dark': '#1a5f73',
        'background-dark': '#1a1a1a',
        'surface-dark': '#2a2a2a',
      },
      fontFamily: {
        'sans': ['Inter', 'ui-sans-serif', 'system-ui'],
        'chinese': ['PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        'mono': ['DIN Alternate', 'Roboto Mono', 'ui-monospace', 'monospace'],
      },
      spacing: {
        '18': '4.5rem',
        '88': '22rem',
      },
      borderRadius: {
        '4xl': '2rem',
      },
      boxShadow: {
        'admin': '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
        'admin-lg': '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
      }
    },
  },
  plugins: [
    require('@tailwindcss/typography'),
  ],
}
