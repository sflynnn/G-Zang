<template>
  <div class="role-management" :class="{ 'dark-theme': isDark }">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">{{ t('menu.roleManagement') }}</h1>
          <p class="page-subtitle">{{ t('menu.roleManagementDesc') }}</p>
        </div>
        <n-button type="primary" @click="openCreateModal">
          <template #icon>
            <n-icon :component="AddOutline" />
          </template>
          {{ t('role.create') }}
        </n-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-primary">
            <n-icon :component="ShieldOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ totalRoles }}</span>
            <span class="stat-label">{{ t('role.total') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-success">
            <n-icon :component="BuildOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ systemRoles }}</span>
            <span class="stat-label">{{ t('role.systemBuiltIn') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-warning">
            <n-icon :component="CopyOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ customRoles }}</span>
            <span class="stat-label">{{ t('role.custom') }}</span>
          </div>
        </div>
      </n-card>
    </div>

    <!-- 角色列表 -->
    <n-card class="table-card" :bordered="false">
      <n-data-table
        :loading="loading"
        :columns="columns"
        :data="roles"
        :pagination="false"
        :theme-overrides="dataTableThemeOverrides"
        striped
      />
    </n-card>

    <!-- 新建/编辑角色模态框 -->
    <n-modal
      v-model:show="showModal"
      preset="card"
      :title="editingRole ? t('role.edit') : t('role.create')"
      style="width: 720px; max-width: 90vw"
    >
      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="top"
      >
        <n-grid :cols="2" :x-gap="16" responsive="screen">
          <n-gi>
            <n-form-item :label="t('role.name')" path="roleName" required>
              <n-input 
                v-model:value="formData.roleName" 
                :placeholder="t('role.namePlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('role.code')" path="roleCode" :disabled="!!editingRole" required>
              <n-input 
                v-model:value="formData.roleCode" 
                :placeholder="t('role.codePlaceholder')"
                :disabled="!!editingRole"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('role.dataScope')" path="dataScope" required>
              <n-select 
                v-model:value="formData.dataScope" 
                :options="dataScopeOptions" 
                :placeholder="t('role.selectDataScope')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('role.type')" path="roleType">
              <n-select 
                v-model:value="formData.roleType" 
                :options="roleTypeOptions" 
                :disabled="!!editingRole"
              />
            </n-form-item>
          </n-gi>
          <n-gi span="2">
            <n-form-item :label="t('role.description')" path="description">
              <n-input 
                v-model:value="formData.description" 
                type="textarea" 
                :placeholder="t('role.descPlaceholder')"
                :autosize="{ minRows: 2, maxRows: 4 }"
              />
            </n-form-item>
          </n-gi>
        </n-grid>

        <!-- 权限分配 -->
        <n-divider>
          <span class="divider-text">{{ t('role.assignPermissions') }}</span>
        </n-divider>
        
        <div v-if="permLoading" class="permission-loading">
          <n-spin size="small" />
          <span>{{ t('role.loadingPermissions') }}</span>
        </div>
        
        <div v-else-if="flatPermissions.length > 0" class="permission-tree-wrapper">
          <n-tree
            v-model:checked-keys="formData.permissionIds"
            :data="permissionTreeData"
            checkable
            expand-on-click-node
            block-line
            cascade
            check-strategy="all"
            virtual-scroll
            :render-suffix="renderSuffix"
            :default-expand-all="true"
            class="permission-tree"
          />
        </div>
        <n-empty v-else :description="t('role.noPermissions')" />
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">
            {{ t('common.cancel') }}
          </n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">
            {{ editingRole ? t('common.update') : t('common.create') }}
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
  AddOutline,
  ShieldOutline,
  BuildOutline,
  CopyOutline,
  PencilOutline,
  TrashOutline,
  LockClosedOutline
} from '@vicons/ionicons5';
import { useRoleStore } from '@/stores/role';
import { usePermissionStore } from '@/stores/permission';
import { useAppStore } from '@/stores/app';
import type { RoleResponse } from '@/api/role';
import { NButton, NSpace, NTag, NIcon } from 'naive-ui';
import type { DataTableColumns, TreeOption } from 'naive-ui';

const { t } = useI18n();
const roleStore = useRoleStore();
const permStore = usePermissionStore();
const appStore = useAppStore();

const isDark = computed(() => appStore.isDark);

const loading = computed(() => roleStore.loading);
const roles = computed(() => roleStore.roles);
const flatPermissions = computed(() => permStore.flatPermissions);
const permLoading = computed(() => permStore.loading);

const showModal = ref(false);
const submitLoading = ref(false);
const editingRole = ref<RoleResponse | null>(null);
const formRef = ref<any>(null);

// 统计数据
const totalRoles = computed(() => roles.value.length);
const systemRoles = computed(() => roles.value.filter(r => r.roleType === 1).length);
const customRoles = computed(() => roles.value.filter(r => r.roleType !== 1).length);

// Naive UI 主题配置
const dataTableThemeOverrides = computed(() => ({
  thColor: isDark.value ? '#374151' : '#F8F9FA',
  tdColor: isDark.value ? '#1F2937' : '#FFFFFF',
  tdColorStriped: isDark.value ? '#252D3B' : '#F8F9FA',
  borderColor: isDark.value ? '#374151' : '#E5E7EB',
  thTextColor: isDark.value ? '#F9FAFB' : '#1F2937',
  tdTextColor: isDark.value ? '#E5E7EB' : '#374451',
}));

const dataScopeOptions = computed(() => [
  { label: t('role.scopeOwn'), value: 'OWN' },
  { label: t('role.scopeDepartment'), value: 'DEPARTMENT' },
  { label: t('role.scopeCompany'), value: 'COMPANY' },
  { label: t('role.scopeAll'), value: 'ALL' }
]);

const roleTypeOptions = computed(() => [
  { label: t('role.systemBuiltIn'), value: 1 },
  { label: t('role.custom'), value: 2 }
]);

// 表单数据
const formData = reactive({
  roleName: '',
  roleCode: '',
  description: '',
  dataScope: 'OWN',
  roleType: 2,
  permissionIds: [] as (string | number)[]
});

const rules = computed(() => ({
  roleName: [{ required: true, message: t('role.nameRequired'), trigger: 'blur' }],
  roleCode: [{ required: true, message: t('role.codeRequired'), trigger: 'blur' }],
  dataScope: [{ required: true, message: t('role.selectDataScope'), trigger: 'change' }]
}));

// 将权限列表转换为树形结构
const permissionTreeData = computed(() => {
  const groupMap = new Map<string, TreeOption[]>();
  
  flatPermissions.value.forEach(perm => {
    const group = perm.permissionGroup || 'Other';
    if (!groupMap.has(group)) {
      groupMap.set(group, []);
    }
    groupMap.get(group)!.push({
      key: perm.id,
      label: perm.permissionName,
      isLeaf: true,
      data: perm
    });
  });

  return Array.from(groupMap.entries()).map(([group, children]) => ({
    key: group,
    label: group,
    children
  }));
});

// 渲染树节点后缀
const renderSuffix = ({ option }: { option: TreeOption }) => {
  if (option.isLeaf && option.data) {
    return h('span', { class: 'perm-code' }, (option.data as any).permissionCode);
  }
  return null;
};

const columns = computed<DataTableColumns<RoleResponse>>(() => [
  {
    title: 'ID',
    key: 'id',
    width: 80,
    render: (row: RoleResponse) => h('span', { class: 'id-cell' }, `#${row.id}`)
  },
  {
    title: t('role.name'),
    key: 'roleName',
    width: 160,
    ellipsis: { tooltip: true }
  },
  {
    title: t('role.code'),
    key: 'roleCode',
    width: 180,
    render: (row: RoleResponse) => h('code', { class: 'code-cell' }, row.roleCode)
  },
  {
    title: t('role.dataScope'),
    key: 'dataScope',
    width: 160,
    render: (row: RoleResponse) => {
      const opt = dataScopeOptions.value.find(o => o.value === row.dataScope);
      return h(NTag, { type: 'info', size: 'small' }, { default: () => (opt ? opt.label : row.dataScope) });
    }
  },
  {
    title: t('role.type'),
    key: 'roleType',
    width: 120,
    render: (row: RoleResponse) => {
      const type = row.roleType === 1 ? 'error' : 'default';
      const text = row.roleType === 1 ? t('role.systemBuiltIn') : t('role.custom');
      return h(NTag, { type, size: 'small' }, { default: () => text });
    }
  },
  {
    title: t('role.description'),
    key: 'description',
    ellipsis: { tooltip: true },
    render: (row: RoleResponse) => row.description || '-'
  },
  {
    title: t('user.createTime'),
    key: 'createTime',
    width: 200,
    render: (row: RoleResponse) => {
      return row.createTime ? new Date(row.createTime).toLocaleString(appStore.language === 'zh-CN' ? 'zh-CN' : 'en-US') : '-';
    }
  },
  {
    title: t('common.actions'),
    key: 'actions',
    width: 200,
    fixed: 'right',
    align: 'center',
    render: (row: RoleResponse) => {
      const buttons = [
        h(NButton, {
          size: 'small',
          quaternary: true,
          type: 'primary',
          disabled: row.roleType === 1,
          onClick: () => openEditModal(row)
        }, {
          icon: () => h(NIcon, { component: row.roleType === 1 ? LockClosedOutline : PencilOutline, size: 16 }),
          default: () => t('common.edit')
        })
      ];

      if (row.roleType !== 1) {
        buttons.push(
          h(NButton, {
            size: 'small',
            quaternary: true,
            type: 'error',
            onClick: () => handleDelete(row)
          }, {
            icon: () => h(NIcon, { component: TrashOutline, size: 16 }),
            default: () => t('common.delete')
          })
        );
      }

      return h(NSpace, { size: 'small', justify: 'center' }, { default: () => buttons });
    }
  }
]);

const openCreateModal = async () => {
  editingRole.value = null;
  resetForm();
  await loadPermissions();
  showModal.value = true;
};

const openEditModal = async (role: RoleResponse) => {
  editingRole.value = role;
  try {
    await loadPermissions();
    const permIds = await roleStore.fetchRolePermissions(role.id);
    formData.roleName = role.roleName;
    formData.roleCode = role.roleCode;
    formData.description = role.description || '';
    formData.dataScope = role.dataScope || 'OWN';
    formData.roleType = role.roleType || 2;
    formData.permissionIds = (permIds as any) || [];
  } catch (e) {
    console.error(e);
  }
  showModal.value = true;
};

const loadPermissions = async () => {
  if (flatPermissions.value.length === 0) {
    await permStore.fetchPermissions();
    permStore.buildFlatList();
  }
};

const resetForm = () => {
  formData.roleName = '';
  formData.roleCode = '';
  formData.description = '';
  formData.dataScope = 'OWN';
  formData.roleType = 2;
  formData.permissionIds = [];
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }

  submitLoading.value = true;
  try {
    if (editingRole.value) {
      await roleStore.editRole(editingRole.value.id, {
        roleName: formData.roleName,
        description: formData.description,
        dataScope: formData.dataScope,
        permissionIds: formData.permissionIds.map(Number)
      });
      window.$message?.success(t('role.updateSuccess'));
    } else {
      await roleStore.addRole({
        roleName: formData.roleName,
        roleCode: formData.roleCode,
        description: formData.description,
        dataScope: formData.dataScope,
        roleType: formData.roleType,
        permissionIds: formData.permissionIds.map(Number)
      });
      window.$message?.success(t('role.createSuccess'));
    }
    showModal.value = false;
  } catch (e: any) {
    window.$message?.error(e?.message || t('common.operationFailed'));
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = (role: RoleResponse) => {
  window.$dialog.warning({
    title: t('common.confirm'),
    content: `${t('role.deleteConfirm')}: ${role.roleName}?`,
    positiveText: t('common.delete'),
    negativeText: t('common.cancel'),
    onPositiveClick: async () => {
      try {
        await roleStore.removeRole(role.id);
        window.$message?.success(t('role.deleteSuccess'));
      } catch (e: any) {
        window.$message?.error(e?.message || t('common.deleteFailed'));
      }
    }
  });
};

onMounted(async () => {
  await roleStore.fetchRoles();
});
</script>

<style scoped>
.role-management {
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

/* 表格卡片 */
.table-card {
  border-radius: 12px;
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

/* 角色代码 */
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

/* 权限分配 */
.divider-text {
  font-weight: 600;
  color: #1F2937;
}

.dark-theme .divider-text {
  color: #F9FAFB;
}

.permission-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 24px;
  color: #6B7280;
}

.dark-theme .permission-loading {
  color: #9CA3AF;
}

.permission-tree-wrapper {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  padding: 8px;
}

.dark-theme .permission-tree-wrapper {
  border-color: #374151;
}

.permission-tree {
  --n-node-text-color: #374451;
  --n-node-text-color-hover: #0F4C5C;
  --n-node-text-color-active: #0F4C5C;
}

.dark-theme .permission-tree {
  --n-node-text-color: #E5E7EB;
  --n-node-text-color-hover: #60A5FA;
  --n-node-text-color-active: #FB8B24;
}

/* renderSuffix 同样在 n-tree 内部子组件中挂载，须 :deep 才能命中 scoped 选择器 */
.permission-tree :deep(.perm-code) {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  padding: 1px 6px;
  background: rgba(15, 76, 92, 0.1);
  border-radius: 3px;
  color: #0F4C5C;
  flex-shrink: 0;
}

.dark-theme .permission-tree :deep(.perm-code) {
  background: rgba(15, 76, 92, 0.2);
  color: #60A5FA;
}
</style>
