import api from './index';

/** 角色列表查询参数 */
export interface RoleQueryParams {
  keyword?: string;
  roleType?: number;
  status?: number;
}

/** 创建角色请求 */
export interface CreateRoleRequest {
  roleName: string;
  roleCode: string;
  description?: string;
  dataScope: string;
  roleType: number;
  permissionIds?: number[];
}

/** 更新角色请求 */
export interface UpdateRoleRequest {
  roleName?: string;
  description?: string;
  dataScope?: string;
  permissionIds?: number[];
}

/** 角色响应 */
export interface RoleResponse {
  id: number;
  roleName: string;
  roleCode: string;
  description?: string;
  roleType: number;
  dataScope: string;
  isDefault: number;
  createTime: string;
  permissionIds?: number[];
  permissionCodes?: string[];
}

// 获取角色列表
export const getRoleList = (params?: RoleQueryParams): Promise<RoleResponse[]> => {
  return api.get<RoleResponse[]>('/admin/roles', { params });
};

// 获取角色详情（含权限）
export const getRoleDetail = (id: number) => {
  return api.get<RoleResponse>(`/admin/roles/${id}`);
};

// 创建角色
export const createRole = (data: CreateRoleRequest) => {
  return api.post('/admin/roles', data);
};

// 更新角色
export const updateRole = (id: number, data: UpdateRoleRequest) => {
  return api.put(`/admin/roles/${id}`, data);
};

// 删除角色
export const deleteRole = (id: number) => {
  return api.delete(`/admin/roles/${id}`);
};

// 获取角色权限ID列表
export const getRolePermissions = (id: number) => {
  return api.get<number[]>(`/admin/roles/${id}/permissions`);
};

// 更新角色权限
export const updateRolePermissions = (id: number, permissionIds: number[]) => {
  return api.put(`/admin/roles/${id}/permissions`, permissionIds);
};
