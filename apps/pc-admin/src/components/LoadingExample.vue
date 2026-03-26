<template>
  <div class="loading-example">
    <h2>Loading System Examples</h2>

    <!-- 全局加载示例 -->
    <section class="example-section">
      <h3>全局加载 (Automatic via Axios)</h3>
      <div class="button-group">
        <n-button @click="testGlobalLoading" :loading="globalLoading">
          测试全局加载 (3秒)
        </n-button>
        <n-button @click="testSkipLoading" type="info">
          跳过全局加载
        </n-button>
        <n-button @click="testCustomText" type="warning">
          自定义加载文本
        </n-button>
      </div>
    </section>

    <!-- 局部加载示例 -->
    <section class="example-section">
      <h3>局部加载 (v-loading Directive)</h3>
      <div class="loading-examples">
        <div
          v-loading="localLoading1"
          class="loading-box"
          @click="testLocalLoading1"
        >
          <h4>基础用法</h4>
          <p>点击测试局部加载</p>
        </div>

        <div
          v-loading="{ loading: localLoading2, text: '自定义加载文本...' }"
          class="loading-box"
          @click="testLocalLoading2"
        >
          <h4>自定义文本</h4>
          <p>点击测试自定义文本</p>
        </div>

        <div
          v-loading:chart-section="{ loading: localLoading3, text: chartLoadingText }"
          class="loading-box chart-box"
          @click="testLocalLoading3"
        >
          <h4>命名局部加载</h4>
          <p>点击测试命名加载</p>
        </div>
      </div>
    </section>

    <!-- 编程式局部加载示例 -->
    <section class="example-section">
      <h3>编程式局部加载 (Composable)</h3>
      <div class="button-group">
        <n-button @click="testProgrammaticLoading" :loading="programmaticLoading.isLoading()">
          测试编程式加载
        </n-button>
        <n-button @click="testProgrammaticWithText" type="success">
          带文本的编程式加载
        </n-button>
      </div>

      <div
        v-loading:programmatic-section="programmaticLoading.isLoading()"
        class="loading-box programmatic-box"
      >
        <h4>编程式控制区域</h4>
        <p>当前状态: {{ programmaticLoading.isLoading() ? '加载中' : '空闲' }}</p>
        <p v-if="programmaticLoading.isLoading()">加载文本: {{ programmaticLoading.text() }}</p>
      </div>
    </section>

    <!-- 并发请求示例 -->
    <section class="example-section">
      <h3>并发请求处理</h3>
      <div class="button-group">
        <n-button @click="testConcurrentRequests" :loading="concurrentLoading">
          测试并发请求 (3个同时)
        </n-button>
      </div>
      <p class="note">注意：只有所有请求完成后加载状态才会消失</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useLoadingStore, useLocalLoading } from '@/utils/loading';
import { httpClient, HttpClient } from '@/utils/http';
import api from '@/api';

// Store
const loadingStore = useLoadingStore();

// 局部状态
const globalLoading = ref(false);
const localLoading1 = ref(false);
const localLoading2 = ref(false);
const localLoading3 = ref(false);
const chartLoadingText = ref('正在加载图表...');
const concurrentLoading = ref(false);

// 编程式加载
const programmaticLoading = useLocalLoading('programmatic-section');

// 测试方法

// 全局加载测试
const testGlobalLoading = async () => {
  globalLoading.value = true;
  try {
    await httpClient.get('/api/test-delay?delay=3000');
  } finally {
    globalLoading.value = false;
  }
};

// 跳过加载测试
const testSkipLoading = async () => {
  try {
    await api.get('/api/heartbeat', HttpClient.skipLoading());
    alert('请求完成 (无加载状态)');
  } catch (error) {
    console.log('Heartbeat request completed without loading');
  }
};

// 自定义文本测试
const testCustomText = async () => {
  try {
    await api.get('/api/test-delay?delay=2000', HttpClient.withLoadingText('正在执行特殊操作...'));
  } catch (error) {
    console.log('Custom text loading test completed');
  }
};

// 局部加载测试
const testLocalLoading1 = async () => {
  localLoading1.value = true;
  await new Promise(resolve => setTimeout(resolve, 2000));
  localLoading1.value = false;
};

const testLocalLoading2 = async () => {
  localLoading2.value = true;
  await new Promise(resolve => setTimeout(resolve, 2500));
  localLoading2.value = false;
};

const testLocalLoading3 = async () => {
  localLoading3.value = true;
  chartLoadingText.value = '正在分析数据...';
  await new Promise(resolve => setTimeout(resolve, 3000));
  localLoading3.value = false;
};

// 编程式加载测试
const testProgrammaticLoading = async () => {
  await programmaticLoading.withLoading(async () => {
    await new Promise(resolve => setTimeout(resolve, 2000));
  });
};

const testProgrammaticWithText = async () => {
  programmaticLoading.start('执行复杂计算中...');
  await new Promise(resolve => setTimeout(resolve, 3000));
  programmaticLoading.stop();
};

// 并发请求测试
const testConcurrentRequests = async () => {
  concurrentLoading.value = true;
  try {
    // 同时发送3个请求
    const promises = [
      httpClient.get('/api/test-delay?delay=1000'),
      httpClient.get('/api/test-delay?delay=2000'),
      httpClient.get('/api/test-delay?delay=1500')
    ];

    await Promise.all(promises);
  } finally {
    concurrentLoading.value = false;
  }
};
</script>

<style scoped>
.loading-example {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.example-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.example-section h3 {
  margin-bottom: 15px;
  color: #1f2937;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.loading-examples {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.loading-box {
  padding: 20px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: #f9fafb;
  cursor: pointer;
  transition: all 0.2s;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.loading-box:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.loading-box h4 {
  margin: 0 0 8px 0;
  color: #374151;
}

.loading-box p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.chart-box {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.chart-box h4,
.chart-box p {
  color: white;
}

.programmatic-box {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.programmatic-box h4,
.programmatic-box p {
  color: white;
}

.note {
  color: #6b7280;
  font-size: 14px;
  font-style: italic;
}
</style>
