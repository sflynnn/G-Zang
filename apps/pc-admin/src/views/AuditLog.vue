<template>
  <div class="audit-log" :class="{ 'dark-theme': isDark }">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">{{ t('menu.auditLog') }}</h1>
          <p class="page-subtitle">{{ t('menu.auditLogDesc') }}</p>
        </div>
        <n-space>
          <n-button @click="exportLogs">
            <template #icon>
              <n-icon :component="DownloadOutline" />
            </template>
            {{ t('auditLog.exportLogs') }}
          </n-button>
          <n-button @click="refreshLogs">
            <template #icon>
              <n-icon :component="RefreshOutline" />
            </template>
            {{ t('common.refresh') }}
          </n-button>
        </n-space>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-primary">
            <n-icon :component="DocumentTextOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ totalLogs }}</span>
            <span class="stat-label">{{ t('common.total') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-success">
            <n-icon :component="CheckmarkCircleOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ successLogs }}</span>
            <span class="stat-label">{{ t('auditLog.success') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-danger">
            <n-icon :component="CloseCircleOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ failedLogs }}</span>
            <span class="stat-label">{{ t('auditLog.failed') }}</span>
          </div>
        </div>
      </n-card>
    </div>

    <!-- 筛选区域 -->
    <n-card class="filter-card" :bordered="false">
      <div class="filter-content">
        <div class="filter-row">
          <n-input
            v-model:value="searchKeyword"
            :placeholder="t('common.search') + '...'"
            clearable
            @keyup.enter="handleSearch"
            class="search-input"
          >
            <template #prefix>
              <n-icon :component="SearchOutline" />
            </template>
          </n-input>

          <n-select
            v-model:value="filterAction"
            :placeholder="t('auditLog.actionType')"
            clearable
            :options="actionOptions"
            class="filter-select"
          />

          <n-select
            v-model:value="filterModule"
            :placeholder="t('auditLog.module')"
            clearable
            :options="moduleOptions"
            class="filter-select"
          />

          <n-select
            v-model:value="filterStatus"
            :placeholder="t('auditLog.operationResult')"
            clearable
            :options="resultOptions"
            class="filter-select filter-select-sm"
          />

          <n-date-picker
            v-model:formatted-value="filterDateRange"
            type="daterange"
            clearable
            class="date-picker"
            :placeholder="t('auditLog.filterByTime')"
          />
        </div>

        <div class="filter-row filter-row-second">
          <n-button @click="resetFilters">
            <template #icon>
              <n-icon :component="RefreshOutline" />
            </template>
            {{ t('common.reset') }}
          </n-button>
          <n-button type="primary" @click="handleSearch">
            <template #icon>
              <n-icon :component="SearchOutline" />
            </template>
            {{ t('common.search') }}
          </n-button>
        </div>
      </div>
    </n-card>

    <!-- 日志列表 -->
    <n-card class="table-card" :bordered="false">
      <n-data-table
        :loading="loading"
        :columns="columns"
        :data="logs"
        :pagination="pagination"
        :row-key="(row: any) => row.id"
        :theme-overrides="dataTableThemeOverrides"
        striped
      />

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <n-pagination
          v-model:page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :item-count="totalLogs"
          :page-sizes="[10, 20, 50, 100]"
          show-size-picker
          show-quick-jumper
          @update:page="handlePageChange"
          @update:page-size="handlePageSizeChange"
        />
      </div>
    </n-card>

    <!-- 日志详情模态框 -->
    <n-modal
      v-model:show="showDetailModal"
      preset="card"
      :title="t('auditLog.detail')"
      style="width: 720px; max-width: 90vw"
    >
      <n-descriptions label-placement="top" :column="2" bordered size="small">
        <n-descriptions-item :label="t('auditLog.username')">
          {{ currentLog?.username || '-' }}
        </n-descriptions-item>
        <n-descriptions-item :label="t('auditLog.userId')">
          <code class="code-inline">{{ currentLog?.userId || '-' }}</code>
        </n-descriptions-item>
        <n-descriptions-item :label="t('auditLog.module')">
          <n-tag type="info" size="small">{{ currentLog?.module || '-' }}</n-tag>
        </n-descriptions-item>
        <n-descriptions-item :label="t('auditLog.action')">
          <n-tag type="warning" size="small">{{ currentLog?.action || '-' }}</n-tag>
        </n-descriptions-item>
        <n-descriptions-item :label="t('auditLog.targetType')">
          {{ currentLog?.targetType || '-' }}
        </n-descriptions-item>
        <n-descriptions-item :label="t('auditLog.targetId')">
          <code class="code-inline">{{ currentLog?.targetId || '-' }}</code>
        </n-descriptions-item>
        <n-descriptions-item :label="t('auditLog.ip')" :span="2">
          <code class="code-inline">{{ currentLog?.ip || '-' }}</code>
        </n-descriptions-item>
        <n-descriptions-item :label="'User-Agent'" :span="2">
          <n-text depth="3" style="word-break: break-all; font-size: 12px;">
            {{ currentLog?.userAgent || '-' }}
          </n-text>
        </n-descriptions-item>
        <n-descriptions-item :label="t('auditLog.description')" :span="2">
          {{ currentLog?.description || '-' }}
        </n-descriptions-item>
        <n-descriptions-item :label="t('auditLog.operationResult')" :span="2">
          <n-tag :type="currentLog?.status === 1 ? 'success' : 'error'" size="small">
            {{ currentLog?.status === 1 ? t('auditLog.success') : t('auditLog.failed') }}
          </n-tag>
        </n-descriptions-item>
        <n-descriptions-item :label="t('auditLog.createTime')" :span="2">
          {{ currentLog?.createTime ? new Date(currentLog.createTime).toLocaleString(appStore.language === 'zh-CN' ? 'zh-CN' : 'en-US') : '-' }}
        </n-descriptions-item>
      </n-descriptions>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showDetailModal = false">
            {{ t('common.confirm') }}
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, h } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  SearchOutline,
  RefreshOutline,
  DownloadOutline,
  DocumentTextOutline,
  CheckmarkCircleOutline,
  CloseCircleOutline,
  EyeOutline
} from '@vicons/ionicons5';
import { useAuditLogStore } from '@/stores/auditLog';
import { useAppStore } from '@/stores/app';
import type { AuditLogResponse } from '@/api/audit-log';
import { NButton, NTag, NIcon } from 'naive-ui';
import type { DataTableColumns, PaginationProps } from 'naive-ui';

const { t } = useI18n();
const auditLogStore = useAuditLogStore();
const appStore = useAppStore();

const isDark = computed(() => appStore.isDark);

const loading = computed(() => auditLogStore.loading);
const logs = computed(() => auditLogStore.logs);

const searchKeyword = ref('');
const filterAction = ref<string | null>(null);
const filterModule = ref<string | null>(null);
const filterStatus = ref<number | null>(null);
const filterDateRange = ref<[number, number] | null>(null);
const showDetailModal = ref(false);
const currentLog = ref<AuditLogResponse | null>(null);

// 统计数据
const totalLogs = computed(() => auditLogStore.total);
const successLogs = computed(() => logs.value.filter(l => l.status === 1).length);
const failedLogs = computed(() => logs.value.filter(l => l.status !== 1).length);

// Naive UI 主题配置
const dataTableThemeOverrides = computed(() => ({
  thColor: isDark.value ? '#374151' : '#F8F9FA',
  tdColor: isDark.value ? '#1F2937' : '#FFFFFF',
  tdColorStriped: isDark.value ? '#252D3B' : '#F8F9FA',
  borderColor: isDark.value ? '#374151' : '#E5E7EB',
  thTextColor: isDark.value ? '#F9FAFB' : '#1F2937',
  tdTextColor: isDark.value ? '#E5E7EB' : '#374451',
}));

const actionOptions = computed(() => [
  { label: t('auditLog.create'), value: 'CREATE' },
  { label: t('auditLog.update'), value: 'UPDATE' },
  { label: t('auditLog.delete'), value: 'DELETE' },
  { label: t('auditLog.query'), value: 'QUERY' },
  { label: t('auditLog.grant'), value: 'GRANT' },
  { label: t('auditLog.login'), value: 'LOGIN' },
  { label: t('auditLog.logout'), value: 'LOGOUT' },
  { label: t('auditLog.permissionChange'), value: 'PERMISSION_CHANGE' }
]);

const moduleOptions = computed(() => [
  { label: t('menu.user'), value: 'USER' },
  { label: t('menu.role'), value: 'ROLE' },
  { label: t('menu.permission'), value: 'PERMISSION' },
  { label: t('menu.company'), value: 'COMPANY' },
  { label: t('auditLog.auth'), value: 'AUTH' }
]);

const resultOptions = computed(() => [
  { label: t('auditLog.success'), value: 1 },
  { label: t('auditLog.failed'), value: 0 }
]);

// 分页配置
const pagination = reactive<PaginationProps>({
  page: 1,
  pageSize: 20,
  showSizePicker: true,
  showQuickJumper: true
});

const columns = computed<DataTableColumns<AuditLogResponse>>(() => [
  {
    title: 'ID',
    key: 'id',
    width: 80,
    render: (row: AuditLogResponse) => h('span', { class: 'id-cell' }, `#${row.id}`)
  },
  {
    title: t('auditLog.username'),
    key: 'username',
    width: 120,
    render: (row: AuditLogResponse) => row.username || `ID: ${row.userId}`
  },
  {
    title: t('auditLog.module'),
    key: 'module',
    width: 100,
    render: (row: AuditLogResponse) => {
      return h(NTag, { type: 'info', size: 'small' }, { default: () => row.module || '-' });
    }
  },
  {
    title: t('auditLog.action'),
    key: 'action',
    width: 120,
    render: (row: AuditLogResponse) => {
      const actionTypeMap: Record<string, string> = {
        CREATE: 'success',
        UPDATE: 'warning',
        DELETE: 'error',
        QUERY: 'default',
        GRANT: 'info',
        LOGIN: 'success',
        LOGOUT: 'default',
        PERMISSION_CHANGE: 'warning'
      };
      return h(NTag, { type: (actionTypeMap[row.action] as any) || 'default', size: 'small' }, {
        default: () => row.action || '-'
      });
    }
  },
  {
    title: t('auditLog.description'),
    key: 'description',
    ellipsis: { tooltip: true }
  },
  {
    title: t('auditLog.ip'),
    key: 'ip',
    width: 140,
    render: (row: AuditLogResponse) => h('code', { class: 'ip-cell' }, row.ip || '-')
  },
  {
    title: t('auditLog.operationResult'),
    key: 'status',
    width: 100,
    render: (row: AuditLogResponse) => {
      const isSuccess = row.status === 1;
      return h(NTag, { type: isSuccess ? 'success' : 'error', size: 'small' }, {
        default: () => (isSuccess ? t('auditLog.success') : t('auditLog.failed'))
      });
    }
  },
  {
    title: t('auditLog.createTime'),
    key: 'createTime',
    width: 170,
    render: (row: AuditLogResponse) => {
      return row.createTime ? new Date(row.createTime).toLocaleString(appStore.language === 'zh-CN' ? 'zh-CN' : 'en-US') : '-';
    }
  },
  {
    title: t('common.actions'),
    key: 'actions',
    width: 120,
    fixed: 'right',
    align: 'center',
    render: (row: AuditLogResponse) => {
      return h(NButton, {
        size: 'small',
        quaternary: true,
        type: 'primary',
        onClick: () => openDetail(row)
      }, {
        icon: () => h(NIcon, { component: EyeOutline, size: 16 }),
        default: () => t('common.detail')
      });
    }
  }
]);

const fetchLogs = async () => {
  await auditLogStore.fetchLogs({
    current: pagination.page as number,
    size: pagination.pageSize as number,
    keyword: searchKeyword.value || undefined,
    action: filterAction.value || undefined,
    targetType: filterModule.value || undefined,
    status: filterStatus.value !== null ? filterStatus.value : undefined,
    startDate: filterDateRange.value ? filterDateRange.value[0] : undefined,
    endDate: filterDateRange.value ? filterDateRange.value[1] : undefined
  });
};

const openDetail = (log: AuditLogResponse) => {
  currentLog.value = log;
  showDetailModal.value = true;
};

const handleSearch = () => {
  pagination.page = 1;
  fetchLogs();
};

const resetFilters = () => {
  searchKeyword.value = '';
  filterAction.value = null;
  filterModule.value = null;
  filterStatus.value = null;
  filterDateRange.value = null;
  pagination.page = 1;
  fetchLogs();
};

const refreshLogs = () => {
  fetchLogs();
};

const handlePageChange = (page: number) => {
  pagination.page = page;
  fetchLogs();
};

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize;
  pagination.page = 1;
  fetchLogs();
};

const exportLogs = () => {
  window.$message.info(t('auditLog.exportDeveloping'));
};

onMounted(() => {
  fetchLogs();
});
</script>

<style scoped>
.audit-log {
  padding: 24px;
  min-height: 100%;
  background: linear-gradient(180deg, #F8F9FA 0%, #FFFFFF 100%);
}

.dark-theme {
  background: linear-gradient(180deg, #111827 0%, #1F2937 100%);
}

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1F2937;
  margin: 0;
}

.dark-theme .page-title {
  color: #F9FAFB;
}

.page-subtitle {
  font-size: 14px;
  color: #6B7280;
  margin: 0;
}

.dark-theme .page-subtitle {
  color: #9CA3AF;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  border-radius: 12px;
  transition: all 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px -4px rgba(0, 0, 0, 0.1);
}

.dark-theme .stat-card:hover {
  box-shadow: 0 8px 16px -4px rgba(0, 0, 0, 0.3);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon-primary {
  background: rgba(15, 76, 92, 0.1);
  color: #0F4C5C;
}

.stat-icon-success {
  background: rgba(6, 214, 160, 0.1);
  color: #06D6A0;
}

.stat-icon-danger {
  background: rgba(239, 71, 111, 0.1);
  color: #EF476F;
}

.dark-theme .stat-icon-primary {
  background: rgba(15, 76, 92, 0.2);
  color: #60A5FA;
}

.dark-theme .stat-icon-success {
  background: rgba(6, 214, 160, 0.2);
  color: #34D399;
}

.dark-theme .stat-icon-danger {
  background: rgba(239, 71, 111, 0.2);
  color: #F87171;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1F2937;
  font-family: 'JetBrains Mono', monospace;
}

.dark-theme .stat-value {
  color: #F9FAFB;
}

.stat-label {
  font-size: 13px;
  color: #6B7280;
}

.dark-theme .stat-label {
  color: #9CA3AF;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 16px;
  border-radius: 12px;
}

.filter-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-row-second {
  justify-content: flex-end;
}

.search-input {
  width: 200px;
}

.filter-select {
  width: 150px;
}

.filter-select-sm {
  width: 130px;
}

.date-picker {
  width: 280px;
}

/* 表格卡片 */
.table-card {
  border-radius: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 16px 0;
  border-top: 1px solid #E5E7EB;
  margin-top: 16px;
}

.dark-theme .pagination-wrapper {
  border-top-color: #374151;
}

/* ID单元格 */
.id-cell {
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
  color: #6B7280;
}

.dark-theme .id-cell {
  color: #9CA3AF;
}

/* IP地址 */
.ip-cell {
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
  padding: 2px 6px;
  background: rgba(15, 76, 92, 0.1);
  border-radius: 4px;
  color: #0F4C5C;
}

.dark-theme .ip-cell {
  background: rgba(15, 76, 92, 0.2);
  color: #60A5FA;
}

/* 内联代码 */
.code-inline {
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
  padding: 2px 8px;
  background: rgba(15, 76, 92, 0.1);
  border-radius: 4px;
  color: #0F4C5C;
}

.dark-theme .code-inline {
  background: rgba(15, 76, 92, 0.2);
  color: #60A5FA;
}
</style>
