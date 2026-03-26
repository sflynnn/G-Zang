<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h2>仪表板</h2>
      <p>欢迎使用 G-Zang 管理系统</p>
    </div>

    <div>
      <h3>系统状态正常</h3>
      <p>Dashboard页面已成功加载</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <n-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <n-icon size="32" :component="PeopleOutline" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.users }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </div>
      </n-card>

      <n-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <n-icon size="32" :component="BusinessOutline" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.companies }}</div>
            <div class="stat-label">总公司数</div>
          </div>
        </div>
      </n-card>

      <n-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <n-icon size="32" :component="WalletOutline" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ formatCurrency(stats.totalBalance) }}</div>
            <div class="stat-label">总余额</div>
          </div>
        </div>
      </n-card>

      <n-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <n-icon size="32" :component="DocumentTextOutline" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.transactions }}</div>
            <div class="stat-label">总交易数</div>
          </div>
        </div>
      </n-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <n-card title="月度收支趋势" class="chart-card">
        <div class="chart-placeholder">
          <n-icon size="64" :component="BarChartOutline" />
          <p>月度收支趋势图表</p>
        </div>
      </n-card>

      <n-card title="分类统计" class="chart-card">
        <div class="chart-placeholder">
          <n-icon size="64" :component="PieChartOutline" />
          <p>分类统计饼图</p>
        </div>
      </n-card>
    </div>

    <!-- 最近活动 -->
    <n-card title="最近活动" class="activity-card">
      <n-timeline>
        <n-timeline-item
          v-for="activity in recentActivities"
          :key="activity.id"
          :title="activity.title"
          :content="activity.content"
          :time="activity.time"
          type="success"
        />
      </n-timeline>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import {
  PeopleOutline,
  BusinessOutline,
  WalletOutline,
  DocumentTextOutline,
  BarChartOutline,
  PieChartOutline
} from '@vicons/ionicons5';

// 统计数据
const stats = ref({
  users: 0,
  companies: 0,
  totalBalance: 0,
  transactions: 0
});

// 最近活动
const recentActivities = ref([
  {
    id: 1,
    title: '新用户注册',
    content: '用户张三完成了注册',
    time: '2024-01-18 10:30'
  },
  {
    id: 2,
    title: '交易记录',
    content: '用户李四添加了新的交易记录',
    time: '2024-01-18 09:15'
  },
  {
    id: 3,
    title: '公司创建',
    content: '新公司"ABC公司"已创建',
    time: '2024-01-18 08:45'
  }
]);

// 格式化货币
const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY'
  }).format(amount);
};

// 获取统计数据
const fetchStats = async () => {
  try {
    // TODO: 调用API获取统计数据
    // 这里先模拟数据
    stats.value = {
      users: 1250,
      companies: 45,
      totalBalance: 1250000.50,
      transactions: 15680
    };
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
};

onMounted(() => {
  fetchStats();
});
</script>

<style scoped>
.dashboard {
  padding: 1rem;
}

.dashboard-header {
  margin-bottom: 2rem;
}

.dashboard-header h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--n-text-color);
}

.dashboard-header p {
  color: var(--n-text-color-3);
  font-size: 0.9rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.stat-card {
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.stat-icon {
  color: var(--n-color-primary);
  opacity: 0.8;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--n-text-color);
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.85rem;
  color: var(--n-text-color-3);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.chart-card {
  height: 300px;
}

.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: var(--n-text-color-3);
}

.chart-placeholder p {
  margin-top: 1rem;
  font-size: 0.9rem;
}

.activity-card {
  margin-top: 2rem;
}
</style>
