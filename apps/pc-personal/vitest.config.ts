import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
  ],
  test: {
    globals: true,
    environment: 'jsdom',
    include: ['src/**/*.{test,spec}.{js,ts}', 'tests/**/*.{test,spec}.{js,ts}'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      reportsDirectory: 'coverage',
      exclude: [
        'node_modules/',
        'dist/',
        'src/main.ts',
        'src/router/**',
        '**/*.d.ts',
        '**/*.config.{js,ts}',
      ],
    },
    setupFiles: ['./tests/setup.ts'],
    testTimeout: 10000,
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
})
