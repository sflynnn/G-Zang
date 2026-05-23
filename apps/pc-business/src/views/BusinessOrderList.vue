<template>
  <div class="p-4 md:p-6 lg:p-8">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">{{ $t('order.title') }}</h1>
      </div>
      <button @click="showAddModal = true" class="btn-primary flex items-center gap-2">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" /></svg>
        新建订单
      </button>
    </div>

    <!-- 筛选工具栏 -->
    <div class="bg-surface dark:bg-dark-surface rounded-2xl p-4 mb-6 shadow-gzang">
      <div class="flex flex-wrap items-center gap-3">
        <NSelect v-model="filterStatus" :options="statusOptions" placeholder="订单状态" clearable class="!w-36" />
        <NInput v-model="keyword" placeholder="搜索客户名称或订单号" clearable class="!w-64">
          <template #prefix><svg class="w-4 h-4 text-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg></template>
        </NInput>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="bg-surface dark:bg-dark-surface rounded-2xl shadow-gzang overflow-hidden">
      <NDataTable :columns="columns" :data="filteredOrders" :pagination="false" :row-key="(row: any) => row.id" />
    </div>

    <!-- 新建订单弹窗 -->
    <NModal v-model:show="showAddModal" preset="card" title="新建业务订单" style="max-width: 600px; margin: 0 auto;">
      <NForm ref="formRef" :model="formData" label-placement="top">
        <NFormItem label="客户名称" path="customer">
          <NInput v-model:value="formData.customer" placeholder="请输入客户名称" />
        </NFormItem>
        <NFormItem label="订单金额" path="amount">
          <NInputNumber v-model:value="formData.amount" :min="0" :precision="2" class="!w-full" />
        </NFormItem>
        <NFormItem label="订单描述" path="description">
          <NInput v-model:value="formData.description" type="textarea" :rows="3" placeholder="请输入订单描述" />
        </NFormItem>
      </NForm>
      <template #footer>
        <div class="flex gap-3 justify-end">
          <button @click="showAddModal = false" class="px-4 py-2 rounded-xl border border-border dark:border-dark-border text-text-secondary">取消</button>
          <button @click="handleCreate" class="px-4 py-2 rounded-xl bg-secondary text-white font-medium hover:bg-secondary-dark">创建</button>
        </div>
      </template>
    </NModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { NSelect, NInput, NInputNumber, NDataTable, NModal, NForm, NFormItem, useMessage } from 'naive-ui';

const message = useMessage();
const filterStatus = ref<string>('');
const keyword = ref('');
const showAddModal = ref(false);
const formRef = ref<any>(null);
const formData = ref({ customer: '', amount: 0, description: '' });

const orders = ref<any[]>([
  { id: 1, orderNo: 'BO202605001', customer: '北京科技有限公司', amount: 28000, status: 'pending', createTime: '2026-05-22 10:30', description: 'IT运维服务' },
  { id: 2, orderNo: 'BO202605002', customer: '上海贸易公司', amount: 15600, status: 'approved', createTime: '2026-05-21 14:20', description: '软件定制开发' },
  { id: 3, orderNo: 'BO202605003', customer: '广州实业集团', amount: 42000, status: 'inProgress', createTime: '2026-05-20 09:15', description: '系统集成项目' },
]);

const statusOptions: Array<{ label: string; value: string }> = [
  { label: '全部', value: '' },
  { label: '待审批', value: 'pending' },
  { label: '已通过', value: 'approved' },
  { label: '进行中', value: 'inProgress' },
  { label: '已完成', value: 'completed' },
  { label: '已驳回', value: 'rejected' },
];

const columns = [
  { title: '订单号', key: 'orderNo', width: 140 },
  { title: '客户名称', key: 'customer' },
  { title: '金额', key: 'amount', render: (row: any) => `¥${row.amount.toLocaleString()}` },
  { title: '状态', key: 'status', render: (row: any) => statusOptions.find(s => s.value === row.status)?.label || row.status },
  { title: '创建时间', key: 'createTime' },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    render: (row: any) => {
      const buttons = [];
      if (row.status === 'pending') {
        buttons.push({ label: '审批', action: 'approve', class: 'text-success hover:bg-success/10' });
      }
      buttons.push({ label: '详情', action: 'detail', class: 'text-info hover:bg-info/10' });
      return buttons.map(btn =>
        `<button class="px-2 py-1 rounded text-xs font-medium ${btn.class}" data-action="${btn.action}" data-id="${row.id}">${btn.label}</button>`
      ).join(' ');
    }
  }
];

const filteredOrders = computed(() => {
  let result = [...orders.value];
  if (filterStatus.value) result = result.filter(o => o.status === filterStatus.value);
  if (keyword.value) {
    const kw = keyword.value.toLowerCase();
    result = result.filter(o => o.customer.toLowerCase().includes(kw) || o.orderNo.toLowerCase().includes(kw));
  }
  return result;
});

const handleCreate = () => {
  const newOrder = {
    id: Date.now(),
    orderNo: `BO${new Date().toISOString().replace(/[-T:]/g, '').slice(2, 10)}`,
    customer: formData.value.customer,
    amount: formData.value.amount,
    status: 'pending',
    createTime: new Date().toLocaleString('zh-CN'),
    description: formData.value.description,
  };
  orders.value.unshift(newOrder);
  showAddModal.value = false;
  formData.value = { customer: '', amount: 0, description: '' };
  message.success('订单创建成功');
};
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center justify-center gap-2 px-4 py-2 bg-secondary text-white rounded-xl font-medium text-sm transition-all hover:bg-secondary-dark active:scale-95; }
.text-success { @apply text-[#06D6A0]; }
.text-danger { @apply text-[#EF476F]; }
.text-info { @apply text-[#118AB2]; }
</style>
