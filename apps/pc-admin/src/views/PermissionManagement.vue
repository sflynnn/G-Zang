<template>
  <div class="permission-management">
    <div class="page-header">
      <div class="page-title">
        <h2>权限管理</h2>
        <p>管理系统权限配置</p>
      </div>
      <n-button
        type="primary"
        @click="showCreateModal = true"
        v-role="'SUPER_ADMIN'"
      >
        <template #icon>
          <n-icon :component="AddOutline" />
        </template>
        新建权限
      </n-button>
    </div>

    <n-card>
      <n-data-table
        :loading="loading"
        :columns="columns"
        :data="permissions"
        max-height="600"
      />
    </n-card>

    <!-- 创建权限模态框 -->
    <n-modal
      v-model:show="showCreateModal"
      title="新建权限"
      preset="card"
      size="large"
    >
      <n-form
        ref="formRef"
        :model="permissionForm"
        :rules="rules"
        label-placement="top"
      >
        <n-form-item label="权限名称" path="permissionName" required>
          <n-input
            v-model:value="permissionForm.permissionName"
            placeholder="请输入权限名称"
          />
        </n-form-item>

        <n-form-item label="权限编码" path="permissionCode" required>
          <n-input
            v-model:value="permissionForm.permissionCode"
            placeholder="请输入权限编码"
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
import { ref, reactive, h } from 'vue';
import { AddOutline, CreateOutline, TrashOutline } from '@vicons/ionicons5';

const loading = ref(false);
const submitLoading = ref(false);
const showCreateModal = ref(false);

// 权限表单
const permissionForm = reactive({
  permissionName: '',
  permissionCode: ''
});

// 表单验证规则
const rules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' }
  ]
};

// 权限数据
const permissions = ref([
  { id: 1, permissionName: '用户管理', permissionCode: 'USER_MANAGE', createTime: '2024-01-01 00:00:00' },
  { id: 2, permissionName: '角色管理', permissionCode: 'ROLE_MANAGE', createTime: '2024-01-01 00:00:00' },
  { id: 3, permissionName: '公司管理', permissionCode: 'COMPANY_MANAGE', createTime: '2024-01-01 00:00:00' },
  { id: 4, permissionName: '记账录入', permissionCode: 'TRANSACTION_ADD', createTime: '2024-01-01 00:00:00' },
  { id: 5, permissionName: '记账查看', permissionCode: 'TRANSACTION_VIEW', createTime: '2024-01-01 00:00:00' },
  { id: 6, permissionName: '记账编辑', permissionCode: 'TRANSACTION_EDIT', createTime: '2024-01-01 00:00:00' },
  { id: 7, permissionName: '记账删除', permissionCode: 'TRANSACTION_DELETE', createTime: '2024-01-01 00:00:00' },
  { id: 8, permissionName: '账户管理', permissionCode: 'ACCOUNT_MANAGE', createTime: '2024-01-01 00:00:00' },
  { id: 9, permissionName: '分类管理', permissionCode: 'CATEGORY_MANAGE', createTime: '2024-01-01 00:00:00' },
  { id: 10, permissionName: '报表查看', permissionCode: 'REPORT_VIEW', createTime: '2024-01-01 00:00:00' }
]);

// 表格列配置
const columns = [
  {
    title: '权限名称',
    key: 'permissionName',
    width: 150
  },
  {
    title: '权限编码',
    key: 'permissionCode',
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
            directives: [{ name: 'role', value: 'SUPER_ADMIN' }]
          }),
          h('n-button', {
            size: 'small',
            type: 'error',
            ghost: true,
            onClick: () => handleDelete(row)
          }, {
            default: () => '删除',
            icon: () => h('n-icon', { component: TrashOutline }),
            directives: [{ name: 'role', value: 'SUPER_ADMIN' }]
          })
        ]
      });
    }
  }
];

const handleEdit = (permission: any) => {
  console.log('编辑权限:', permission);
};

const handleDelete = (permission: any) => {
  try {
    console.log('删除权限:', permission);
    window.$message?.success('权限删除成功');
  } catch (error) {
    console.error('删除权限失败:', error);
    window.$message?.error('删除权限失败');
  }
};

const handleSubmit = async () => {
  try {
    submitLoading.value = true;
    console.log('创建权限:', permissionForm);
    window.$message?.success('权限创建成功');
    showCreateModal.value = false;
    resetForm();
  } catch (error: any) {
    console.error('创建权限失败:', error);
    window.$message?.error(error.message || '创建权限失败');
  } finally {
    submitLoading.value = false;
  }
};

const resetForm = () => {
  Object.assign(permissionForm, {
    permissionName: '',
    permissionCode: ''
  });
};
</script>

<style scoped>
.permission-management {
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
