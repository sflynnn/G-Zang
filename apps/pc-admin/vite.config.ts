import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import qiankun from 'vite-plugin-qiankun';
import { resolve } from 'path';

export default defineConfig({
  plugins: [
    vue(),
    qiankun('pc-admin', { useDevMode: true })
  ],
  server: {
    port: 3000,
    cors: true,
    origin: 'http://localhost:3000',
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  build: {
    target: 'esnext',
    lib: {
      entry: resolve(__dirname, 'src/main.ts'),
      name: 'pc-admin',
      fileName: 'pc-admin',
      formats: ['umd']
    },
    rollupOptions: {
      external: ['vue', 'vue-router', 'pinia'],
      output: {
        globals: {
          vue: 'Vue',
          'vue-router': 'VueRouter',
          pinia: 'Pinia'
        }
      }
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler'
      }
    }
  }
});
