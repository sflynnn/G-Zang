<template>
  <div class="p-4 md:p-6 lg:p-8">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">{{ $t('approval.title') }}</h1>
    </div>

    <!-- 统计 -->
    <div class="grid grid-cols-3 gap-4 mb-6">
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <p class="text-xs text-text-secondary mb-1">待审批</p>
        <p class="text-3xl font-bold font-mono text-warning">{{ pendingCount }}</p>
      </div>
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <p class="text-xs text-text-secondary mb-1">本月已审批</p>
        <p class="text-3xl font-bold font-mono text-success">{{ approvedCount }}</p>
      </div>
      <div class="bg-surface dark:bg-dark-surface rounded-2xl p-5 shadow-gzang">
        <p class="text-xs text-text-secondary mb-1">本月驳回</p>
        <p class="text-3xl font-bold font-mono text-danger">{{ rejectedCount }}</p>
      </div>
    </div>

    <!-- 审批列表 -->
    <div class="bg-surface dark:bg-dark-surface rounded-2xl shadow-gzang overflow-hidden">
      <NTabs type="line" v-model:value="activeTab">
        <NTabPane name="pending" tab="待处理">
          <div v-for="item in pendingApprovals" :key="item.id" class="border-b border-border dark:border-dark-border p-4 hover:bg-gray-50 dark:hover:bg-dark-bg">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-warning/10 flex items-center justify-center">
                  <svg class="w-5 h-5 text-warning" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                </div>
                <div>
                  <p class="font-semibold text-text-primary dark:text-dark-text-primary">{{ item.title }}</p>
                  <p class="text-xs text-text-secondary">{{ item.applicant }} · {{ item.time }}</p>
                </div>
              </div>
              <div class="flex items-center gap-2">
                <button @click="handleApprove(item)" class="px-3 py-1.5 rounded-lg bg-success/10 text-success text-sm font-medium hover:bg-success/20">通过</button>
                <button @click="handleReject(item)" class="px-3 py-1.5 rounded-lg bg-danger/10 text-danger text-sm font-medium hover:bg-danger/20">驳回</button>
              </div>
            </div>
            <div class="mt-2 flex flex-wrap gap-2">
              <span v-for="tag in item.tags" :key="tag" class="text-xs px-2 py-0.5 rounded-full bg-gray-100 dark:bg-dark-bg text-text-secondary">{{ tag }}</span>
            </div>
          </div>
          <div v-if="pendingApprovals.length === 0" class="p-12 text-center text-text-secondary">暂无待审批项</div>
        </NTabPane>
        <NTabPane name="approved" tab="已通过">
          <div v-for="item in approvedApprovals" :key="item.id" class="border-b border-border dark:border-dark-border p-4">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-success/10 flex items-center justify-center">
                  <svg class="w-5 h-5 text-success" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>
                </div>
                <div>
                  <p class="font-semibold text-text-primary dark:text-dark-text-primary">{{ item.title }}</p>
                  <p class="text-xs text-text-secondary">{{ item.applicant }} · {{ item.time }} · {{ item.approver }}</p>
                </div>
              </div>
              <span class="text-xs px-2 py-0.5 rounded-full bg-success/10 text-success">已通过</span>
            </div>
          </div>
          <div v-if="approvedApprovals.length === 0" class="p-12 text-center text-text-secondary">暂无已通过记录</div>
        </NTabPane>
        <NTabPane name="rejected" tab="已驳回">
          <div v-for="item in rejectedApprovals" :key="item.id" class="border-b border-border dark:border-dark-border p-4">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-danger/10 flex items-center justify-center">
                  <svg class="w-5 h-5 text-danger" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
                </div>
                <div>
                  <p class="font-semibold text-text-primary dark:text-dark-text-primary">{{ item.title }}</p>
                  <p class="text-xs text-text-secondary">{{ item.applicant }} · {{ item.time }} · {{ item.reason }}</p>
                </div>
              </div>
              <span class="text-xs px-2 py-0.5 rounded-full bg-danger/10 text-danger">已驳回</span>
            </div>
          </div>
          <div v-if="rejectedApprovals.length === 0" class="p-12 text-center text-text-secondary">暂无已驳回记录</div>
        </NTabPane>
      </NTabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { NTabs, NTabPane, useMessage } from 'naive-ui';

const message = useMessage();
const activeTab = ref('pending');

const pendingApprovals = ref([
  { id: 1, title: '采购申请 - 办公设备采购', applicant: '张三', time: '2026-05-22 09:30', tags: ['采购', 'IT设备', '¥5,000'] },
  { id: 2, title: '差旅报销 - 北京出差', applicant: '李四', time: '2026-05-21 15:20', tags: ['差旅', '交通', '¥3,200'] },
  { id: 3, title: '项目经费申请 - Q2研发', applicant: '王五', time: '2026-05-20 11:00', tags: ['项目', '研发', '¥20,000'] },
]);

const approvedApprovals = ref([
  { id: 4, title: '团建活动经费', applicant: '赵六', time: '2026-05-19', approver: '张经理' },
  { id: 5, title: '培训费用报销', applicant: '孙七', time: '2026-05-18', approver: '张经理' },
]);

const rejectedApprovals = ref([
  { id: 6, title: '礼品采购申请', applicant: '周八', time: '2026-05-17', reason: '预算不足' },
]);

const pendingCount = ref(3);
const approvedCount = ref(12);
const rejectedCount = ref(2);

const handleApprove = (item: any) => {
  pendingApprovals.value = pendingApprovals.value.filter(i => i.id !== item.id);
  approvedApprovals.value.unshift({ ...item, approver: '当前用户', time: new Date().toLocaleString('zh-CN') });
  pendingCount.value--;
  approvedCount.value++;
  message.success('审批已通过');
};

const handleReject = (item: any) => {
  pendingApprovals.value = pendingApprovals.value.filter(i => i.id !== item.id);
  rejectedApprovals.value.unshift({ ...item, reason: '审批驳回' });
  pendingCount.value--;
  rejectedCount.value++;
  message.info('已驳回');
};
</script>
