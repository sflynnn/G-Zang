import api from './index';

/** 权限响应 */
export interface PermissionResponse {
  id: number;
  permissionName: string;
  permissionCode: string;
  permissionGroup: string;
  permissionModule: string;
  permissionLevel: number;
  sortOrder: number;
  description?: string;
  createTime: string;
}

/** 分组后的权限 Map */
export type GroupedPermissions = Record<string, PermissionResponse[]>;

// 获取所有权限（分组）
export const getAllPermissions = (): Promise<GroupedPermissions> => {
  return api.get<GroupedPermissions>('/api/v1/permissions');
};

// 获取权限模块分组
export const getPermissionModules = () => {
  return api.get<GroupedPermissions>('/api/v1/permissions/modules');
};
