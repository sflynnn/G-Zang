<template>
  <div class="user-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <h2>用户管理</h2>
        <p>管理系统用户账户</p>
      </div>
      <n-button
        type="primary"
        @click="showCreateModal = true"
        v-permission="'USER_ADD'"
      >
        <template #icon>
          <n-icon :component="AddOutline" />
        </template>
        新建用户
      </n-button>
    </div>

    <!-- 搜索和筛选 -->
    <n-card class="filter-card">
      <n-space>
        <n-input
          v-model:value="searchKeyword"
          placeholder="搜索用户名或昵称"
          style="width: 200px"
          @input="handleSearch"
        >
          <template #prefix>
            <n-icon :component="SearchOutline" />
          </template>
        </n-input>

        <!-- 批量操作按钮 -->
        <n-space v-if="selectedRowKeys.length > 0">
          <n-button
            type="error"
            ghost
            @click="handleBatchDelete"
            v-permission="'USER_DELETE'"
          >
            批量删除
          </n-button>
          <n-button
            type="warning"
            ghost
            @click="handleBatchDisable"
            v-permission="'USER_EDIT'"
          >
            批量禁用
          </n-button>
          <n-button
            type="success"
            ghost
            @click="handleBatchEnable"
            v-permission="'USER_EDIT'"
          >
            批量启用
          </n-button>
        </n-space>

        <n-select
          v-model:value="filterRole"
          placeholder="选择角色"
          style="width: 150px"
          clearable
          @update:value="handleFilter"
        >
          <n-option
            v-for="role in roleOptions"
            :key="role.value"
            :value="role.value"
            :label="role.label"
          />
        </n-select>

        <n-select
          v-model:value="filterStatus"
          placeholder="选择状态"
          style="width: 150px"
          clearable
          @update:value="handleFilter"
        >
          <n-option
            v-for="status in statusOptions"
            :key="status.value"
            :value="status.value"
            :label="status.label"
          />
        </n-select>

        <n-button @click="resetFilters">
          重置
        </n-button>
      </n-space>
    </n-card>

    <!-- 用户列表 -->
    <n-card>
      <n-data-table
        :loading="loading"
        :columns="columns"
        :data="users"
        :pagination="pagination"
        @update:checked-row-keys="handleCheck"
        max-height="600"
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

    <!-- 创建/编辑用户模态框 -->
    <n-modal
      v-model:show="showCreateModal"
      :title="editingUser ? '编辑用户' : '新建用户'"
      preset="card"
      size="huge"
    >
      <n-form
        ref="formRef"
        :model="userForm"
        :rules="rules"
        label-placement="top"
      >
        <n-grid cols="2 s:1" responsive="screen">
          <n-form-item label="用户名" path="username" required>
            <n-input
              v-model:value="userForm.username"
              placeholder="请输入用户名"
              :disabled="!!editingUser"
            />
          </n-form-item>

          <n-form-item label="昵称" path="nickname">
            <n-input
              v-model:value="userForm.nickname"
              placeholder="请输入昵称"
            />
          </n-form-item>

          <n-form-item
            v-if="!editingUser"
            label="密码"
            path="password"
            required
          >
            <n-input
              v-model:value="userForm.password"
              type="password"
              placeholder="请输入密码"
            />
          </n-form-item>

          <n-form-item label="邮箱" path="avatar">
            <n-input
              v-model:value="userForm.avatar"
              placeholder="请输入邮箱"
            />
          </n-form-item>

          <n-form-item label="角色" path="roleId" required>
            <n-select
              v-model:value="userForm.roleId"
              placeholder="请选择角色"
            >
              <n-option
                v-for="role in roleOptions"
                :key="role.value"
                :value="role.value"
                :label="role.label"
              />
            </n-select>
          </n-form-item>

          <n-form-item label="状态" path="status" required>
            <n-select
              v-model:value="userForm.status"
              placeholder="请选择状态"
            >
              <n-option
                v-for="status in statusOptions"
                :key="status.value"
                :value="status.value"
                :label="status.label"
              />
            </n-select>
          </n-form-item>
        </n-grid>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showCreateModal = false">取消</n-button>
          <n-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            {{ editingUser ? '更新' : '创建' }}
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, h } from 'vue';
import { AddOutline, SearchOutline, CreateOutline, TrashOutline } from '@vicons/ionicons5';
import { useUserStore } from '@/stores/user';
import type { DataTableColumns } from 'naive-ui';

const userStore = useUserStore();

// 响应式数据
const loading = ref(false);
const submitLoading = ref(false);
const showCreateModal = ref(false);
const editingUser = ref(null);
const searchKeyword = ref('');
const filterRole = ref(null);
const filterStatus = ref(null);

// 用户表单
const userForm = reactive({
  username: '',
  nickname: '',
  password: '',
  avatar: '',
  roleId: null,
  status: 1
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
};

// 角色选项
const roleOptions = [
  { value: 1, label: '超级管理员' },
  { value: 2, label: '管理员' },
  { value: 3, label: '财务' },
  { value: 4, label: '普通用户' }
];

// 状态选项
const statusOptions = [
  { value: 1, label: '正常' },
  { value: 0, label: '禁用' }
];

// 分页配置
const pagination = reactive({
  page: 1,
  pageSize: 10,
  showSizePicker: true,
  showQuickJumper: true
});

// 表格列配置
const columns: DataTableColumns = [
  {
    type: 'selection'
  },
  {
    title: '用户名',
    key: 'username',
    width: 120
  },
  {
    title: '昵称',
    key: 'nickname',
    width: 120
  },
  {
    title: '角色',
    key: 'roleId',
    width: 100,
    render: (row) => {
      const role = roleOptions.find(r => r.value === row.roleId);
      return role ? role.label : '未知';
    }
  },
  {
    title: '状态',
    key: 'status',
    width: 80,
    render: (row) => {
      const status = statusOptions.find(s => s.value === row.status);
      return h('n-tag', {
        type: row.status === 1 ? 'success' : 'error'
      }, { default: () => status?.label || '未知' });
    }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 160,
    render: (row) => {
      return new Date(row.createTime as string).toLocaleString('zh-CN');
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 120,
    render: (row) => {
      return h('n-space', {}, {
        default: () => [
          h('n-button', {
            size: 'small',
            type: 'primary',
            ghost: true,
            onClick: () => handleEdit(row)
          }, {
            default: () => '编辑',
            icon: () => h('n-icon', { component: CreateOutline }),
            directives: [{ name: 'permission', value: 'USER_EDIT' }]
          }),
          h('n-button', {
            size: 'small',
            type: 'error',
            ghost: true,
            onClick: () => handleDelete(row)
          }, {
            default: () => '删除',
            icon: () => h('n-icon', { component: TrashOutline }),
            directives: [{ name: 'permission', value: 'USER_DELETE' }]
          })
        ]
      });
    }
  }
];

// 计算属性
const users = computed(() => userStore.users);
const total = computed(() => userStore.total);

// 方法
const handleSearch = () => {
  fetchUsers();
};

const handleFilter = () => {
  fetchUsers();
};

const resetFilters = () => {
  searchKeyword.value = '';
  filterRole.value = null;
  filterStatus.value = null;
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

const selectedRowKeys = ref<any[]>([]);

const handleCheck = (rowKeys: any[]) => {
  selectedRowKeys.value = rowKeys;
};

const handleBatchDelete = async () => {
  if (selectedRowKeys.value.length === 0) return;

  try {
    // TODO: 实现批量删除逻辑
    console.log('批量删除用户:', selectedRowKeys.value);
    window.$message?.success(`成功删除 ${selectedRowKeys.value.length} 个用户`);
    selectedRowKeys.value = [];
  } catch (error) {
    console.error('批量删除失败:', error);
    window.$message?.error('批量删除失败');
  }
};

const handleBatchDisable = async () => {
  if (selectedRowKeys.value.length === 0) return;

  try {
    // TODO: 实现批量禁用逻辑
    console.log('批量禁用用户:', selectedRowKeys.value);
    window.$message?.success(`成功禁用 ${selectedRowKeys.value.length} 个用户`);
  } catch (error) {
    console.error('批量禁用失败:', error);
    window.$message?.error('批量禁用失败');
  }
};

const handleBatchEnable = async () => {
  if (selectedRowKeys.value.length === 0) return;

  try {
    // TODO: 实现批量启用逻辑
    console.log('批量启用用户:', selectedRowKeys.value);
    window.$message?.success(`成功启用 ${selectedRowKeys.value.length} 个用户`);
  } catch (error) {
    console.error('批量启用失败:', error);
    window.$message?.error('批量启用失败');
  }
};

const handleEdit = (user: any) => {
  editingUser.value = user;
  Object.assign(userForm, user);
  showCreateModal.value = true;
};

const handleDelete = async (user: any) => {
  try {
    // TODO: 实现删除逻辑
    console.log('删除用户:', user);
    window.$message?.success('用户删除成功');
  } catch (error) {
    console.error('删除用户失败:', error);
    window.$message?.error('删除用户失败');
  }
};

const handleSubmit = async () => {
  try {
    submitLoading.value = true;
    // TODO: 实现提交逻辑
    console.log('提交表单:', userForm);
    window.$message?.success('用户创建成功');
    showCreateModal.value = false;
    resetForm();
  } catch (error: any) {
    console.error('提交失败:', error);
    window.$message?.error(error.message || '创建用户失败');
  } finally {
    submitLoading.value = false;
  }
};

const resetForm = () => {
  Object.assign(userForm, {
    username: '',
    nickname: '',
    password: '',
    avatar: '',
    roleId: null,
    status: 1
  });
  editingUser.value = null;
};

const fetchUsers = async () => {
  loading.value = true;
  try {
    await userStore.fetchUsers({
      current: pagination.page,
      size: pagination.pageSize,
      keyword: searchKeyword.value,
      roleId: filterRole.value,
      status: filterStatus.value
    });
  } catch (error) {
    console.error('获取用户列表失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.user-management {
  padding: 1rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.page-title h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--n-text-color);
}

.page-title p {
  color: var(--n-text-color-3);
  font-size: 0.9rem;
}

.filter-card {
  margin-bottom: 1rem;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 1rem;
  padding: 1rem 0;
}
</style>
