import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getAuditLogs, type AuditLogResponse, type AuditLogQueryParams } from '@/api/audit-log';

export const useAuditLogStore = defineStore('auditLog', () => {
  const logs = ref<AuditLogResponse[]>([]);
  const total = ref(0);
  const loading = ref(false);

  const fetchLogs = async (params: AuditLogQueryParams = {}) => {
    loading.value = true;
    try {
      const result = await getAuditLogs(params);
      logs.value = result.records || [];
      total.value = result.total || 0;
    } catch (error) {
      console.error('获取审计日志失败:', error);
    } finally {
      loading.value = false;
    }
  };

  return { logs, total, loading, fetchLogs };
});
