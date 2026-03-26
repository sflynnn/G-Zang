<template>
    <div class="company-management">
        <div class="page-header">
            <div class="page-title">
                <h2>公司管理</h2>
                <p>管理系统中的公司组织</p>
            </div>
            <n-button type="primary" @click="showCreateModal = true">
                <template #icon>
                    <n-icon :component="AddOutline" />
                </template>
                新建公司
            </n-button>
        </div>

        <n-card>
            <n-data-table :loading="loading" :columns="columns" :data="companies" max-height="600" />
        </n-card>

        <!-- 创建公司模态框 -->
        <n-modal v-model:show="showCreateModal" title="新建公司" preset="card" size="large">
            <n-form ref="formRef" :model="companyForm" :rules="rules" label-placement="top">
                <n-form-item label="公司名称" path="companyName" required>
                    <n-input v-model:value="companyForm.companyName" placeholder="请输入公司名称" />
                </n-form-item>

                <n-form-item label="管理员用户ID" path="adminUserId" required>
                    <n-input-number v-model:value="companyForm.adminUserId" placeholder="请输入管理员用户ID" :min="1" />
                </n-form-item>
            </n-form>

            <template #footer>
                <n-space justify="end">
                    <n-button @click="showCreateModal = false">取消</n-button>
                    <n-button type="primary" :loading="submitLoading" @click="handleSubmit">
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

// 公司表单
const companyForm = reactive({
    companyName: '',
    adminUserId: null
});

// 表单验证规则
const rules = {
    companyName: [
        { required: true, message: '请输入公司名称', trigger: 'blur' }
    ],
    adminUserId: [
        { required: true, message: '请输入管理员用户ID', trigger: 'blur' }
    ]
};

// 公司数据
const companies = ref([
    {
        id: 1,
        companyName: '示例公司',
        adminUserId: 1,
        createTime: '2024-01-01 00:00:00'
    }
]);

// 表格列配置
const columns = [
    {
        title: '公司名称',
        key: 'companyName',
        width: 200
    },
    {
        title: '管理员ID',
        key: 'adminUserId',
        width: 120
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
            icon: () => h('n-icon', { component: CreateOutline })
          }),
                    h('n-button', {
                        size: 'small',
                        type: 'error',
                        ghost: true,
                        onClick: () => handleDelete(row)
                    }, {
                        default: () => '删除',
                        icon: () => h('n-icon', { component: TrashOutline })
                    })
                ]
            });
        }
    }
];

const handleEdit = (company: any) => {
    console.log('编辑公司:', company);
};

const handleDelete = (company: any) => {
    try {
        console.log('删除公司:', company);
        window.$message?.success('公司删除成功');
    } catch (error) {
        console.error('删除公司失败:', error);
        window.$message?.error('删除公司失败');
    }
};

const handleSubmit = async () => {
    try {
        submitLoading.value = true;
        console.log('创建公司:', companyForm);
        window.$message?.success('公司创建成功');
        showCreateModal.value = false;
        resetForm();
    } catch (error: any) {
        console.error('创建公司失败:', error);
        window.$message?.error(error.message || '创建公司失败');
    } finally {
        submitLoading.value = false;
    }
};

const resetForm = () => {
    Object.assign(companyForm, {
        companyName: '',
        adminUserId: null
    });
};
</script>

<style scoped>
.company-management {
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
