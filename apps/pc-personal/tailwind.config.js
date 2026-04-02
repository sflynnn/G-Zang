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
          50: '#E8F4F6',
          100: '#C5E2E7',
          200: '#9FCFD8',
          300: '#79BCC9',
          400: '#53A9BA',
          500: '#0F4C5C',
          600: '#0D4250',
          700: '#0A3542',
          800: '#082834',
          900: '#051B26',
          light: '#186a7d',
          dark: '#0a3644',
        },
        secondary: {
          DEFAULT: '#FB8B24',
          50: '#FEF3E6',
          100: '#FDE3C3',
          200: '#FBCDA1',
          300: '#F9B77F',
          400: '#F7A15D',
          500: '#FB8B24',
          600: '#E67A1A',
          700: '#CC6A16',
          800: '#B35A12',
          900: '#994A0E',
          light: '#fca03d',
          dark: '#e67a1a',
        },
        success: '#06D6A0',
        danger: '#EF476F',
        warning: '#FFD166',
        info: '#118AB2',
        background: '#F8F9FA',
        surface: '#FFFFFF',
        'text-primary': '#1F2937',
        'text-secondary': '#6B7280',
        border: '#E5E7EB',
        // Dark theme colors - using dark: variant syntax
        dark: {
          bg: '#111827',
          surface: '#1F2937',
          'surface-elevated': '#374151',
          'text-primary': '#F9FAFB',
          'text-secondary': '#9CA3AF',
          'text-tertiary': '#6B7280',
          border: '#374151',
        },
      },
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
        chinese: ['PingFang SC', 'Microsoft YaHei', 'sans-serif'],
        mono: ['DIN Alternate', 'Roboto Mono', 'ui-monospace', 'monospace'],
      },
      spacing: {
        '18': '4.5rem',
        '88': '22rem',
      },
      borderRadius: {
        '4xl': '2rem',
      },
      boxShadow: {
        'gzang': '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
        'gzang-md': '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1)',
        'gzang-lg': '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1)',
        'gzang-xl': '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1)',
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-out',
        'slide-up': 'slideUp 0.5s ease-out',
        'pulse-slow': 'pulse 3s infinite',
        'float': 'float 3s ease-in-out infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { opacity: '0', transform: 'translateY(20px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-10px)' },
        },
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'gradient-primary': 'linear-gradient(135deg, #0F4C5C 0%, #186a7d 100%)',
        'gradient-secondary': 'linear-gradient(135deg, #FB8B24 0%, #fca03d 100%)',
        'gradient-dark-surface': 'linear-gradient(180deg, #1F2937 0%, #111827 100%)',
      },
    },
  },
  plugins: [
    require('@tailwindcss/typography'),
  ],
}
