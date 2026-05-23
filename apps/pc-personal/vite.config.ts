import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import qiankun from 'vite-plugin-qiankun';
import { resolve } from 'path';
import { visualizer } from 'rollup-plugin-visualizer';

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
    origin: 'http://localhost:8081',
    proxy: {
      '/api/v1': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/v1/, '/api/mobile')
      }
    }
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler',
        additionalData: `@use "@/styles/variables.scss" as *;`
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
        },
        // 手动分包配置
        manualChunks: {
          'vendor-vue': ['vue', 'vue-router', 'pinia'],
          'vendor-ui': ['naive-ui'],
          'vendor-echarts': ['echarts']
        }
      }
    },
    // 启用 gzip 压缩
    reportCompressedSize: true,
    // 压缩算法
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    }
  },
  // 依赖预构建
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'naive-ui', 'echarts', 'axios']
  },
  // 生产环境构建分析
  preview: {
    port: 8081
  }
});
