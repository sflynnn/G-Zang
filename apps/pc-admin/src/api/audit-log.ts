import api from './index';

/** 审计日志查询参数 */
export interface AuditLogQueryParams {
  current?: number;
  size?: number;
  action?: string;
  targetType?: string;
  userId?: number;
}

/** 审计日志响应 */
export interface AuditLogResponse {
  id: number;
  userId: number;
  username?: string;
  module: string;
  action: string;
  targetType?: string;
  targetId?: number;
  description?: string;
  ip?: string;
  userAgent?: string;
  createTime: string;
}

/** 分页响应 */
export interface AuditLogPageResponse {
  records: AuditLogResponse[];
  total: number;
  size: number;
  current: number;
}

// 分页查询操作日志
export const getAuditLogs = (params?: AuditLogQueryParams): Promise<AuditLogPageResponse> => {
  return api.get<AuditLogPageResponse>('/admin/operation-logs', { params });
};

// 获取日志详情
export const getAuditLogDetail = (id: number) => {
  return api.get<AuditLogResponse>(`/admin/operation-logs/${id}`);
};
