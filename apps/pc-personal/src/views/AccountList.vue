<template>
  <div class="account-list min-h-screen p-4 md:p-6 lg:p-8">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">{{ $t('account.listTitle') }}</h1>
        <p class="text-sm text-text-secondary dark:text-dark-text-secondary mt-1">{{ $t('account.listSubtitle') }}</p>
      </div>
      <button @click="showAddModal = true" class="btn-primary flex items-center gap-2">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        {{ $t('account.add') }}
      </button>
    </div>

    <!-- 总资产卡片 -->
    <div class="bg-gradient-to-br from-primary via-primary to-primary-light rounded-2xl p-6 mb-6 text-white">
      <p class="text-white/70 text-sm mb-1">{{ $t('account.totalBalance') }}</p>
      <p class="text-4xl font-bold font-mono">{{ formatCurrency(totalBalance) }}</p>
    </div>

    <!-- 账户列表 -->
    <div class="space-y-4">
      <div
        v-for="account in accounts"
        :key="account.id"
        class="bg-surface dark:bg-dark-surface rounded-2xl p-4 shadow-gzang hover:shadow-lg transition-all cursor-pointer"
        @click="handleEdit(account)"
      >
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div
              class="w-12 h-12 rounded-xl flex items-center justify-center text-2xl"
              :style="{ backgroundColor: getAccountColor(account.accountType) + '15' }"
            >
              {{ getAccountIcon(account.accountType) }}
            </div>
            <div>
              <p class="font-semibold text-text-primary dark:text-dark-text-primary">{{ account.accountName }}</p>
              <p class="text-xs text-text-secondary dark:text-dark-text-secondary">{{ getAccountTypeName(account.accountType) }}</p>
            </div>
          </div>
          <div class="text-right">
            <p class="font-mono font-bold text-lg" :class="account.balance >= 0 ? 'text-success' : 'text-danger'">
              {{ formatCurrency(account.balance) }}
            </p>
            <p class="text-xs text-text-secondary">{{ $t('account.balance') }}</p>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="accounts.length === 0 && !loading" class="text-center py-16">
        <div class="w-16 h-16 mx-auto mb-4 rounded-2xl bg-gray-100 dark:bg-dark-bg flex items-center justify-center">
          <svg class="w-8 h-8 text-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
          </svg>
        </div>
        <p class="text-text-secondary dark:text-dark-text-secondary mb-4">{{ $t('account.empty') }}</p>
        <button @click="showAddModal = true" class="btn-primary">{{ $t('account.addFirst') }}</button>
      </div>
    </div>

    <!-- 添加/编辑弹窗 -->
    <NModal v-model:show="showAddModal" preset="card" :title="editingAccount ? $t('account.edit') : $t('account.add')" style="max-width: 480px; margin: 0 auto;">
      <NForm ref="formRef" :model="formData" :rules="rules" label-placement="top">
        <NFormItem :label="$t('account.accountName')" path="accountName">
          <NInput v-model:value="formData.accountName" :placeholder="$t('account.namePlaceholder')" />
        </NFormItem>
        <NFormItem :label="$t('account.accountType')" path="accountType">
          <NSelect v-model:value="formData.accountType" :options="typeOptions" />
        </NFormItem>
        <NFormItem :label="$t('account.initialBalance')" path="balance">
          <NInputNumber v-model:value="formData.balance" :min="0" :precision="2" class="!w-full" />
        </NFormItem>
      </NForm>
      <template #footer>
        <div class="flex gap-3">
          <button v-if="editingAccount" @click="handleDelete" class="flex-1 py-2 rounded-xl border-2 border-danger text-danger font-medium hover:bg-danger/5">
            {{ $t('account.delete') }}
          </button>
          <button @click="showAddModal = false" class="flex-1 py-2 rounded-xl border border-border dark:border-dark-border text-text-secondary">
            {{ $t('account.cancel') }}
          </button>
          <button @click="handleSave" class="flex-1 py-2 rounded-xl bg-secondary text-white font-medium hover:bg-secondary-dark">
            {{ $t('account.save') }}
          </button>
        </div>
      </template>
    </NModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { NModal, NForm, NFormItem, NInput, NInputNumber, NSelect, useMessage } from 'naive-ui';
import { accountingApi } from '@gzang/shared';
import { usePersonalStore } from '@/stores/personal';
import type { FormRules } from 'naive-ui';
import type { Account } from '@gzang/shared';

const personalStore = usePersonalStore();
const message = useMessage();

const loading = ref(false);
const showAddModal = ref(false);
const editingAccount = ref<Account | null>(null);

const formData = ref({ accountName: '', accountType: 1 as number, balance: 0 as number });

const typeOptions = [
  { label: '现金', value: 1 },
  { label: '银行卡', value: 2 },
  { label: '电子支付', value: 3 },
];

const rules: FormRules = {
  accountName: { required: true, message: '账户名称不能为空' },
  accountType: { required: true, type: 'number', message: '请选择账户类型' },
};

const accounts = computed(() => personalStore.accounts);
const totalBalance = computed(() => accounts.value.reduce((sum, a) => sum + a.balance, 0));

const formatCurrency = (v: number) => v.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

const getAccountIcon = (type: number) => ({ 1: '💵', 2: '💳', 3: '📱' }[type] || '💰');
const getAccountColor = (type: number) => ({ 1: '#06D6A0', 2: '#118AB2', 3: '#FB8B24' }[type] || '#0F4C5C');
const getAccountTypeName = (type: number) => ({ 1: '现金', 2: '银行卡', 3: '电子支付' }[type] || '其他');

const handleEdit = (account: Account) => {
  editingAccount.value = account;
  formData.value = { accountName: account.accountName, accountType: account.accountType, balance: account.balance };
  showAddModal.value = true;
};

const handleSave = async () => {
  try {
    if (editingAccount.value) {
      await accountingApi.updateAccount(editingAccount.value.id, { accountName: formData.value.accountName, accountType: formData.value.accountType });
      personalStore.updateAccount({ ...editingAccount.value, ...formData.value });
      message.success('更新成功');
    } else {
      await accountingApi.createAccount({ accountName: formData.value.accountName, accountType: formData.value.accountType, balance: formData.value.balance, userId: 0 });
      loadAccounts();
      message.success('创建成功');
    }
    showAddModal.value = false;
    editingAccount.value = null;
    formData.value = { accountName: '', accountType: 1, balance: 0 };
  } catch (error: any) {
    message.error(error.message || '保存失败');
  }
};

const handleDelete = async () => {
  if (!editingAccount.value) return;
  try {
    await accountingApi.deleteAccount(editingAccount.value.id);
    personalStore.removeAccount(editingAccount.value.id);
    message.success('删除成功');
    showAddModal.value = false;
    editingAccount.value = null;
  } catch (error: any) {
    message.error(error.message || '删除失败');
  }
};

const loadAccounts = async () => {
  try {
    const res = await accountingApi.getAccounts();
    if (res.data) personalStore.setAccounts(res.data);
  } catch (error) { console.error('加载账户失败:', error); }
};

onMounted(loadAccounts);
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center justify-center gap-2 px-4 py-2 bg-secondary text-white rounded-xl font-medium text-sm transition-all hover:bg-secondary-dark active:scale-95; }
.text-success { @apply text-[#06D6A0]; }
.text-danger { @apply text-[#EF476F]; }
.bg-secondary { @apply bg-[#FB8B24]; }
.hover-bg-secondary-dark:hover { @apply bg-[#e67a1a]; }
</style>
