import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import qiankun from 'vite-plugin-qiankun';
import { resolve } from 'path';

export default defineConfig({
  plugins: [
    vue(),
    qiankun('pc-personal', {
      useDevMode: true
    })
  ],
  server: {
    port: 8081,
    cors: true,
    origin: 'http://localhost:8081'
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@import "@/styles/variables.scss";'
      }
    }
  },
  build: {
    target: 'esnext',
    lib: {
      entry: resolve(__dirname, 'src/main.ts'),
      name: 'pc-personal',
      fileName: 'pc-personal',
      formats: ['umd']
    },
    rollupOptions: {
      external: ['vue', 'vue-router'],
      output: {
        globals: {
          vue: 'Vue',
          'vue-router': 'VueRouter'
        }
      }
    }
  }
});
