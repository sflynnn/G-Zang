<template>
  <div class="transaction-list min-h-screen p-4 md:p-6 lg:p-8">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">
          {{ $t('transaction.listTitle') }}
        </h1>
        <p class="text-sm text-text-secondary dark:text-dark-text-secondary mt-1">
          {{ $t('transaction.listSubtitle') }}
        </p>
      </div>
      <button
        @click="goToAdd"
        class="btn-primary flex items-center gap-2"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        {{ $t('transaction.add') }}
      </button>
    </div>

    <!-- 筛选工具栏 -->
    <div class="bg-surface dark:bg-dark-surface rounded-2xl p-4 mb-6 shadow-gzang">
      <div class="flex flex-wrap items-center gap-3">
        <!-- 日期范围 -->
        <div class="flex items-center gap-2">
          <NDatePicker
            v-model:value="dateRange"
            type="daterange"
            clearable
            :placeholder="$t('transaction.selectDateRange')"
            class="!w-64"
            @update:value="handleDateRangeChange"
          />
        </div>

        <!-- 交易类型 -->
        <NSelect
          v-model:value="filterType"
          :options="typeOptions"
          :placeholder="$t('transaction.selectType')"
          clearable
          class="!w-32"
          @update:value="handleFilterChange"
        />

        <!-- 分类筛选 -->
        <NSelect
          v-model:value="filterCategoryId"
          :options="categoryOptions"
          :placeholder="$t('transaction.selectCategory')"
          clearable
          filterable
          class="!w-40"
          @update:value="handleFilterChange"
        />

        <!-- 账户筛选 -->
        <NSelect
          v-model:value="filterAccountId"
          :options="accountOptions"
          :placeholder="$t('transaction.selectAccount')"
          clearable
          filterable
          class="!w-40"
          @update:value="handleFilterChange"
        />

        <!-- 搜索 -->
        <NInput
          v-model:value="keyword"
          :placeholder="$t('transaction.searchPlaceholder')"
          clearable
          class="!w-48"
          @update:value="handleKeywordChange"
        >
          <template #prefix>
            <svg class="w-4 h-4 text-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </template>
        </NInput>

        <!-- 重置 -->
        <button
          v-if="hasActiveFilters"
          @click="resetFilters"
          class="text-sm text-secondary hover:text-secondary-dark transition-colors"
        >
          {{ $t('transaction.reset') }}
        </button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="grid grid-cols-3 gap-4 mb-6">
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-4 shadow-gzang">
        <p class="text-xs text-text-secondary dark:text-dark-text-secondary mb-1">{{ $t('transaction.totalIncome') }}</p>
        <p class="text-xl font-bold font-mono text-success">+{{ formatCurrency(stats.totalIncome) }}</p>
      </div>
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-4 shadow-gzang">
        <p class="text-xs text-text-secondary dark:text-dark-text-secondary mb-1">{{ $t('transaction.totalExpense') }}</p>
        <p class="text-xl font-bold font-mono text-danger">-{{ formatCurrency(stats.totalExpense) }}</p>
      </div>
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-4 shadow-gzang">
        <p class="text-xs text-text-secondary dark:text-dark-text-secondary mb-1">{{ $t('transaction.netBalance') }}</p>
        <p
          class="text-xl font-bold font-mono"
          :class="stats.netBalance >= 0 ? 'text-success' : 'text-danger'"
        >
          {{ stats.netBalance >= 0 ? '+' : '' }}{{ formatCurrency(stats.netBalance) }}
        </p>
      </div>
    </div>

    <!-- 交易列表 -->
    <div class="bg-surface dark:bg-dark-surface rounded-2xl shadow-gzang overflow-hidden">
      <!-- 表头 -->
      <div class="hidden md:grid grid-cols-12 gap-4 px-6 py-3 bg-background dark:bg-dark-bg text-xs font-medium text-text-secondary dark:text-dark-text-secondary border-b border-border dark:border-dark-border">
        <div class="col-span-1">{{ $t('transaction.date') }}</div>
        <div class="col-span-2">{{ $t('transaction.category') }}</div>
        <div class="col-span-2">{{ $t('transaction.account') }}</div>
        <div class="col-span-1">{{ $t('transaction.type') }}</div>
        <div class="col-span-2">{{ $t('transaction.remark') }}</div>
        <div class="col-span-2 text-right">{{ $t('transaction.amount') }}</div>
        <div class="col-span-2 text-center">{{ $t('transaction.actions') }}</div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="p-12 text-center">
        <NSpin size="large" />
        <p class="mt-3 text-sm text-text-secondary">{{ $t('common.loading') }}...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="filteredTransactions.length === 0" class="p-12 text-center">
        <div class="w-16 h-16 mx-auto mb-4 rounded-2xl bg-gray-100 dark:bg-dark-bg flex items-center justify-center">
          <svg class="w-8 h-8 text-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
        </div>
        <p class="text-text-secondary dark:text-dark-text-secondary">{{ $t('transaction.empty') }}</p>
        <button @click="goToAdd" class="btn-primary mt-4">
          {{ $t('transaction.addFirst') }}
        </button>
      </div>

      <!-- 交易记录列表 -->
      <div v-else class="divide-y divide-border dark:divide-dark-border">
        <!-- 按日期分组 -->
        <template v-for="(group, date) in groupedTransactions" :key="date">
          <!-- 日期分组标题 -->
          <div class="px-6 py-2 bg-background dark:bg-dark-bg text-xs font-medium text-text-secondary dark:text-dark-text-secondary flex items-center justify-between">
            <span>{{ formatGroupDate(date) }}</span>
            <span class="text-text-secondary">
              {{ group.length }} {{ $t('transaction.records') }}
            </span>
          </div>

          <!-- 该日期的交易 -->
          <div
            v-for="tx in group"
            :key="tx.id"
            class="grid grid-cols-12 gap-4 px-6 py-4 hover:bg-background dark:hover:bg-dark-bg transition-colors cursor-pointer"
            @click="goToDetail(tx.id)"
          >
            <!-- 时间 -->
            <div class="col-span-1 flex items-center">
              <div class="md:hidden text-xs text-text-secondary w-16">{{ $t('transaction.date') }}:</div>
              <span class="text-sm text-text-primary dark:text-dark-text-primary">
                {{ formatTime(tx.transactionTime) }}
              </span>
            </div>

            <!-- 分类 -->
            <div class="col-span-2 flex items-center gap-2">
              <div
                class="w-8 h-8 rounded-lg flex items-center justify-center text-sm"
                :style="{ backgroundColor: getCategoryColor(tx.categoryId) + '20', color: getCategoryColor(tx.categoryId) }"
              >
                {{ getCategoryIcon(tx.categoryId) }}
              </div>
              <div class="min-w-0">
                <p class="text-sm font-medium truncate text-text-primary dark:text-dark-text-primary">
                  {{ getCategoryName(tx.categoryId) }}
                </p>
              </div>
            </div>

            <!-- 账户 -->
            <div class="col-span-2 flex items-center">
              <div class="md:hidden text-xs text-text-secondary w-16">{{ $t('transaction.account') }}:</div>
              <span class="text-sm truncate text-text-secondary dark:text-dark-text-secondary">
                {{ getAccountName(tx.accountId) }}
              </span>
            </div>

            <!-- 类型 -->
            <div class="col-span-1 flex items-center">
              <div class="md:hidden text-xs text-text-secondary w-12">{{ $t('transaction.type') }}:</div>
              <span
                class="text-xs px-2 py-0.5 rounded-full font-medium"
                :class="tx.type === 1 ? 'bg-success/10 text-success' : 'bg-danger/10 text-danger'"
              >
                {{ tx.type === 1 ? $t('transaction.income') : $t('transaction.expense') }}
              </span>
            </div>

            <!-- 备注 -->
            <div class="col-span-2 flex items-center">
              <div class="md:hidden text-xs text-text-secondary w-12">{{ $t('transaction.remark') }}:</div>
              <span class="text-sm truncate text-text-secondary dark:text-dark-text-secondary">
                {{ tx.remark || '-' }}
              </span>
            </div>

            <!-- 金额 -->
            <div class="col-span-2 flex items-center justify-end">
              <div class="md:hidden text-xs text-text-secondary w-12">{{ $t('transaction.amount') }}:</div>
              <span
                class="font-mono font-bold text-sm"
                :class="tx.type === 1 ? 'text-success' : 'text-danger'"
              >
                {{ tx.type === 1 ? '+' : '-' }}{{ formatCurrency(tx.amount) }}
              </span>
            </div>

            <!-- 操作 -->
            <div class="col-span-2 flex items-center justify-center gap-2">
              <button
                @click.stop="goToEdit(tx.id)"
                class="w-8 h-8 rounded-lg hover:bg-gray-100 dark:hover:bg-dark-bg flex items-center justify-center transition-colors"
                :title="$t('transaction.edit')"
              >
                <svg class="w-4 h-4 text-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </button>
              <NPopconfirm
                @positive-click="handleDelete(tx.id)"
                :positive-text="$t('common.confirm')"
                :negative-text="$t('common.cancel')"
              >
                <template #trigger>
                  <button
                    class="w-8 h-8 rounded-lg hover:bg-danger/10 flex items-center justify-center transition-colors"
                    :title="$t('transaction.delete')"
                  >
                    <svg class="w-4 h-4 text-danger" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </template>
                {{ $t('transaction.deleteConfirm') }}
              </NPopconfirm>
            </div>
          </div>
        </template>
      </div>

      <!-- 分页 -->
      <div v-if="totalCount > pageSize" class="px-6 py-4 border-t border-border dark:border-dark-border flex items-center justify-between">
        <p class="text-sm text-text-secondary dark:text-dark-text-secondary">
          {{ $t('transaction.showing') }} {{ (currentPage - 1) * pageSize + 1 }}-{{ Math.min(currentPage * pageSize, totalCount) }} {{ $t('transaction.of') }} {{ totalCount }}
        </p>
        <NPagination
          v-model:page="currentPage"
          :page-count="Math.ceil(totalCount / pageSize)"
          :page-slot="5"
          show-quick-jumper
          @update:page="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { NDatePicker, NSelect, NInput, NPopconfirm, NPagination, NSpin, useMessage, useDialog } from 'naive-ui';
import { accountingApi } from '@gzang/shared';
import { usePersonalStore } from '@/stores/personal';
import { Transaction } from '@gzang/shared';

const { t } = useI18n();
const router = useRouter();
const personalStore = usePersonalStore();
const message = useMessage();
const dialog = useDialog();

// 状态
const loading = ref(false);
const transactions = ref<Transaction[]>([]);
const currentPage = ref(1);
const pageSize = ref(20);
const totalCount = ref(0);

// 筛选条件
const dateRange = ref<[number, number] | null>(null);
const filterType = ref<number | null>(null);
const filterCategoryId = ref<number | null>(null);
const filterAccountId = ref<number | null>(null);
const keyword = ref('');

// 统计数据
const stats = ref({
  totalIncome: 0,
  totalExpense: 0,
  netBalance: 0,
});

// 选项配置
const typeOptions = computed(() => [
  { label: t('transaction.allTypes'), value: null },
  { label: t('transaction.income'), value: 1 },
  { label: t('transaction.expense'), value: 2 },
]);

const categoryOptions = computed(() => [
  { label: t('transaction.allCategories'), value: null },
  ...personalStore.categories.map(c => ({
    label: c.categoryName,
    value: c.id,
  })),
]);

const accountOptions = computed(() => [
  { label: t('transaction.allAccounts'), value: null },
  ...personalStore.accounts.map(a => ({
    label: a.accountName,
    value: a.id,
  })),
]);

// 计算属性
const hasActiveFilters = computed(() => {
  return dateRange.value !== null || filterType.value !== null ||
    filterCategoryId.value !== null || filterAccountId.value !== null || keyword.value !== '';
});

const filteredTransactions = computed(() => {
  let result = [...transactions.value];
  if (keyword.value) {
    const kw = keyword.value.toLowerCase();
    result = result.filter(tx =>
      (tx.remark && tx.remark.toLowerCase().includes(kw)) ||
      getCategoryName(tx.categoryId).toLowerCase().includes(kw) ||
      getAccountName(tx.accountId).toLowerCase().includes(kw)
    );
  }
  return result;
});

const groupedTransactions = computed(() => {
  const groups: Record<string, Transaction[]> = {};
  for (const tx of filteredTransactions.value) {
    const date = tx.transactionTime.split('T')[0];
    if (!groups[date]) groups[date] = [];
    groups[date].push(tx);
  }
  return groups;
});

// 辅助函数
const formatCurrency = (amount: number) => {
  return amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

const formatTime = (time: string) => {
  return new Date(time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
};

const formatGroupDate = (date: string) => {
  const d = new Date(date);
  const today = new Date();
  const yesterday = new Date(today);
  yesterday.setDate(yesterday.getDate() - 1);

  if (date === today.toISOString().split('T')[0]) return t('transaction.today');
  if (date === yesterday.toISOString().split('T')[0]) return t('transaction.yesterday');
  return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', weekday: 'short' });
};

const getCategoryName = (categoryId: number) => {
  const cat = personalStore.categories.find(c => c.id === categoryId);
  return cat?.categoryName || t('transaction.uncategorized');
};

const getCategoryColor = (categoryId: number) => {
  return '#0F4C5C';
};

const getCategoryIcon = (categoryId: number) => {
  return '📦';
};

const getAccountName = (accountId: number) => {
  const acc = personalStore.accounts.find(a => a.id === accountId);
  return acc?.accountName || t('transaction.unknownAccount');
};

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    const params: any = {
      page: currentPage.value,
      size: pageSize.value,
    };
    if (dateRange.value) {
      params.startDate = new Date(dateRange.value[0]).toISOString();
      params.endDate = new Date(dateRange.value[1]).toISOString();
    }
    if (filterType.value) params.type = filterType.value;
    if (filterCategoryId.value) params.categoryId = filterCategoryId.value;
    if (filterAccountId.value) params.accountId = filterAccountId.value;

    const res = await accountingApi.getTransactions(params);
    if (res.data) {
      transactions.value = res.data.records;
      totalCount.value = res.data.total;
      // 更新统计
      stats.value = {
        totalIncome: filteredTransactions.value
          .filter(t => t.type === 1)
          .reduce((sum, t) => sum + t.amount, 0),
        totalExpense: filteredTransactions.value
          .filter(t => t.type === 2)
          .reduce((sum, t) => sum + t.amount, 0),
        netBalance: 0,
      };
      stats.value.netBalance = stats.value.totalIncome - stats.value.totalExpense;
    }
  } catch (error: any) {
    message.error(error.message || t('transaction.loadFailed'));
  } finally {
    loading.value = false;
  }
};

const loadCategories = async () => {
  try {
    const res = await accountingApi.getCategories();
    if (res.data) {
      personalStore.setCategories(res.data);
    }
  } catch (error) {
    console.error('加载分类失败:', error);
  }
};

const loadAccounts = async () => {
  try {
    const res = await accountingApi.getAccounts();
    if (res.data) {
      personalStore.setAccounts(res.data);
    }
  } catch (error) {
    console.error('加载账户失败:', error);
  }
};

// 事件处理
const handleDateRangeChange = () => handleFilterChange();
const handleFilterChange = () => {
  currentPage.value = 1;
  loadData();
};

const handleKeywordChange = () => {
  currentPage.value = 1;
  loadData();
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadData();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const resetFilters = () => {
  dateRange.value = null;
  filterType.value = null;
  filterCategoryId.value = null;
  filterAccountId.value = null;
  keyword.value = '';
  currentPage.value = 1;
  loadData();
};

const handleDelete = async (id: number) => {
  try {
    await accountingApi.deleteTransaction(id);
    personalStore.removeTransaction(id);
    message.success(t('transaction.deleteSuccess'));
    loadData();
  } catch (error: any) {
    message.error(error.message || t('transaction.deleteFailed'));
  }
};

// 路由跳转
const goToAdd = () => router.push('/transaction/add');
const goToEdit = (id: number) => router.push(`/transaction/edit/${id}`);
const goToDetail = (id: number) => router.push(`/transaction/edit/${id}`);

onMounted(async () => {
  await Promise.all([loadCategories(), loadAccounts()]);
  loadData();
});
</script>

<style scoped>
.btn-primary {
  @apply inline-flex items-center justify-center gap-2 px-4 py-2 bg-secondary text-white rounded-xl font-medium text-sm transition-all hover:bg-secondary-dark active:scale-95;
}

.text-success { @apply text-[#06D6A0]; }
.text-danger { @apply text-[#EF476F]; }
.bg-success\/10 { @apply bg-[#06D6A0]/10; }
.bg-danger\/10 { @apply bg-[#EF476F]/10; }
.text-secondary { @apply text-[#FB8B24]; }
.hover\:text-secondary-dark:hover { @apply text-[#e67a1a]; }
.bg-secondary { @apply bg-[#FB8B24]; }
.hover\:bg-secondary-dark:hover { @apply bg-[#e67a1a]; }
</style>
