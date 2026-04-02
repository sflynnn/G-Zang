/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#0F4C5C',
          light: '#186a7d',
          dark: '#0a3644',
        },
        secondary: {
          DEFAULT: '#FB8B24',
          light: '#fca03d',
          dark: '#e67a1a',
        },
        success: '#06D6A0',
        danger: '#EF476F',
        warning: '#FFD166',
        info: '#118AB2',
        background: {
          DEFAULT: '#F8F9FA',
          dark: '#111827',
        },
        surface: {
          DEFAULT: '#FFFFFF',
          dark: '#1F2937',
        },
        'surface-elevated': {
          DEFAULT: '#FFFFFF',
          dark: '#374151',
        },
        'text-primary': {
          DEFAULT: '#1F2937',
          dark: '#F9FAFB',
        },
        'text-secondary': {
          DEFAULT: '#6B7280',
          dark: '#9CA3AF',
        },
        'text-tertiary': {
          DEFAULT: '#9CA3AF',
          dark: '#6B7280',
        },
        border: {
          DEFAULT: '#E5E7EB',
          dark: '#374151',
        },
        // AdminLayout 专用深色颜色
        'dark-bg': '#111827',
        'dark-surface': '#1F2937',
        'dark-surface-elevated': '#374151',
        'dark-text-primary': '#F9FAFB',
        'dark-text-secondary': '#9CA3AF',
        'dark-text-tertiary': '#6B7280',
        'dark-border': '#374151',
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
        'gzang': '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
        'gzang-md': '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1)',
        'gzang-lg': '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1)',
      }
    },
  },
  plugins: [
    require('@tailwindcss/typography'),
  ],
}
