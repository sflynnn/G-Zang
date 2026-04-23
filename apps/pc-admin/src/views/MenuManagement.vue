<template>
  <div class="menu-management" :class="{ 'dark-theme': isDark }">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">{{ t('menu.menuManagement') }}</h1>
          <p class="page-subtitle">{{ t('menu.menuManagementDesc') }}</p>
        </div>
        <n-button type="primary" @click="openCreateModal">
          <template #icon>
            <n-icon :component="AddOutline" />
          </template>
          {{ t('menu.create') }}
        </n-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-primary">
            <n-icon :component="MenuOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ totalMenus }}</span>
            <span class="stat-label">{{ t('menu.total') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-success">
            <n-icon :component="FolderOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ dirCount }}</span>
            <span class="stat-label">{{ t('menu.directory') }}</span>
          </div>
        </div>
      </n-card>
      <n-card class="stat-card" :bordered="false">
        <div class="stat-content">
          <div class="stat-icon stat-icon-warning">
            <n-icon :component="CopyOutline" :size="24" />
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ systemMenus }}</span>
            <span class="stat-label">{{ t('menu.systemMenus') }}</span>
          </div>
        </div>
      </n-card>
    </div>

    <!-- 菜单管理区域 -->
    <div class="content-grid">
      <!-- 左侧：菜单树 -->
      <n-card class="tree-card" :bordered="false">
        <template #header>
          <div class="tree-header">
            <span class="tree-title">{{ t('menu.menuTree') }}</span>
            <n-button text @click="refreshTree">
              <template #icon>
                <n-icon :component="RefreshOutline" :size="16" />
              </template>
            </n-button>
          </div>
        </template>

        <div v-if="menuLoading" class="tree-loading">
          <n-spin size="small" />
          <span>{{ t('menu.loading') }}</span>
        </div>

        <div v-else class="tree-wrapper">
          <n-tree
            v-model:selected-keys="selectedKeys"
            :data="menuTreeData"
            block-line
            expand-on-click-node
            :default-expand-all="true"
            virtual-scroll
            class="menu-tree"
            @update:selected-keys="handleTreeSelect"
          />
        </div>
      </n-card>

      <!-- 右侧：菜单详情/编辑 -->
      <n-card class="detail-card" :bordered="false">
        <template #header>
          <span class="detail-title">{{ selectedMenu ? t('menu.editMenu') : t('menu.selectMenu') }}</span>
        </template>

        <div v-if="!selectedMenu" class="no-selection">
          <n-empty :description="t('menu.selectMenuHint')" />
        </div>

        <div v-else class="menu-detail">
          <!-- 基本信息 -->
          <n-divider>
            <span class="divider-text">{{ t('menu.basicInfo') }}</span>
          </n-divider>

          <n-descriptions :column="2" :label-placement="'left'" bordered>
            <n-descriptions-item :label="t('menu.menuName')">
              {{ selectedMenu.menuName }}
            </n-descriptions-item>
            <n-descriptions-item :label="t('menu.menuCode')">
              <code class="code-cell">{{ selectedMenu.menuCode }}</code>
            </n-descriptions-item>
            <n-descriptions-item :label="t('menu.menuType')">
              <n-tag :type="getMenuTypeTag(selectedMenu.menuType)" size="small">
                {{ getMenuTypeText(selectedMenu.menuType) }}
              </n-tag>
            </n-descriptions-item>
            <n-descriptions-item :label="t('menu.menuLevel')">
              <n-tag :type="selectedMenu.menuLevel === 1 ? 'info' : 'warning'" size="small">
                {{ selectedMenu.menuLevel === 1 ? t('menu.systemLevel') : t('menu.companyLevel') }}
              </n-tag>
            </n-descriptions-item>
            <n-descriptions-item :label="t('menu.sortOrder')">
              {{ selectedMenu.sortOrder }}
            </n-descriptions-item>
            <n-descriptions-item :label="t('menu.visible')">
              <n-tag :type="selectedMenu.visible === 1 ? 'success' : 'error'" size="small">
                {{ selectedMenu.visible === 1 ? t('common.show') : t('common.hide') }}
              </n-tag>
            </n-descriptions-item>
            <n-descriptions-item :label="t('menu.icon')">
              <n-icon v-if="selectedMenu.icon" :component="getIcon(selectedMenu.icon)" :size="20" />
              <span v-else>-</span>
            </n-descriptions-item>
            <n-descriptions-item :label="t('menu.path')">
              {{ selectedMenu.path || '-' }}
            </n-descriptions-item>
            <n-descriptions-item :label="t('menu.component')" :span="2">
              {{ selectedMenu.component || '-' }}
            </n-descriptions-item>
            <n-descriptions-item :label="t('menu.description')" :span="2">
              {{ selectedMenu.description || '-' }}
            </n-descriptions-item>
          </n-descriptions>

          <!-- 操作按钮 -->
          <div class="detail-actions">
            <n-space justify="end">
              <n-button @click="selectedMenu = null">
                {{ t('common.cancel') }}
              </n-button>
              <n-button type="primary" :loading="submitLoading" @click="openEditModal">
                <template #icon>
                  <n-icon :component="PencilOutline" />
                </template>
                {{ t('common.edit') }}
              </n-button>
              <n-button type="error" :loading="deleteLoading" @click="handleDelete" :disabled="selectedMenu.isSystem === 1">
                <template #icon>
                  <n-icon :component="TrashOutline" />
                </template>
                {{ t('common.delete') }}
              </n-button>
            </n-space>
          </div>
        </div>
      </n-card>
    </div>

    <!-- 新建/编辑菜单模态框 -->
    <n-modal
      v-model:show="showModal"
      preset="card"
      :title="editingMenu ? t('menu.editMenu') : t('menu.createMenu')"
      class="menu-modal"
      style="width: 800px; max-width: 90vw"
    >
      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="top"
      >
        <n-grid :cols="2" :x-gap="16" responsive="screen">
          <n-gi>
            <n-form-item :label="t('menu.menuName')" path="menuName" required>
              <n-input
                v-model:value="formData.menuName"
                :placeholder="t('menu.menuNamePlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('menu.menuCode')" path="menuCode" :disabled="!!editingMenu" required>
              <n-input
                v-model:value="formData.menuCode"
                :placeholder="t('menu.menuCodePlaceholder')"
                :disabled="!!editingMenu"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('menu.menuType')" path="menuType" required>
              <n-select
                v-model:value="formData.menuType"
                :options="menuTypeOptions"
                :placeholder="t('menu.selectMenuType')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('menu.parentMenu')" path="parentId">
              <n-tree-select
                v-model:value="formData.parentId"
                :options="menuTreeOptionsData"
                :placeholder="t('menu.selectParentMenu')"
                clearable
                show-path
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('menu.menuLevel')" path="menuLevel" required>
              <n-select
                v-model:value="formData.menuLevel"
                :options="menuLevelOptions"
                :placeholder="t('menu.selectMenuLevel')"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('menu.sortOrder')" path="sortOrder">
              <n-input-number
                v-model:value="formData.sortOrder"
                :min="0"
                :max="9999"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('menu.icon')" path="icon">
              <n-input
                v-model:value="formData.icon"
                :placeholder="t('menu.iconPlaceholder')"
              >
                <template #prefix>
                  <n-icon v-if="formData.icon" :component="getIcon(formData.icon)" />
                </template>
              </n-input>
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item :label="t('menu.visible')" path="visible">
              <n-switch v-model:value="formData.visible" />
            </n-form-item>
          </n-gi>
          <n-gi span="2">
            <n-form-item :label="t('menu.path')" path="path">
              <n-input
                v-model:value="formData.path"
                :placeholder="t('menu.pathPlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi span="2">
            <n-form-item :label="t('menu.component')" path="component">
              <n-input
                v-model:value="formData.component"
                :placeholder="t('menu.componentPlaceholder')"
              />
            </n-form-item>
          </n-gi>
          <n-gi span="2">
            <n-form-item :label="t('menu.description')" path="description">
              <n-input
                v-model:value="formData.description"
                type="textarea"
                :placeholder="t('menu.descPlaceholder')"
                :autosize="{ minRows: 2, maxRows: 4 }"
              />
            </n-form-item>
          </n-gi>
        </n-grid>

        <!-- 权限配置 -->
        <n-divider>
          <span class="divider-text">{{ t('menu.permissionConfig') }}</span>
        </n-divider>

        <div v-if="permLoading" class="permission-loading">
          <n-spin size="small" />
          <span>{{ t('menu.loadingPermissions') }}</span>
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
            :default-expand-all="true"
            class="permission-tree"
          />
        </div>
        <n-empty v-else :description="t('menu.noPermissions')" />
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">
            {{ t('common.cancel') }}
          </n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">
            {{ editingMenu ? t('common.update') : t('common.create') }}
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, computed, h } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  AddOutline,
  MenuOutline,
  FolderOutline,
  CopyOutline,
  RefreshOutline,
  PencilOutline,
  TrashOutline,
  SettingsOutline,
  PeopleOutline,
  ShieldOutline,
  KeyOutline,
  BusinessOutline,
  DocumentTextOutline,
  WalletOutline,
  CardOutline,
  GridOutline,
  SwapHorizontalOutline,
  BarChartOutline,
  HomeOutline,
  PersonOutline,
  FlashOutline,
  ConstructOutline
} from '@vicons/ionicons5';
import { useMenuStore } from '@/stores/menu';
import { usePermissionStore } from '@/stores/permission';
import { useAppStore } from '@/stores/app';
import type { MenuResponse } from '@/api/menu';
import { NButton, NSpace, NTag, NIcon, NEmpty, NTree, TreeOption } from 'naive-ui';
import type { TreeSelectOption } from 'naive-ui';

const { t } = useI18n();
const menuStore = useMenuStore();
const permStore = usePermissionStore();
const appStore = useAppStore();

const isDark = computed(() => appStore.isDark);
const menuLoading = computed(() => menuStore.loading);
const flatPermissions = computed(() => permStore.flatPermissions);
const permLoading = computed(() => permStore.loading);

const showModal = ref(false);
const submitLoading = ref(false);
const deleteLoading = ref(false);
const editingMenu = ref<MenuResponse | null>(null);
const selectedMenu = ref<MenuResponse | null>(null);
const selectedKeys = ref<string[]>([]);
const formRef = ref<any>(null);
const menuTreeOptionsData = ref<TreeSelectOption[]>([]);

// 统计数据
const allMenus = computed(() => menuStore.flattenMenus(menuStore.menus));
const totalMenus = computed(() => allMenus.value.length);
const dirCount = computed(() => allMenus.value.filter(m => m.menuType === 1).length);
const systemMenus = computed(() => allMenus.value.filter(m => m.isSystem === 1).length);

// Naive UI 主题配置
const dataTableThemeOverrides = computed(() => ({
  thColor: isDark.value ? '#374151' : '#F8F9FA',
  tdColor: isDark.value ? '#1F2937' : '#FFFFFF',
  tdColorStriped: isDark.value ? '#252D3B' : '#F8F9FA',
  borderColor: isDark.value ? '#374151' : '#E5E7EB',
  thTextColor: isDark.value ? '#F9FAFB' : '#1F2937',
  tdTextColor: isDark.value ? '#E5E7EB' : '#374451',
}));

// 菜单类型选项
const menuTypeOptions = computed(() => [
  { label: t('menu.typeDir'), value: 1 },
  { label: t('menu.typeMenu'), value: 2 },
  { label: t('menu.typeButton'), value: 3 },
  { label: t('menu.typeLink'), value: 4 }
]);

// 菜单级别选项
const menuLevelOptions = computed(() => [
  { label: t('menu.systemLevel'), value: 1 },
  { label: t('menu.companyLevel'), value: 2 }
]);

// 图标映射
const iconMap: Record<string, any> = {
  SettingsOutline,
  PeopleOutline,
  ShieldOutline,
  MenuOutline,
  KeyOutline,
  BusinessOutline,
  ConstructOutline,
  DocumentTextOutline,
  WalletOutline,
  CardOutline,
  GridOutline,
  SwapHorizontalOutline,
  BarChartOutline,
  HomeOutline,
  PersonOutline,
  FlashOutline,
  FolderOutline,
  CopyOutline
};

const getIcon = (iconName: string) => {
  return iconMap[iconName] || SettingsOutline;
};

// 获取菜单类型文本
const getMenuTypeText = (type: number) => {
  const options: Record<number, string> = {
    1: t('menu.typeDir'),
    2: t('menu.typeMenu'),
    3: t('menu.typeButton'),
    4: t('menu.typeLink')
  };
  return options[type] || t('common.unknown');
};

// 获取菜单类型标签颜色
const getMenuTypeTag = (type: number) => {
  const types: Record<number, 'info' | 'success' | 'warning' | 'error'> = {
    1: 'info',
    2: 'success',
    3: 'warning',
    4: 'error'
  };
  return types[type] || 'info';
};

// 将菜单数据转换为树形组件所需格式
const menuTreeData = computed(() => {
  return convertToTreeData(menuStore.menus);
});

const convertToTreeData = (menus: MenuResponse[]): TreeOption[] => {
  return menus.map(menu => ({
    key: String(menu.id),
    label: menu.menuName,
    icon: () => h(NIcon, { component: getIcon(menu.icon || 'SettingsOutline'), size: 16 }),
    data: menu,
    children: menu.children && menu.children.length > 0 ? convertToTreeData(menu.children) : undefined
  }));
};

// 树节点选择处理
const handleTreeSelect = (keys: string[]) => {
  if (keys.length > 0) {
    const menu = menuStore.findMenuById(Number(keys[0]));
    if (menu) {
      selectedMenu.value = menu;
    }
  }
};

// 将菜单树转换为 tree-select 选项格式
const convertToSelectOptions = (menus: MenuResponse[], level = 0): TreeSelectOption[] => {
  return menus.map(menu => ({
    key: menu.id,
    label: level > 0 ? '  '.repeat(level) + menu.menuName : menu.menuName,
    children: menu.children && menu.children.length > 0 ? convertToSelectOptions(menu.children, level + 1) : undefined
  }));
};

// 权限树形数据
const permissionTreeData = computed(() => {
  const groupMap = new Map<string, TreeSelectOption[]>();

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

// 表单数据
const formData = reactive({
  menuName: '',
  menuCode: '',
  menuType: 2,
  parentId: null as number | null,
  icon: '',
  path: '',
  component: '',
  sortOrder: 0,
  visible: 1,
  cache: 0,
  affix: 0,
  keepAlive: 0,
  permissionCode: '',
  menuLevel: 1,
  companyId: undefined as number | undefined,
  description: '',
  permissionIds: [] as (string | number)[]
});

const rules = computed(() => ({
  menuName: [{ required: true, message: t('menu.menuNameRequired'), trigger: 'blur' }],
  menuCode: [{ required: true, message: t('menu.menuCodeRequired'), trigger: 'blur' }],
  menuType: [{ required: true, message: t('menu.selectMenuType'), trigger: 'change' }],
  menuLevel: [{ required: true, message: t('menu.selectMenuLevel'), trigger: 'change' }]
}));

// 刷新树
const refreshTree = async () => {
  await menuStore.fetchMenus();
  menuTreeOptionsData.value = convertToSelectOptions(menuStore.menus);
};

// 打开创建弹窗
const openCreateModal = async () => {
  editingMenu.value = null;
  resetForm();
  await loadPermissions();
  await refreshTree();
  showModal.value = true;
};

// 打开编辑弹窗
const openEditModal = async () => {
  if (!selectedMenu.value) return;
  editingMenu.value = selectedMenu.value;
  resetForm();
  await loadPermissions();
  await refreshTree();

  formData.menuName = selectedMenu.value.menuName;
  formData.menuCode = selectedMenu.value.menuCode;
  formData.menuType = selectedMenu.value.menuType;
  formData.parentId = selectedMenu.value.parentId > 0 ? selectedMenu.value.parentId : null;
  formData.icon = selectedMenu.value.icon || '';
  formData.path = selectedMenu.value.path || '';
  formData.component = selectedMenu.value.component || '';
  formData.sortOrder = selectedMenu.value.sortOrder || 0;
  formData.visible = selectedMenu.value.visible;
  formData.cache = selectedMenu.value.cache || 0;
  formData.affix = selectedMenu.value.affix || 0;
  formData.keepAlive = selectedMenu.value.keepAlive || 0;
  formData.permissionCode = selectedMenu.value.permissionCode || '';
  formData.menuLevel = selectedMenu.value.menuLevel;
  formData.companyId = selectedMenu.value.companyId;
  formData.description = selectedMenu.value.description || '';

  // 加载菜单权限
  try {
    const permIds = await menuStore.fetchMenuPermissions(selectedMenu.value.id);
    formData.permissionIds = (permIds as any) || [];
  } catch (e) {
    console.error(e);
  }

  showModal.value = true;
};

// 加载权限列表
const loadPermissions = async () => {
  if (flatPermissions.value.length === 0) {
    await permStore.fetchPermissions();
    permStore.buildFlatList();
  }
};

// 重置表单
const resetForm = () => {
  formData.menuName = '';
  formData.menuCode = '';
  formData.menuType = 2;
  formData.parentId = null;
  formData.icon = '';
  formData.path = '';
  formData.component = '';
  formData.sortOrder = 0;
  formData.visible = 1;
  formData.cache = 0;
  formData.affix = 0;
  formData.keepAlive = 0;
  formData.permissionCode = '';
  formData.menuLevel = 1;
  formData.companyId = undefined;
  formData.description = '';
  formData.permissionIds = [];
};

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }

  submitLoading.value = true;
  try {
    const data = {
      menuName: formData.menuName,
      menuCode: formData.menuCode,
      menuType: formData.menuType,
      parentId: formData.parentId || 0,
      icon: formData.icon || undefined,
      path: formData.path || undefined,
      component: formData.component || undefined,
      sortOrder: formData.sortOrder,
      visible: formData.visible ? 1 : 0,
      cache: formData.cache,
      affix: formData.affix,
      keepAlive: formData.keepAlive,
      permissionCode: formData.permissionCode || undefined,
      menuLevel: formData.menuLevel,
      companyId: formData.companyId,
      description: formData.description || undefined,
      permissionIds: formData.permissionIds.map(Number)
    };

    if (editingMenu.value) {
      await menuStore.editMenu(editingMenu.value.id, data);
      window.$message?.success(t('menu.updateSuccess'));
    } else {
      await menuStore.addMenu(data);
      window.$message?.success(t('menu.createSuccess'));
    }
    showModal.value = false;
    await refreshTree();
  } catch (e: any) {
    window.$message?.error(e?.message || t('common.operationFailed'));
  } finally {
    submitLoading.value = false;
  }
};

// 删除菜单
const handleDelete = () => {
  if (!selectedMenu.value) return;

  window.$dialog.warning({
    title: t('common.confirm'),
    content: `${t('menu.deleteConfirm')}: ${selectedMenu.value.menuName}?`,
    positiveText: t('common.delete'),
    negativeText: t('common.cancel'),
    onPositiveClick: async () => {
      deleteLoading.value = true;
      try {
        await menuStore.removeMenu(selectedMenu.value!.id);
        window.$message?.success(t('menu.deleteSuccess'));
        selectedMenu.value = null;
        selectedKeys.value = [];
      } catch (e: any) {
        window.$message?.error(e?.message || t('common.deleteFailed'));
      } finally {
        deleteLoading.value = false;
      }
    }
  });
};

// 组件挂载时加载数据
onMounted(async () => {
  await menuStore.fetchMenus();
  menuTreeOptionsData.value = convertToSelectOptions(menuStore.menus);
  // 默认选中第一个菜单
  if (menuStore.menus.length > 0) {
    await nextTick();
    const firstMenu = findFirstMenu(menuStore.menus);
    if (firstMenu) {
      selectedMenu.value = firstMenu;
      selectedKeys.value = [String(firstMenu.id)];
    }
  }
});

// 递归查找第一个菜单
const findFirstMenu = (menus: MenuResponse[]): MenuResponse | null => {
  for (const menu of menus) {
    if (menu.menuType === 2 || menu.menuType === 4) { // 菜单类型或外链类型
      return menu;
    }
    if (menu.children && menu.children.length > 0) {
      const found = findFirstMenu(menu.children);
      if (found) return found;
    }
  }
  // 如果没有找到菜单类型的，返回目录类型的第一个子菜单
  for (const menu of menus) {
    if (menu.children && menu.children.length > 0) {
      const found = findFirstMenu(menu.children);
      if (found) return found;
    }
  }
  return menus[0] || null;
};
</script>

<style scoped>
.menu-management {
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

/* 内容区域 */
.content-grid {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 16px;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

/* 树形卡片 */
.tree-card {
  border-radius: 12px;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-title {
  font-weight: 600;
  color: #1F2937;
}

.dark-theme .tree-title {
  color: #F9FAFB;
}

.tree-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 24px;
  color: #6B7280;
}

.dark-theme .tree-loading {
  color: #9CA3AF;
}

.tree-wrapper {
  max-height: 600px;
  overflow-y: auto;
}

.menu-tree {
  --n-node-text-color: #374451;
  --n-node-text-color-hover: #0F4C5C;
  --n-node-text-color-active: #0F4C5C;
}

.dark-theme .menu-tree {
  --n-node-text-color: #E5E7EB;
  --n-node-text-color-hover: #60A5FA;
  --n-node-text-color-active: #FB8B24;
}

/* 详情卡片 */
.detail-card {
  border-radius: 12px;
}

.detail-title {
  font-weight: 600;
  color: #1F2937;
}

.dark-theme .detail-title {
  color: #F9FAFB;
}

.no-selection {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.menu-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.divider-text {
  font-weight: 600;
  color: #1F2937;
}

.dark-theme .divider-text {
  color: #F9FAFB;
}

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

.detail-actions {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #E5E7EB;
}

.dark-theme .detail-actions {
  border-top-color: #374151;
}

/* 权限配置 */
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

/* 菜单模态框 */
:deep(.menu-modal .n-card) {
  max-height: 85vh;
  overflow: hidden;
}

:deep(.menu-modal .n-card__content) {
  max-height: calc(85vh - 80px);
  overflow-y: auto;
  padding-bottom: 16px;
}
</style>
