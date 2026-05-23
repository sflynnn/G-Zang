<template>
  <div class="transaction-form min-h-screen p-4 md:p-6 lg:p-8">
    <!-- 页面标题 -->
    <div class="flex items-center gap-4 mb-6">
      <button
        @click="goBack"
        class="w-10 h-10 rounded-xl bg-surface dark:bg-dark-surface shadow-gzang flex items-center justify-center hover:bg-gray-50 dark:hover:bg-dark-bg transition-colors"
      >
        <svg class="w-5 h-5 text-text-primary dark:text-dark-text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </button>
      <div>
        <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">
          {{ isEdit ? $t('transactionForm.editTitle') : $t('transactionForm.addTitle') }}
        </h1>
        <p class="text-sm text-text-secondary dark:text-dark-text-secondary mt-1">
          {{ isEdit ? $t('transactionForm.editSubtitle') : $t('transactionForm.addSubtitle') }}
        </p>
      </div>
    </div>

    <!-- 表单卡片 -->
    <div class="max-w-2xl mx-auto">
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-6 shadow-gzang">
        <NForm
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-placement="top"
          @submit.prevent="handleSubmit"
        >
          <!-- 金额输入 -->
          <NFormItem :label="$t('transactionForm.amount')" path="amount">
            <div class="w-full">
              <NInputNumber
                v-model:value="formData.amount"
                :min="0.01"
                :precision="2"
                :placeholder="$t('transactionForm.amountPlaceholder')"
                size="large"
                class="!w-full"
              >
                <template #prefix>
                  <span class="text-2xl font-bold text-secondary mr-1">¥</span>
                </template>
              </NInputNumber>
            </div>
          </NFormItem>

          <!-- 交易类型 -->
          <NFormItem :label="$t('transactionForm.type')" path="type">
            <div class="flex gap-4">
              <button
                v-for="t in typeOptions"
                :key="t.value"
                @click="formData.type = t.value"
                class="flex-1 py-4 rounded-xl border-2 transition-all duration-200 flex flex-col items-center gap-2"
                :class="formData.type === t.value
                  ? t.value === 1
                    ? 'border-success bg-success/5 text-success'
                    : 'border-danger bg-danger/5 text-danger'
                  : 'border-border dark:border-dark-border hover:border-gray-300 dark:hover:border-dark-text-secondary text-text-secondary dark:text-dark-text-secondary'"
              >
                <div
                  class="w-12 h-12 rounded-xl flex items-center justify-center"
                  :class="formData.type === t.value
                    ? t.value === 1 ? 'bg-success/20' : 'bg-danger/20'
                    : 'bg-gray-100 dark:bg-dark-bg'"
                >
                  <svg v-if="t.value === 1" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 11l5-5m0 0l5 5m-5-5v12" />
                  </svg>
                  <svg v-else class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 13l-5 5m0 0l-5-5m5 5V6" />
                  </svg>
                </div>
                <span class="font-medium">{{ t.label }}</span>
              </button>
            </div>
          </NFormItem>

          <!-- 分类选择 -->
          <NFormItem :label="$t('transactionForm.category')" path="categoryId">
            <NSelect
              v-model:value="formData.categoryId"
              :options="categoryOptions"
              :placeholder="$t('transactionForm.categoryPlaceholder')"
              filterable
              class="!w-full"
            />
          </NFormItem>

          <!-- 账户选择 -->
          <NFormItem :label="$t('transactionForm.account')" path="accountId">
            <NSelect
              v-model:value="formData.accountId"
              :options="accountOptions"
              :placeholder="$t('transactionForm.accountPlaceholder')"
              filterable
              class="!w-full"
            />
          </NFormItem>

          <!-- 交易时间 -->
          <NFormItem :label="$t('transactionForm.transactionTime')" path="transactionTime">
            <NDatePicker
              v-model:value="formData.transactionTime"
              type="datetime"
              :placeholder="$t('transactionForm.timePlaceholder')"
              class="!w-full"
            />
          </NFormItem>

          <!-- 备注 -->
          <NFormItem :label="$t('transactionForm.remark')" path="remark">
            <NInput
              v-model:value="formData.remark"
              type="textarea"
              :rows="3"
              :placeholder="$t('transactionForm.remarkPlaceholder')"
              maxlength="500"
              show-count
            />
          </NFormItem>

          <!-- 操作按钮 -->
          <div class="flex gap-4 mt-6">
            <button
              v-if="isEdit"
              type="button"
              @click="handleDelete"
              class="flex-1 py-3 rounded-xl border-2 border-danger text-danger font-medium transition-all hover:bg-danger/5"
            >
              {{ $t('transactionForm.delete') }}
            </button>
            <NButton
              type="primary"
              attr-type="submit"
              :loading="submitting"
              size="large"
              class="flex-1"
              :style="{ backgroundColor: '#FB8B24', borderColor: '#FB8B24' }"
            >
              {{ $t('transactionForm.submit') }}
            </NButton>
          </div>
        </NForm>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { NForm, NFormItem, NInputNumber, NSelect, NDatePicker, NInput, NButton, useMessage } from 'naive-ui';
import { accountingApi } from '@gzang/shared';
import { usePersonalStore } from '@/stores/personal';
import type { FormRules } from 'naive-ui';

const { t } = useI18n();
const router = useRouter();
const route = useRoute();
const personalStore = usePersonalStore();
const message = useMessage();

const formRef = ref<any>(null);
const submitting = ref(false);

const isEdit = computed(() => !!route.params.id);
const transactionId = computed(() => route.params.id ? Number(route.params.id) : null);

const formData = ref({
  amount: null as number | null,
  type: 2,
  categoryId: null as number | null,
  accountId: null as number | null,
  transactionTime: Date.now() as number,
  remark: '',
});

const typeOptions = computed(() => [
  { label: t('transactionForm.income'), value: 1 },
  { label: t('transactionForm.expense'), value: 2 },
]);

const categoryOptions = computed(() =>
  personalStore.categories
    .filter(c => c.type === formData.value.type)
    .map(c => ({ label: c.categoryName, value: c.id }))
);

const accountOptions = computed(() =>
  personalStore.accounts.map(a => ({ label: a.accountName, value: a.id }))
);

const rules: FormRules = {
  amount: { required: true, type: 'number', min: 0.01, message: t('transactionForm.amountRequired') },
  type: { required: true, type: 'number', message: t('transactionForm.typeRequired') },
  categoryId: { required: true, type: 'number', message: t('transactionForm.categoryRequired') },
  accountId: { required: true, type: 'number', message: t('transactionForm.accountRequired') },
  transactionTime: { required: true, type: 'number', message: t('transactionForm.timeRequired') },
};

const loadTransaction = async () => {
  if (!transactionId.value) return;
  try {
    const res = await accountingApi.getTransaction(transactionId.value);
    if (res.data) {
      const tx = res.data;
      formData.value = {
        amount: tx.amount,
        type: tx.type,
        categoryId: tx.categoryId,
        accountId: tx.accountId,
        transactionTime: new Date(tx.transactionTime).getTime(),
        remark: tx.remark || '',
      };
    }
  } catch (error: any) {
    message.error(error.message || t('transactionForm.loadFailed'));
  }
};

const loadCategories = async () => {
  if (personalStore.categories.length > 0) return;
  try {
    const res = await accountingApi.getCategories();
    if (res.data) personalStore.setCategories(res.data);
  } catch (error) { console.error('加载分类失败:', error); }
};

const loadAccounts = async () => {
  if (personalStore.accounts.length > 0) return;
  try {
    const res = await accountingApi.getAccounts();
    if (res.data) personalStore.setAccounts(res.data);
  } catch (error) { console.error('加载账户失败:', error); }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  formRef.value.validate(async (errors: any) => {
    if (errors) return;
    submitting.value = true;
    try {
      const payload = {
        amount: formData.value.amount,
        type: formData.value.type,
        categoryId: formData.value.categoryId,
        accountId: formData.value.accountId,
        transactionTime: new Date(formData.value.transactionTime).toISOString(),
        remark: formData.value.remark,
      };
      if (isEdit.value) {
        await accountingApi.updateTransaction(transactionId.value!, payload);
        message.success(t('transactionForm.updateSuccess'));
      } else {
        await accountingApi.createTransaction(payload);
        message.success(t('transactionForm.createSuccess'));
      }
      router.push('/transactions');
    } catch (error: any) {
      message.error(error.message || t('transactionForm.submitFailed'));
    } finally {
      submitting.value = false;
    }
  });
};

const handleDelete = async () => {
  if (!transactionId.value) return;
  try {
    await accountingApi.deleteTransaction(transactionId.value);
    message.success(t('transactionForm.deleteSuccess'));
    router.push('/transactions');
  } catch (error: any) {
    message.error(error.message || t('transactionForm.deleteFailed'));
  }
};

const goBack = () => router.back();

onMounted(async () => {
  await Promise.all([loadCategories(), loadAccounts()]);
  if (isEdit.value) await loadTransaction();
});
</script>

<style scoped>
.text-success { color: #06D6A0; }
.text-danger { color: #EF476F; }
.bg-success\/5 { background-color: rgba(6, 214, 160, 0.05); }
.bg-danger\/5 { background-color: rgba(239, 71, 111, 0.05); }
.bg-success\/20 { background-color: rgba(6, 214, 160, 0.2); }
.bg-danger\/20 { background-color: rgba(239, 71, 111, 0.2); }
.border-success { border-color: #06D6A0; }
.border-danger { border-color: #EF476F; }
</style>
