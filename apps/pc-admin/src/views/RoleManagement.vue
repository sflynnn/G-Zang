<template>
  <div class="role-management">
    <div class="page-header">
      <div class="page-title">
        <h2>角色管理</h2>
        <p>管理系统角色和权限分配</p>
      </div>
      <n-button
        type="primary"
        @click="showCreateModal = true"
        v-permission="'ROLE_ADD'"
      >
        <template #icon>
          <n-icon :component="AddOutline" />
        </template>
        新建角色
      </n-button>
    </div>

    <n-card>
      <n-data-table
        :loading="loading"
        :columns="columns"
        :data="roles"
        max-height="600"
      />
    </n-card>

    <!-- 创建角色模态框 -->
    <n-modal
      v-model:show="showCreateModal"
      title="新建角色"
      preset="card"
      size="large"
    >
      <n-form
        ref="formRef"
        :model="roleForm"
        :rules="rules"
        label-placement="top"
      >
        <n-form-item label="角色名称" path="roleName" required>
          <n-input
            v-model:value="roleForm.roleName"
            placeholder="请输入角色名称"
          />
        </n-form-item>

        <n-form-item label="角色编码" path="roleCode" required>
          <n-input
            v-model:value="roleForm.roleCode"
            placeholder="请输入角色编码"
          />
        </n-form-item>

        <n-form-item label="角色描述" path="description">
          <n-input
            v-model:value="roleForm.description"
            type="textarea"
            placeholder="请输入角色描述"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
        </n-form-item>

        <n-form-item label="权限分配">
          <n-transfer
            v-model:value="selectedPermissions"
            :options="permissionOptions"
            :render-source-list="{ title: '可选权限' }"
            :render-target-list="{ title: '已选权限' }"
          />
        </n-form-item>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showCreateModal = false">取消</n-button>
          <n-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            创建
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { AddOutline, CreateOutline, TrashOutline } from '@vicons/ionicons5';

const loading = ref(false);
const submitLoading = ref(false);
const showCreateModal = ref(false);

// 角色表单
const roleForm = reactive({
  roleName: '',
  roleCode: '',
  description: ''
});

// 表单验证规则
const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ]
};

// 选中的权限
const selectedPermissions = ref([]);

// 权限选项
const permissionOptions = [
  { label: '用户管理', value: 'USER_MANAGE' },
  { label: '角色管理', value: 'ROLE_MANAGE' },
  { label: '公司管理', value: 'COMPANY_MANAGE' },
  { label: '记账录入', value: 'TRANSACTION_ADD' },
  { label: '记账查看', value: 'TRANSACTION_VIEW' },
  { label: '记账编辑', value: 'TRANSACTION_EDIT' },
  { label: '记账删除', value: 'TRANSACTION_DELETE' },
  { label: '账户管理', value: 'ACCOUNT_MANAGE' },
  { label: '分类管理', value: 'CATEGORY_MANAGE' },
  { label: '报表查看', value: 'REPORT_VIEW' }
];

// 角色数据
const roles = ref([
  {
    id: 1,
    roleName: '超级管理员',
    roleCode: 'SUPER_ADMIN',
    description: '系统超级管理员，拥有所有权限',
    createTime: '2024-01-01 00:00:00'
  },
  {
    id: 2,
    roleName: '管理员',
    roleCode: 'ADMIN',
    description: '公司管理员，管理公司事务',
    createTime: '2024-01-01 00:00:00'
  },
  {
    id: 3,
    roleName: '财务',
    roleCode: 'FINANCE',
    description: '财务人员，负责记账和报表',
    createTime: '2024-01-01 00:00:00'
  },
  {
    id: 4,
    roleName: '普通用户',
    roleCode: 'USER',
    description: '普通用户，基本记账功能',
    createTime: '2024-01-01 00:00:00'
  }
]);

// 表格列配置
const columns = [
  {
    title: '角色名称',
    key: 'roleName',
    width: 150
  },
  {
    title: '角色编码',
    key: 'roleCode',
    width: 150
  },
  {
    title: '描述',
    key: 'description',
    width: 200
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 160,
    render: (row: any) => {
      return new Date(row.createTime).toLocaleString('zh-CN');
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 120,
    render: (row: any) => {
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
            directives: [{ name: 'permission', value: 'ROLE_EDIT' }]
          }),
          h('n-button', {
            size: 'small',
            type: 'error',
            ghost: true,
            onClick: () => handleDelete(row)
          }, {
            default: () => '删除',
            icon: () => h('n-icon', { component: TrashOutline }),
            directives: [{ name: 'permission', value: 'ROLE_DELETE' }]
          })
        ]
      });
    }
  }
];

const handleEdit = (role: any) => {
  console.log('编辑角色:', role);
};

const handleDelete = (role: any) => {
  try {
    console.log('删除角色:', role);
    window.$message?.success('角色删除成功');
  } catch (error) {
    console.error('删除角色失败:', error);
    window.$message?.error('删除角色失败');
  }
};

const handleSubmit = async () => {
  try {
    submitLoading.value = true;
    console.log('创建角色:', roleForm, selectedPermissions.value);
    window.$message?.success('角色创建成功');
    showCreateModal.value = false;
    resetForm();
  } catch (error: any) {
    console.error('创建角色失败:', error);
    window.$message?.error(error.message || '创建角色失败');
  } finally {
    submitLoading.value = false;
  }
};

const resetForm = () => {
  Object.assign(roleForm, {
    roleName: '',
    roleCode: '',
    description: ''
  });
  selectedPermissions.value = [];
};

onMounted(() => {
  // TODO: 获取角色列表
});
</script>

<style scoped>
.role-management {
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
</style>
