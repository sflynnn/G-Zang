<template>
  <div class="company-management" :class="{ 'dark-theme': isDark }">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">{{ t('menu.companyManagement') }}</h1>
          <p class="page-subtitle">{{ t('menu.companyManagementDesc') }}</p>
        </div>
        <n-button type="primary" @click="openCreateModal">
          <template #icon>
            <n-icon :component="AddOutline" />
          </template>
          {{ t('company.create') }}
        </n-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-primary">
            <n-icon :component="BusinessOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ totalCompanies }}</span>
            <span class="stat-label">{{ t('dashboard.totalCompanies') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-success">
            <n-icon :component="CheckmarkCircleOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ activeCompanies }}</span>
            <span class="stat-label">{{ t('user.statusNormal') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-warning">
            <n-icon :component="TimeOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ disabledCompanies }}</span>
            <span class="stat-label">{{ t('user.statusDisabled') }}</span>
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
            v-model:value="filterStatus"
            :placeholder="t('common.selectStatus')"
            clearable
            :options="statusOptions"
            class="filter-select filter-select-sm"
          />

          <n-button @click="resetFilters">
            <template #icon>
              <n-icon :component="RefreshOutline" />
            </template>
            {{ t('common.reset') }}
          </n-button>
        </div>
      </div>
    </n-card>

    <!-- 公司列表 -->
    <n-card class="table-card" :bordered="false">
      <n-data-table
        :loading="loading"
        :columns="columns"
        :data="companies"
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
          :item-count="total"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          show-quick-jumper
          @update:page="handlePageChange"
          @update:page-size="handlePageSizeChange"
        />
      </div>
    </n-card>

    <!-- 新建/编辑公司模态框 -->
    <n-modal
      v-model:show="showModal"
      preset="card"
      :title="editingCompany ? t('company.edit') : t('company.create')"
      style="width: 640px; max-width: 90vw"
    >
      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="top"
      >
        <n-grid :cols="2" :x-gap="16" responsive="screen">
          <n-gi>
            <n-form-item :label="t('company.name')" path="companyName" required>
              <n-input 
                v-model:value="formData.companyName" 
                :placeholder="t('company.namePlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('company.code')" path="companyCode" :disabled="!!editingCompany" required>
              <n-input 
                v-model:value="formData.companyCode" 
                :placeholder="t('company.codePlaceholder')"
                :disabled="!!editingCompany"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('company.creditCode')" path="creditCode">
              <n-input 
                v-model:value="formData.creditCode" 
                :placeholder="t('company.creditCodePlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('company.contact')" path="contactName">
              <n-input 
                v-model:value="formData.contactName" 
                :placeholder="t('company.contactPlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('company.phone')" path="contactPhone">
              <n-input 
                v-model:value="formData.contactPhone" 
                :placeholder="t('company.phonePlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('company.email')" path="contactEmail">
              <n-input 
                v-model:value="formData.contactEmail" 
                :placeholder="t('company.emailPlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi span="2">
            <n-form-item :label="t('company.address')" path="address">
              <n-input 
                v-model:value="formData.address" 
                type="textarea"
                :placeholder="t('company.addressPlaceholder')"
                :autosize="{ minRows: 2, maxRows: 4 }"
              />
            </n-form-item>
          </n-gi>
          <n-gi v-if="editingCompany">
            <n-form-item :label="t('company.status')" path="status" required>
              <n-select 
                v-model:value="formData.status" 
                :options="statusOptions" 
                :placeholder="t('common.selectStatus')"
              />
            </n-form-item>
          </n-gi>
        </n-grid>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">
            {{ t('common.cancel') }}
          </n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">
            {{ editingCompany ? t('common.update') : t('common.create') }}
          </n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 公司详情模态框 -->
    <n-modal
      v-model:show="showDetailModal"
      preset="card"
      :title="t('company.detail')"
      style="width: 640px; max-width: 90vw"
    >
      <n-descriptions label-placement="top" :column="2" bordered size="small">
        <n-descriptions-item :label="t('company.name')">
          {{ currentCompany?.companyName || '-' }}
        </n-descriptions-item>
        <n-descriptions-item :label="t('company.code')">
          <code class="code-inline">{{ currentCompany?.companyCode || '-' }}</code>
        </n-descriptions-item>
        <n-descriptions-item :label="t('company.creditCode')">
          {{ currentCompany?.creditCode || '-' }}
        </n-descriptions-item>
        <n-descriptions-item :label="t('company.status')">
          <n-tag :type="currentCompany?.status === 1 ? 'success' : 'error'" size="small">
            {{ currentCompany?.status === 1 ? t('user.statusNormal') : t('user.statusDisabled') }}
          </n-tag>
        </n-descriptions-item>
        <n-descriptions-item :label="t('company.contact')">
          {{ currentCompany?.contactName || '-' }}
        </n-descriptions-item>
        <n-descriptions-item :label="t('company.phone')">
          {{ currentCompany?.contactPhone || '-' }}
        </n-descriptions-item>
        <n-descriptions-item :label="t('company.email')">
          {{ currentCompany?.contactEmail || '-' }}
        </n-descriptions-item>
        <n-descriptions-item :label="t('user.userCount')">
          <n-tag type="info" size="small">{{ currentCompany?.userCount || 0 }}</n-tag>
        </n-descriptions-item>
        <n-descriptions-item :label="t('company.address')" :span="2">
          {{ currentCompany?.address || '-' }}
        </n-descriptions-item>
        <n-descriptions-item :label="t('user.createTime')" :span="2">
          {{ currentCompany?.createTime ? new Date(currentCompany.createTime).toLocaleString(appStore.language === 'zh-CN' ? 'zh-CN' : 'en-US') : '-' }}
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
  AddOutline,
  RefreshOutline,
  BusinessOutline,
  CheckmarkCircleOutline,
  TimeOutline,
  PencilOutline,
  TrashOutline,
  EyeOutline
} from '@vicons/ionicons5';
import { useCompanyStore } from '@/stores/company';
import { useAppStore } from '@/stores/app';
import type { CompanyResponse } from '@/api/company';
import { NButton, NSpace, NTag, NIcon } from 'naive-ui';
import type { DataTableColumns, PaginationProps } from 'naive-ui';

const { t } = useI18n();
const companyStore = useCompanyStore();
const appStore = useAppStore();

const isDark = computed(() => appStore.isDark);

const loading = computed(() => companyStore.loading);
const companies = computed(() => companyStore.companies);

const showModal = ref(false);
const showDetailModal = ref(false);
const submitLoading = ref(false);
const editingCompany = ref<CompanyResponse | null>(null);
const currentCompany = ref<CompanyResponse | null>(null);
const searchKeyword = ref('');
const filterStatus = ref<number | null>(null);

// 统计数据
const total = ref(0);
const totalCompanies = computed(() => companies.value.length);
const activeCompanies = computed(() => companies.value.filter(c => c.status === 1).length);
const disabledCompanies = computed(() => companies.value.filter(c => c.status === 0).length);

// Naive UI 主题配置
const dataTableThemeOverrides = computed(() => ({
  thColor: isDark.value ? '#374151' : '#F8F9FA',
  tdColor: isDark.value ? '#1F2937' : '#FFFFFF',
  tdColorStriped: isDark.value ? '#252D3B' : '#F8F9FA',
  borderColor: isDark.value ? '#374151' : '#E5E7EB',
  thTextColor: isDark.value ? '#F9FAFB' : '#1F2937',
  tdTextColor: isDark.value ? '#E5E7EB' : '#374451',
}));

const statusOptions = computed(() => [
  { label: t('user.statusNormal'), value: 1 },
  { label: t('user.statusDisabled'), value: 0 }
]);

// 分页配置
const pagination = reactive<PaginationProps>({
  page: 1,
  pageSize: 10,
  showSizePicker: true,
  showQuickJumper: true
});

// 表单数据
const formData = reactive({
  companyName: '',
  companyCode: '',
  creditCode: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  status: 1
});

const rules = computed(() => ({
  companyName: [{ required: true, message: t('company.nameRequired'), trigger: 'blur' }],
  companyCode: [{ required: true, message: t('company.codeRequired'), trigger: 'blur' }]
}));

const formRef = ref<any>(null);

const columns = computed<DataTableColumns<CompanyResponse>>(() => [
  {
    title: 'ID',
    key: 'id',
    width: 80,
    render: (row: CompanyResponse) => h('span', { class: 'id-cell' }, `#${row.id}`)
  },
  {
    title: t('company.name'),
    key: 'companyName',
    width: 200,
    ellipsis: { tooltip: true }
  },
  {
    title: t('company.code'),
    key: 'companyCode',
    width: 150,
    render: (row: CompanyResponse) => h('code', { class: 'code-cell' }, row.companyCode)
  },
  {
    title: t('company.contact'),
    key: 'contactName',
    width: 120,
    render: (row: CompanyResponse) => row.contactName || '-'
  },
  {
    title: t('company.phone'),
    key: 'contactPhone',
    width: 140,
    render: (row: CompanyResponse) => row.contactPhone || '-'
  },
  {
    title: t('company.status'),
    key: 'status',
    width: 100,
    render: (row: CompanyResponse) => {
      const isActive = row.status === 1;
      return h(NTag, { type: isActive ? 'success' : 'error', size: 'small' }, {
        default: () => (isActive ? t('user.statusNormal') : t('user.statusDisabled'))
      });
    }
  },
  {
    title: t('user.createTime'),
    key: 'createTime',
    width: 170,
    render: (row: CompanyResponse) => {
      return row.createTime ? new Date(row.createTime).toLocaleString(appStore.language === 'zh-CN' ? 'zh-CN' : 'en-US') : '-';
    }
  },
  {
    title: t('common.actions'),
    key: 'actions',
    width: 240,
    fixed: 'right',
    align: 'center',
    render: (row: CompanyResponse) => {
      return h(NSpace, { size: 'small', justify: 'center' }, {
        default: () => [
          h(NButton, {
            size: 'small',
            quaternary: true,
            type: 'primary',
            onClick: () => openDetail(row)
          }, {
            icon: () => h(NIcon, { component: EyeOutline, size: 16 }),
            default: () => t('common.detail')
          }),
          h(NButton, {
            size: 'small',
            quaternary: true,
            type: 'primary',
            onClick: () => openEditModal(row)
          }, {
            icon: () => h(NIcon, { component: PencilOutline, size: 16 }),
            default: () => t('common.edit')
          }),
          h(NButton, {
            size: 'small',
            quaternary: true,
            type: 'error',
            onClick: () => handleDelete(row)
          }, {
            icon: () => h(NIcon, { component: TrashOutline, size: 16 }),
            default: () => t('common.delete')
          })
        ]
      });
    }
  }
]);

const fetchCompanies = async () => {
  await companyStore.fetchCompanies({
    current: pagination.page as number,
    size: pagination.pageSize as number,
    keyword: searchKeyword.value || undefined,
    status: filterStatus.value !== null ? filterStatus.value : undefined
  });
  total.value = companyStore.total;
};

const handleSearch = () => {
  pagination.page = 1;
  fetchCompanies();
};

const resetFilters = () => {
  searchKeyword.value = '';
  filterStatus.value = null;
  pagination.page = 1;
  fetchCompanies();
};

const handlePageChange = (page: number) => {
  pagination.page = page;
  fetchCompanies();
};

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize;
  pagination.page = 1;
  fetchCompanies();
};

const openCreateModal = () => {
  editingCompany.value = null;
  resetForm();
  showModal.value = true;
};

const openEditModal = (company: CompanyResponse) => {
  editingCompany.value = company;
  formData.companyName = company.companyName;
  formData.companyCode = company.companyCode;
  formData.creditCode = company.creditCode || '';
  formData.contactName = company.contactName || '';
  formData.contactPhone = company.contactPhone || '';
  formData.contactEmail = company.contactEmail || '';
  formData.address = company.address || '';
  formData.status = company.status;
  showModal.value = true;
};

const openDetail = (company: CompanyResponse) => {
  currentCompany.value = company;
  showDetailModal.value = true;
};

const resetForm = () => {
  formData.companyName = '';
  formData.companyCode = '';
  formData.creditCode = '';
  formData.contactName = '';
  formData.contactPhone = '';
  formData.contactEmail = '';
  formData.address = '';
  formData.status = 1;
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitLoading.value = true;
  try {
    if (editingCompany.value) {
      await companyStore.editCompany(editingCompany.value.id, formData as any);
      window.$message?.success(t('company.updateSuccess'));
    } else {
      await companyStore.addCompany(formData as any);
      window.$message?.success(t('company.createSuccess'));
    }
    showModal.value = false;
    fetchCompanies();
  } catch (e: any) {
    window.$message?.error(e?.message || t('common.operationFailed'));
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = (company: CompanyResponse) => {
  window.$dialog.warning({
    title: t('common.confirm'),
    content: `${t('company.deleteConfirm')}: ${company.companyName}?`,
    positiveText: t('common.delete'),
    negativeText: t('common.cancel'),
    onPositiveClick: async () => {
      try {
        await companyStore.removeCompany(company.id);
        window.$message?.success(t('company.deleteSuccess'));
        fetchCompanies();
      } catch (e: any) {
        window.$message?.error(e?.message || t('common.deleteFailed'));
      }
    }
  });
};

onMounted(() => {
  fetchCompanies();
});
</script>

<style scoped>
.company-management {
  padding: 0 24px;
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

.stat-icon-warning {
  background: rgba(251, 139, 36, 0.1);
  color: #FB8B24;
}

.dark-theme .stat-icon-primary {
  background: rgba(15, 76, 92, 0.2);
  color: #60A5FA;
}

.dark-theme .stat-icon-success {
  background: rgba(6, 214, 160, 0.2);
  color: #34D399;
}

.dark-theme .stat-icon-warning {
  background: rgba(251, 139, 36, 0.2);
  color: #FB8B24;
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
  padding: 8px 0;
}

.filter-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.search-input {
  width: 240px;
}

.filter-select {
  width: 160px;
}

.filter-select-sm {
  width: 120px;
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

/* 公司代码 */
.code-cell {
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
  padding: 2px 6px;
  background: rgba(15, 76, 92, 0.1);
  border-radius: 4px;
  color: #0F4C5C;
}

.dark-theme .code-cell {
  background: rgba(15, 76, 92, 0.2);
  color: #60A5FA;
}

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
