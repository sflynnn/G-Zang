import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 8082,
    cors: true,
    headers: {
      'Access-Control-Allow-Origin': '*'
    }
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  build: {
    target: 'esnext'
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler'  // 使用新版 Sass API，消除了 legacy JS API 警告
      }
    }
  }
});
