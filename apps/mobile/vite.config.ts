import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig(({ mode, command }) => {
  const plugins = [uni()]

  return {
    plugins,
    resolve: {
      alias: {
        '@': resolve(__dirname, 'src'),
        '@api': resolve(__dirname, 'src/api'),
        '@components': resolve(__dirname, 'src/components'),
        '@pages': resolve(__dirname, 'src/pages'),
        '@stores': resolve(__dirname, 'src/stores'),
        '@utils': resolve(__dirname, 'src/utils'),
        '@types': resolve(__dirname, 'src/types'),
        '@shared': resolve(__dirname, '../../packages/shared/src')
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
    server: {
      host: '0.0.0.0',
      port: 5173,
      proxy: {
        '/api': {
          target: 'http://localhost:8081',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, '/api')
        }
      }
    },
    build: {
      target: 'es2015',
      cssCodeSplit: true,
      sourcemap: false,
      minify: 'terser',
      terserOptions: {
        compress: {
          drop_console: true,
          drop_debugger: true
        }
      },
      // 依赖预构建优化
      rollupOptions: {
        output: {
          // 小程序分包配置
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: 'static/[ext]/[name]-[hash].[ext]',
          // 手动分包
          manualChunks: {
            'vendor-vue': ['vue', '@vue/runtime-core', '@vue/runtime-dom', '@vue/reactivity'],
            'vendor-uni': ['@dcloudio/uni-app', '@dcloudio/uni-h5', '@dcloudio/uni-mp-weixin'],
            'vendor-utils': ['dayjs', 'decimal.js', 'lodash-es']
          }
        }
      },
      // 启用 gzip 压缩
      reportCompressedSize: true,
      // chunk 大小警告阈值
      chunkSizeWarningLimit: 500
    },
    // 依赖预构建
    optimizeDeps: {
      include: [
        'vue',
        'pinia',
        'dayjs',
        'decimal.js',
        'lodash-es',
        '@vueuse/core'
      ]
    }
  }
})
