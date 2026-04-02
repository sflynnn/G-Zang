<template>
  <div class="permission-management" :class="{ 'dark-theme': isDark }">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">{{ t('menu.permissionManagement') }}</h1>
          <p class="page-subtitle">{{ t('menu.permissionDesc') }}</p>
        </div>
        <n-space>
          <n-button @click="refreshPermissions">
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
            <n-icon :component="KeyOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ totalPermissions }}</span>
            <span class="stat-label">{{ t('common.total') + t('menu.permissions') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false" v-for="(count, group) in permissionGroups" :key="group">
        <div class="stat-content">
          <div class="stat-icon" :class="getGroupIconClass(group)">
            <n-icon :component="getGroupIcon(group)" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ count }}</span>
            <span class="stat-label">{{ group }}</span>
          </div>
        </div>
      </n-card>
    </div>

    <!-- 权限树形结构 + 表格 -->
    <div class="content-grid">
      <!-- 左侧：权限树 -->
      <n-card class="tree-card" :bordered="false">
        <template #header>
          <div class="card-header">
            <span class="card-title">{{ t('permission.permissionTree') }}</span>
            <n-tag size="small" type="info">{{ flatPermissions.length }}</n-tag>
          </div>
        </template>
        
        <div class="tree-wrapper">
          <n-input
            v-model:value="treeSearchKeyword"
            :placeholder="t('common.search') + '...'"
            clearable
            class="tree-search"
          >
            <template #prefix>
              <n-icon :component="SearchOutline" />
            </template>
          </n-input>

          <n-tree
            v-model:selected-keys="selectedKeys"
            :data="permissionTreeData"
            block-line
            select-mode="single"
            virtual-scroll
            :default-expand-all="true"
            :pattern="treeSearchKeyword"
            :render-label="renderLabel"
            class="permission-tree"
            @update:selected-keys="handleSelectPermission"
          />
        </div>
      </n-card>

      <!-- 右侧：权限详情 -->
      <n-card class="detail-card" :bordered="false">
        <template #header>
          <div class="card-header">
            <span class="card-title">{{ t('permission.selectPermission') }}</span>
          </div>
        </template>

        <div v-if="selectedPermission" class="permission-detail">
          <n-descriptions label-placement="left" :column="2" bordered size="small">
            <n-descriptions-item :label="t('permission.name')">
              {{ selectedPermission.permissionName }}
            </n-descriptions-item>
            <n-descriptions-item :label="t('permission.code')">
              <code class="code-inline">{{ selectedPermission.permissionCode }}</code>
            </n-descriptions-item>
            <n-descriptions-item :label="t('permission.group')">
              <n-tag :type="getGroupTagType(selectedPermission.permissionGroup)" size="small">
                {{ selectedPermission.permissionGroup }}
              </n-tag>
            </n-descriptions-item>
            <n-descriptions-item :label="t('permission.level')">
              <n-tag :type="getLevelTagType(selectedPermission.permissionLevel)" size="small">
                {{ getLevelText(selectedPermission.permissionLevel) }}
              </n-tag>
            </n-descriptions-item>
            <n-descriptions-item :label="t('permission.type')">
              {{ getpermissionModuleText(selectedPermission.permissionModule) }}
            </n-descriptions-item>
            <n-descriptions-item :label="t('permission.desc')" :span="2">
              {{ selectedPermission.description || '-' }}
            </n-descriptions-item>
            <n-descriptions-item :label="t('user.createTime')" :span="2">
              {{ selectedPermission.createTime ? new Date(selectedPermission.createTime).toLocaleString(appStore.language === 'zh-CN' ? 'zh-CN' : 'en-US') : '-' }}
            </n-descriptions-item>
          </n-descriptions>
        </div>

        <n-empty v-else :description="t('permission.selectPermission')" />
      </n-card>
    </div>

    <!-- 表格视图 -->
    <n-card class="table-card" :bordered="false">
      <template #header>
        <div class="card-header">
          <span class="card-title">{{ t('menu.permissionList') }}</span>
          <n-text depth="3" style="font-size: 12px">
            {{ t('common.total') }} {{ flatPermissions.length }} {{ t('menu.permissions') }}
          </n-text>
        </div>
      </template>

      <n-data-table
        :loading="loading"
        :columns="columns"
        :data="filteredPermissions"
        :pagination="{ pageSize: 20 }"
        :theme-overrides="dataTableThemeOverrides"
        striped
      />
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, h } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  SearchOutline,
  RefreshOutline,
  KeyOutline,
  ConstructOutline,
  BusinessOutline,
  WalletOutline,
  CartOutline,
  PeopleOutline,
  SettingsOutline,
  DocumentTextOutline,
  GridOutline
} from '@vicons/ionicons5';
import { usePermissionStore } from '@/stores/permission';
import { useAppStore } from '@/stores/app';
import type { PermissionResponse } from '@/api/permission';
import { NTag } from 'naive-ui';
import type { DataTableColumns, TreeOption } from 'naive-ui';

const { t } = useI18n();
const permStore = usePermissionStore();
const appStore = useAppStore();

const isDark = computed(() => appStore.isDark);

const loading = computed(() => permStore.loading);
const flatPermissions = computed(() => permStore.flatPermissions);
const groupedPermissions = computed(() => permStore.groupedPermissions);

const treeSearchKeyword = ref('');
const selectedKeys = ref<string[]>([]);
const selectedPermission = ref<PermissionResponse | null>(null);

// 统计数据
const totalPermissions = computed(() => flatPermissions.value.length);
const permissionGroups = computed(() => {
  const groups: Record<string, number> = {};
  Object.entries(groupedPermissions.value).forEach(([group, perms]) => {
    groups[group] = (perms as PermissionResponse[]).length;
  });
  return groups;
});

// Naive UI 主题配置
const dataTableThemeOverrides = computed(() => ({
  thColor: isDark.value ? '#374151' : '#F8F9FA',
  tdColor: isDark.value ? '#1F2937' : '#FFFFFF',
  tdColorStriped: isDark.value ? '#252D3B' : '#F8F9FA',
  borderColor: isDark.value ? '#374151' : '#E5E7EB',
  thTextColor: isDark.value ? '#F9FAFB' : '#1F2937',
  tdTextColor: isDark.value ? '#E5E7EB' : '#374451',
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
      key: String(perm.id),
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

// 过滤后的权限列表
const filteredPermissions = computed(() => {
  if (!treeSearchKeyword.value) return flatPermissions.value;
  const keyword = treeSearchKeyword.value.toLowerCase();
  return flatPermissions.value.filter(p => 
    p.permissionName.toLowerCase().includes(keyword) ||
    p.permissionCode.toLowerCase().includes(keyword)
  );
});

// 获取分组图标
const getGroupIcon = (group: string) => {
  const iconMap: Record<string, any> = {
    'SYSTEM': SettingsOutline,
    'ORGANIZATION': BusinessOutline,
    'FINANCE': WalletOutline,
    'BUSINESS': CartOutline,
    'USER': PeopleOutline,
    'ROLE': KeyOutline,
    'COMPANY': ConstructOutline,
    'PERMISSION': DocumentTextOutline,
    'Other': GridOutline
  };
  return iconMap[group] || GridOutline;
};

// 获取分组图标样式
const getGroupIconClass = (group: string) => {
  const classMap: Record<string, string> = {
    'SYSTEM': 'stat-icon-danger',
    'ORGANIZATION': 'stat-icon-warning',
    'FINANCE': 'stat-icon-success',
    'BUSINESS': 'stat-icon-info',
    'USER': 'stat-icon-primary',
    'ROLE': 'stat-icon-secondary',
    'COMPANY': 'stat-icon-warning',
    'PERMISSION': 'stat-icon-info',
    'Other': 'stat-icon-primary'
  };
  return classMap[group] || 'stat-icon-primary';
};

// 获取分组标签类型
const getGroupTagType = (group: string) => {
  const typeMap: Record<string, 'default' | 'error' | 'info' | 'success' | 'warning' | 'primary'> = {
    'SYSTEM': 'error',
    'ORGANIZATION': 'warning',
    'FINANCE': 'success',
    'BUSINESS': 'info',
    'USER': 'primary',
    'ROLE': 'primary',
    'COMPANY': 'warning',
    'PERMISSION': 'info',
    'Other': 'default'
  };
  return typeMap[group] || 'default';
};

// 获取级别文本
const getLevelText = (level: number) => {
  const levelMap: Record<number, string> = {
    1: t('permission.system'),
    2: t('permission.company'),
    3: t('permission.personal')
  };
  return levelMap[level] || level;
};

// 获取级别标签类型
const getLevelTagType = (level: number) => {
  const typeMap: Record<number, 'default' | 'error' | 'info' | 'success' | 'warning' | 'primary'> = {
    1: 'error',
    2: 'warning',
    3: 'success'
  };
  return typeMap[level] || 'default';
};

// 获取权限类型文本
const getpermissionModuleText = (type: string) => {
  const typeMap: Record<string, string> = {
    'MENU': t('permission.menu'),
    'BUTTON': t('permission.button'),
    'API': t('permission.api')
  };
  return typeMap[type] || type;
};

// 渲染树节点标签（原生元素 h() 第三参须为子 VNode 数组，不可用 { default: () => ... } 插槽对象）
const renderLabel = ({ option }: { option: TreeOption }) => {
  const perm = option.data as PermissionResponse | undefined;
  const children: ReturnType<typeof h>[] = [
    h('span', { class: 'node-label' }, String(option.label ?? ''))
  ];
  if (option.isLeaf && perm?.permissionCode) {
    children.push(h('code', { class: 'node-code' }, perm.permissionCode));
  }
  return h('div', { class: 'tree-node-label' }, children);
};

// 选择权限
const handleSelectPermission = (keys: string[]) => {
  if (keys.length > 0) {
    const key = keys[0];
    if (!isNaN(Number(key))) {
      selectedPermission.value = flatPermissions.value.find(p => String(p.id) === key) || null;
    }
  }
};

// 刷新权限
const refreshPermissions = async () => {
  await permStore.fetchPermissions();
  permStore.buildFlatList();
};

const columns = computed<DataTableColumns<PermissionResponse>>(() => [
  {
    title: 'ID',
    key: 'id',
    width: 80,
    render: (row: PermissionResponse) => h('span', { class: 'id-cell' }, `#${row.id}`)
  },
  {
    title: t('permission.name'),
    key: 'permissionName',
    width: 160,
    ellipsis: { tooltip: true }
  },
  {
    title: t('permission.code'),
    key: 'permissionCode',
    width: 220,
    render: (row: PermissionResponse) => h('code', { class: 'code-cell' }, row.permissionCode)
  },
  {
    title: t('permission.group'),
    key: 'permissionGroup',
    width: 120,
    render: (row: PermissionResponse) => {
      return h(NTag, { type: getGroupTagType(row.permissionGroup) as any, size: 'small' }, {
        default: () => row.permissionGroup
      });
    }
  },
  {
    title: t('permission.level'),
    key: 'permissionLevel',
    width: 100,
    render: (row: PermissionResponse) => {
      return h(NTag, { type: getLevelTagType(row.permissionLevel) as any, size: 'small' }, {
        default: () => getLevelText(row.permissionLevel)
      });
    }
  },
  {
    title: t('permission.type'),
    key: 'permissionModule',
    width: 100,
    render: (row: PermissionResponse) => {
      const typeMap: Record<string, string> = {
        MENU: 'success',
        BUTTON: 'info',
        API: 'warning'
      };
      return h(NTag, { type: (typeMap[row.permissionModule] as any) || 'default', size: 'small' }, {
        default: () => row.permissionModule
      });
    }
  },
  {
    title: t('permission.desc'),
    key: 'description',
    ellipsis: { tooltip: true },
    render: (row: PermissionResponse) => row.description || '-'
  },
  {
    title: t('user.createTime'),
    key: 'createTime',
    width: 200,
    render: (row: PermissionResponse) => {
      return row.createTime ? new Date(row.createTime).toLocaleString(appStore.language === 'zh-CN' ? 'zh-CN' : 'en-US') : '-';
    }
  }
]);

onMounted(async () => {
  await permStore.fetchPermissions();
  permStore.buildFlatList();

  // 默认选中第一个权限
  if (flatPermissions.value.length > 0) {
    const firstPermission = flatPermissions.value[0];
    selectedKeys.value = [String(firstPermission.id)];
    selectedPermission.value = firstPermission;
  }
});
</script>

<style scoped>
.permission-management {
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
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
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

.stat-icon-danger {
  background: rgba(239, 71, 111, 0.1);
  color: #EF476F;
}

.stat-icon-info {
  background: rgba(17, 138, 178, 0.1);
  color: #118AB2;
}

.stat-icon-secondary {
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

.dark-theme .stat-icon-danger {
  background: rgba(239, 71, 111, 0.2);
  color: #F87171;
}

.dark-theme .stat-icon-info {
  background: rgba(17, 138, 178, 0.2);
  color: #38BDF8;
}

.dark-theme .stat-icon-secondary {
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

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

.tree-card,
.detail-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-weight: 600;
  color: #1F2937;
}

.dark-theme .card-title {
  color: #F9FAFB;
}

/* 树形结构 */
.tree-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tree-search {
  margin-bottom: 8px;
}

.permission-tree {
  max-height: 400px;
  overflow-y: auto;
}

/* render-label 在 n-tree 内部子组件中挂载，须 :deep 才能命中 scoped 选择器 */
.permission-tree :deep(.tree-node-label) {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  min-width: 0;
}

.permission-tree :deep(.node-label) {
  min-width: 0;
}

.permission-tree :deep(.node-code) {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  padding: 1px 6px;
  background: rgba(15, 76, 92, 0.1);
  border-radius: 3px;
  color: #0F4C5C;
  flex-shrink: 0;
}

.dark-theme .permission-tree :deep(.node-code) {
  background: rgba(15, 76, 92, 0.2);
  color: #60A5FA;
}

/* 权限详情 */
.permission-detail {
  padding: 8px 0;
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

/* 权限代码 */
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
</style>
