<template>
  <div class="dashboard">
    <n-space vertical size="large">
      <!-- 统计卡片 -->
      <n-grid cols="1 s:2 l:4" responsive="screen" x-gap="12" y-gap="12">
        <n-grid-item>
          <n-card>
            <n-statistic title="本月收入" :value="monthlyIncome" precision="2">
              <template #prefix>
                <n-icon color="#52c41a">
                  <TrendingUp />
                </n-icon>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>

        <n-grid-item>
          <n-card>
            <n-statistic title="本月支出" :value="monthlyExpense" precision="2">
              <template #prefix>
                <n-icon color="#ff4d4f">
                  <TrendingDown />
                </n-icon>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>

        <n-grid-item>
          <n-card>
            <n-statistic title="账户余额" :value="totalBalance" precision="2">
              <template #prefix>
                <n-icon color="#1890ff">
                  <Wallet />
                </n-icon>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>

        <n-grid-item>
          <n-card>
            <n-statistic title="预算剩余" :value="budgetRemaining" precision="2">
              <template #prefix>
                <n-icon color="#faad14">
                  <Target />
                </n-icon>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>
      </n-grid>

      <!-- 快速操作 -->
      <n-card title="快速操作">
        <n-space>
          <n-button type="primary" @click="goToAddTransaction">
            <template #icon>
              <n-icon>
                <Add />
              </n-icon>
            </template>
            新增交易
          </n-button>
          <n-button @click="goToTransactionList">
            <template #icon>
              <n-icon>
                <List />
              </n-icon>
            </template>
            查看记录
          </n-button>
          <n-button @click="goToReport">
            <template #icon>
              <n-icon>
                <BarChart />
              </n-icon>
            </template>
            财务报表
          </n-button>
        </n-space>
      </n-card>

      <!-- 最近交易 -->
      <n-card title="最近交易">
        <n-data-table
          :columns="transactionColumns"
          :data="recentTransactions"
          :pagination="false"
          :loading="loading"
          max-height="400"
        />
        <template #empty>
          <n-empty description="暂无交易记录">
            <template #extra>
              <n-button @click="goToAddTransaction">
                添加第一笔交易
              </n-button>
            </template>
          </n-empty>
        </template>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  TrendingUp,
  TrendingDown,
  Wallet,
  Target,
  Add,
  List,
  BarChart
} from '@vicons/ionicons5';
import { Transaction, TransactionType } from '@gzang/shared';
import { usePersonalStore } from '../stores/personal';
import { accountingApi } from '@gzang/shared';

const router = useRouter();
const personalStore = usePersonalStore();

// 加载状态
const loading = ref(false);

// 计算属性
const monthlyIncome = computed(() => {
  const now = new Date();
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
  return personalStore.transactions
    .filter(t => t.type === TransactionType.INCOME && new Date(t.transactionTime) >= startOfMonth)
    .reduce((sum, t) => sum + t.amount, 0);
});

const monthlyExpense = computed(() => {
  const now = new Date();
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
  return personalStore.transactions
    .filter(t => t.type === TransactionType.EXPENSE && new Date(t.transactionTime) >= startOfMonth)
    .reduce((sum, t) => sum + t.amount, 0);
});

const totalBalance = computed(() => {
  return personalStore.accounts.reduce((sum, account) => sum + account.balance, 0);
});

const budgetRemaining = computed(() => {
  // 这里应该从预算store获取预算信息
  const monthlyBudget = 5000; // 示例预算
  return monthlyBudget - monthlyExpense.value;
});

// 最近交易
const recentTransactions = computed(() => {
  return personalStore.transactions.slice(0, 10);
});

// 表格列配置
const transactionColumns = [
  {
    title: '时间',
    key: 'transactionTime',
    render: (row: Transaction) => new Date(row.transactionTime).toLocaleDateString('zh-CN')
  },
  {
    title: '类型',
    key: 'type',
    render: (row: Transaction) => row.type === TransactionType.INCOME ? '收入' : '支出'
  },
  {
    title: '金额',
    key: 'amount',
    render: (row: Transaction) => `¥${row.amount.toFixed(2)}`
  },
  {
    title: '分类',
    key: 'categoryId',
    render: (row: Transaction) => {
      const category = personalStore.categories.find(c => c.id === row.categoryId);
      return category?.categoryName || '未知';
    }
  },
  {
    title: '账户',
    key: 'accountId',
    render: (row: Transaction) => {
      const account = personalStore.accounts.find(a => a.id === row.accountId);
      return account?.accountName || '未知';
    }
  }
];

// 页面方法
const goToAddTransaction = () => {
  router.push('/transaction/add');
};

const goToTransactionList = () => {
  router.push('/transactions');
};

const goToReport = () => {
  router.push('/report');
};

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    // 并行加载数据
    const [transactionsRes, accountsRes, categoriesRes] = await Promise.all([
      accountingApi.getTransactions({ page: 1, size: 50 }),
      accountingApi.getAccounts(),
      accountingApi.getCategories()
    ]);

    if (transactionsRes.data) {
      personalStore.setTransactions(transactionsRes.data.records);
    }

    if (accountsRes.data) {
      personalStore.setAccounts(accountsRes.data);
    }

    if (categoriesRes.data) {
      personalStore.setCategories(categoriesRes.data);
    }
  } catch (error) {
    console.error('加载数据失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.dashboard {
  height: 100%;
}

.statistic-card {
  text-align: center;
}
</style>
