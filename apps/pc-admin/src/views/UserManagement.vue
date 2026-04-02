<template>
  <div class="user-management" :class="{ 'dark-theme': isDark }">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">{{ t('menu.userManagement') }}</h1>
          <p class="page-subtitle">{{ t('menu.userManagementDesc') }}</p>
        </div>
        <n-button type="primary" @click="openCreateModal">
          <template #icon>
            <n-icon :component="AddOutline" />
          </template>
          {{ t('user.createUser') }}
        </n-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-primary">
            <n-icon :component="PeopleOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ total }}</span>
            <span class="stat-label">{{ t('user.userCount') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-success">
            <n-icon :component="CheckmarkCircleOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ activeUsers }}</span>
            <span class="stat-label">{{ t('user.statusNormal') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-warning">
            <n-icon :component="PersonRemoveOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ disabledUsers }}</span>
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
            v-model:value="filterRole"
            :placeholder="t('common.selectRole')"
            clearable
            :options="roleOptions"
            class="filter-select"
          />

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

    <!-- 用户列表 -->
    <n-card class="table-card" :bordered="false">
      <n-data-table
        :loading="loading"
        :columns="columns"
        :data="userList"
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

    <!-- 新建/编辑用户模态框 -->
    <n-modal
      v-model:show="showEditModal"
      preset="card"
      :title="editingUser ? t('user.editUser') : t('user.createUser')"
      style="width: 560px; max-width: 90vw"
    >
      <n-form
        ref="formRef"
        :model="userForm"
        :rules="rules"
        label-placement="top"
      >
        <n-grid :cols="2" :x-gap="16" responsive="screen">
          <n-gi>
            <n-form-item :label="t('user.username')" path="username">
              <n-input 
                v-model:value="userForm.username" 
                :disabled="!!editingUser"
                :placeholder="t('user.username')"
                :input-props="{ autocomplete: 'off' }"
              />
            </n-form-item>
          </n-gi>
          <n-gi v-if="!editingUser">
            <n-form-item :label="t('common.password')" path="password">
              <n-input 
                v-model:value="userForm.password" 
                type="password"
                show-password-on="click"
                :placeholder="t('common.passwordPlaceholder')"
                :input-props="{ autocomplete: 'new-password' }"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('user.email')" path="email">
              <n-input 
                v-model:value="userForm.email" 
                :placeholder="t('user.emailPlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('user.phone')" path="phone">
              <n-input 
                v-model:value="userForm.phone" 
                :placeholder="t('user.phonePlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('user.role')" path="roleId" required>
              <n-select 
                v-model:value="userForm.roleId" 
                :options="roleOptions" 
                :placeholder="t('common.selectRole')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('user.status')" path="status" required>
              <n-select 
                v-model:value="userForm.status" 
                :options="statusOptions" 
                :placeholder="t('common.selectStatus')"
              />
            </n-form-item>
          </n-gi>
          <n-gi v-if="editingUser" span="2">
            <n-form-item :label="t('user.department')" path="department">
              <n-input 
                v-model:value="userForm.department" 
                :placeholder="t('user.departmentPlaceholder')"
              />
            </n-form-item>
          </n-gi>
        </n-grid>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showEditModal = false">
            {{ t('common.cancel') }}
          </n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">
            {{ editingUser ? t('common.update') : t('common.create') }}
          </n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 分配角色模态框 -->
    <n-modal
      v-model:show="showRoleModal"
      preset="card"
      :title="t('user.assignRole')"
      style="width: 480px; max-width: 90vw"
    >
      <n-form-item :label="t('user.role')" path="roleId">
        <n-select 
          v-model:value="selectedRoleId" 
          :options="roleOptions" 
          :placeholder="t('common.selectRole')"
        />
      </n-form-item>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showRoleModal = false">
            {{ t('common.cancel') }}
          </n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleAssignRole">
            {{ t('common.confirm') }}
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, computed, h, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  SearchOutline,
  AddOutline,
  RefreshOutline,
  PeopleOutline,
  CheckmarkCircleOutline,
  PersonRemoveOutline,
  PencilOutline,
  TrashOutline,
  ShieldOutline
} from '@vicons/ionicons5';
import { getUsers, createUser, updateUser, deleteUser } from '@/api/user';
import { getRoleList } from '@/api/role';
import { useAppStore } from '@/stores/app';
import { NButton, NSpace, NTag, NIcon } from 'naive-ui';
import type { DataTableColumns, PaginationProps } from 'naive-ui';

const { t } = useI18n();
const appStore = useAppStore();
const isDark = computed(() => appStore.isDark);

// Naive UI 主题配置
const dataTableThemeOverrides = computed(() => ({
  thColor: isDark.value ? '#374151' : '#F8F9FA',
  tdColor: isDark.value ? '#1F2937' : '#FFFFFF',
  tdColorStriped: isDark.value ? '#252D3B' : '#F8F9FA',
  borderColor: isDark.value ? '#374151' : '#E5E7EB',
  thTextColor: isDark.value ? '#F9FAFB' : '#1F2937',
  tdTextColor: isDark.value ? '#E5E7EB' : '#374451',
}));

const loading = ref(false);
const submitLoading = ref(false);
const showEditModal = ref(false);
const showRoleModal = ref(false);
const editingUser = ref<any>(null);
const selectedRoleId = ref<number | null>(null);
const searchKeyword = ref('');
const filterRole = ref<number | null>(null);
const filterStatus = ref<number | null>(null);

// 统计数据
const total = ref(0);
const activeUsers = ref(0);
const disabledUsers = ref(0);

// 角色选项
const roleOptions = ref<{ label: string; value: number }[]>([]);

// 状态选项（label 须为字符串，不可为 ComputedRef）
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

// 用户列表
const userList = ref<any[]>([]);

// 用户表单
const userForm = reactive({
  username: '',
  password: '',
  email: '',
  phone: '',
  roleId: null as number | null,
  status: 1,
  department: ''
});

// 表单验证规则（message 用函数以便切换语言后即时更新；编辑时无密码字段故不校验密码）
const rules = computed(() => {
  const editing = !!editingUser.value;
  return {
    username: [{ required: true, message: () => t('user.usernameRequired'), trigger: 'blur' }],
    ...(!editing
      ? {
          password: [{ required: true, message: () => t('user.passwordRequired'), trigger: 'blur' }]
        }
      : {}),
    roleId: [{ required: true, message: () => t('user.selectRoleTip'), trigger: 'change' }]
  };
});

const formRef = ref<any>(null);

// 表格列配置（title 须为字符串；h() 中须使用组件引用而非 'n-button' / 错误的 n-icon）
const columns = computed<DataTableColumns<any>>(() => [
  {
    title: 'ID',
    key: 'id',
    width: 80,
    render: (row: any) => h('span', { class: 'id-cell' }, `#${row.id}`)
  },
  {
    title: t('user.username'),
    key: 'username',
    width: 140,
    ellipsis: { tooltip: true }
  },
  {
    title: t('user.email'),
    key: 'email',
    width: 180,
    ellipsis: { tooltip: true },
    render: (row: any) => row.email || '-'
  },
  {
    title: t('user.phone'),
    key: 'phone',
    width: 130,
    render: (row: any) => row.phone || '-'
  },
  {
    title: t('user.role'),
    key: 'roleId',
    width: 130,
    render: (row: any) => {
      const role = roleOptions.value.find(r => r.value === row.roleId);
      return role ? h(NTag, { type: 'info', size: 'small' }, { default: () => role.label }) : '-';
    }
  },
  {
    title: t('user.status'),
    key: 'status',
    width: 100,
    render: (row: any) => {
      const isActive = row.status === 1;
      return h(NTag, {
        type: isActive ? 'success' : 'error',
        size: 'small'
      }, { default: () => (isActive ? t('user.statusNormal') : t('user.statusDisabled')) });
    }
  },
  {
    title: t('user.createTime'),
    key: 'createTime',
    width: 170,
    render: (row: any) => {
      return row.createTime ? new Date(row.createTime).toLocaleString(appStore.language === 'zh-CN' ? 'zh-CN' : 'en-US') : '-';
    }
  },
  {
    title: t('common.actions'),
    key: 'actions',
    width: 240,
    fixed: 'right',
    align: 'center',
    render: (row: any) => {
      return h(NSpace, { size: 'small', justify: 'center' }, {
        default: () => [
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
            type: 'warning',
            onClick: () => openRoleModal(row)
          }, {
            icon: () => h(NIcon, { component: ShieldOutline, size: 16 }),
            default: () => t('user.assignRole')
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

const fetchUsers = async () => {
  loading.value = true;
  try {
    const res: any = await getUsers({
      current: pagination.page,
      size: pagination.pageSize,
      keyword: searchKeyword.value || undefined,
      roleId: filterRole.value || undefined,
      status: filterStatus.value !== null ? filterStatus.value : undefined
    });
    userList.value = res?.data?.records || res?.records || [];
    total.value = res?.data?.total || res?.total || 0;
    
    // 计算统计数据
    activeUsers.value = userList.value.filter((u: any) => u.status === 1).length;
    disabledUsers.value = userList.value.filter((u: any) => u.status === 0).length;
  } catch (error) {
    console.error('Failed to fetch users:', error);
  } finally {
    loading.value = false;
  }
};

const fetchRoles = async () => {
  try {
    const res: any = await getRoleList();
    const roles = res?.data || res || [];
    roleOptions.value = (Array.isArray(roles) ? roles : []).map((r: any) => ({
      label: r.roleName,
      value: r.id
    }));
  } catch (error) {
    console.error('Failed to fetch roles:', error);
  }
};

const handleSearch = () => {
  pagination.page = 1;
  fetchUsers();
};

const resetFilters = () => {
  searchKeyword.value = '';
  filterRole.value = null;
  filterStatus.value = null;
  pagination.page = 1;
  fetchUsers();
};

const handlePageChange = (page: number) => {
  pagination.page = page;
  fetchUsers();
};

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize;
  pagination.page = 1;
  fetchUsers();
};

const openCreateModal = () => {
  editingUser.value = null;
  resetForm();
  showEditModal.value = true;
};

const openEditModal = (user: any) => {
  editingUser.value = user;
  userForm.username = user.username;
  userForm.email = user.email || '';
  userForm.phone = user.phone || '';
  userForm.roleId = user.roleId;
  userForm.status = user.status;
  userForm.department = user.department || '';
  showEditModal.value = true;
};

const openRoleModal = (user: any) => {
  editingUser.value = user;
  selectedRoleId.value = user.roleId;
  showRoleModal.value = true;
};

const resetForm = () => {
  userForm.username = '';
  userForm.password = '';
  userForm.email = '';
  userForm.phone = '';
  userForm.roleId = null;
  userForm.status = 1;
  userForm.department = '';
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitLoading.value = true;
  try {
    if (editingUser.value) {
      await updateUser(editingUser.value.id, {
        email: userForm.email,
        phone: userForm.phone,
        roleId: userForm.roleId,
        status: userForm.status,
        department: userForm.department
      } as any);
      window.$message?.success(t('user.updateSuccess'));
    } else {
      await createUser({
        username: userForm.username,
        password: userForm.password,
        email: userForm.email,
        phone: userForm.phone,
        roleId: userForm.roleId,
        status: userForm.status
      } as any);
      window.$message?.success(t('user.createSuccess'));
    }
    showEditModal.value = false;
    fetchUsers();
  } catch (e: any) {
    window.$message?.error(e?.message || t('common.operationFailed'));
  } finally {
    submitLoading.value = false;
  }
};

const handleAssignRole = async () => {
  if (!selectedRoleId.value) return;
  submitLoading.value = true;
  try {
    await updateUser(editingUser.value.id, {
      roleId: selectedRoleId.value
    } as any);
    window.$message?.success(t('user.updateSuccess'));
    showRoleModal.value = false;
    fetchUsers();
  } catch (e: any) {
    window.$message?.error(e?.message || t('common.operationFailed'));
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = (user: any) => {
  window.$dialog.warning({
    title: t('common.confirm'),
    content: `${t('user.deleteConfirm')}: ${user.username}?`,
    positiveText: t('common.delete'),
    negativeText: t('common.cancel'),
    onPositiveClick: async () => {
      try {
        await deleteUser(user.id);
        window.$message?.success(t('user.deleteSuccess'));
        fetchUsers();
      } catch (e: any) {
        window.$message?.error(e?.message || t('common.deleteFailed'));
      }
    }
  });
};

onMounted(() => {
  fetchUsers();
  fetchRoles();
});

// 新建用户时，确保表单值为空（防止浏览器自动填充）
watch(showEditModal, (val) => {
  if (val && !editingUser.value) {
    nextTick(() => {
      resetForm();
    });
  }
});
</script>

<style scoped>
.user-management {
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
  background: rgba(255, 209, 102, 0.1);
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
</style>
