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

/**
 * 按权限组对权限进行分组
 */
function groupPermissions(permissions: PermissionResponse[]): GroupedPermissions {
  return permissions.reduce((acc, perm) => {
    const group = perm.permissionGroup || 'OTHER';
    if (!acc[group]) {
      acc[group] = [];
    }
    acc[group].push(perm);
    return acc;
  }, {} as GroupedPermissions);
}

// 获取所有权限（后端返回扁平数组，前端分组）
export const getAllPermissions = (): Promise<GroupedPermissions> => {
  return api.get<PermissionResponse[]>('/admin/permissions').then((response: any) => {
    // 处理后端返回的扁平数组
    const permissions = response?.data ?? response;
    if (Array.isArray(permissions)) {
      return groupPermissions(permissions);
    }
    return permissions;
  });
};

// 获取权限模块分组
export const getPermissionModules = () => {
  return api.get<GroupedPermissions>('/admin/permissions/modules');
};
