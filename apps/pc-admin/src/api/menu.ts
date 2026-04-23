import api from './index';

/** 权限响应 */
interface PermissionResponse {
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

/** 菜单响应 */
export interface MenuResponse {
  id: number;
  menuName: string;
  menuCode: string;
  menuType: number;
  parentId: number;
  parentName?: string;
  icon?: string;
  path?: string;
  component?: string;
  redirect?: string;
  sortOrder: number;
  visible: number;
  cache: number;
  affix: number;
  keepAlive: number;
  permissionCode?: string;
  menuLevel: number;
  companyId?: number;
  description?: string;
  isSystem: number;
  createTime: string;
  updateTime: string;
  permissionIds?: number[];
  permissions?: PermissionResponse[];
  children?: MenuResponse[];
}

/** 创建菜单请求 */
export interface CreateMenuRequest {
  menuName: string;
  menuCode: string;
  menuType: number;
  parentId?: number;
  icon?: string;
  path?: string;
  component?: string;
  redirect?: string;
  sortOrder?: number;
  visible?: number;
  cache?: number;
  affix?: number;
  keepAlive?: number;
  permissionCode?: string;
  menuLevel: number;
  companyId?: number;
  description?: string;
  permissionIds?: number[];
}

/** 更新菜单请求 */
export interface UpdateMenuRequest {
  menuName?: string;
  menuType?: number;
  parentId?: number;
  icon?: string;
  path?: string;
  component?: string;
  redirect?: string;
  sortOrder?: number;
  visible?: number;
  cache?: number;
  affix?: number;
  keepAlive?: number;
  permissionCode?: string;
  menuLevel?: number;
  companyId?: number;
  description?: string;
  permissionIds?: number[];
}

/** 菜单树查询参数 */
export interface MenuTreeParams {
  keyword?: string;
  menuLevel?: number;
}

/** 获取菜单列表（树形） */
export const getMenuTree = (params?: MenuTreeParams): Promise<MenuResponse[]> => {
  return api.get<MenuResponse[]>('/admin/menus', { params });
};

/** 获取菜单详情（含权限） */
export const getMenuDetail = (id: number): Promise<MenuResponse> => {
  return api.get<MenuResponse>(`/admin/menus/${id}`);
};

/** 创建菜单 */
export const createMenu = (data: CreateMenuRequest): Promise<void> => {
  return api.post('/admin/menus', data);
};

/** 更新菜单 */
export const updateMenu = (id: number, data: UpdateMenuRequest): Promise<void> => {
  return api.put(`/admin/menus/${id}`, data);
};

/** 删除菜单 */
export const deleteMenu = (id: number): Promise<void> => {
  return api.delete(`/admin/menus/${id}`);
};

/** 获取菜单树（用于选择器） */
export const getMenuTreeOptions = (): Promise<MenuResponse[]> => {
  return api.get<MenuResponse[]>('/admin/menus/tree');
};

/** 获取菜单关联的权限列表 */
export const getMenuPermissions = (menuId: number): Promise<number[]> => {
  return api.get<number[]>(`/admin/menus/${menuId}/permissions`);
};

/** 更新菜单权限 */
export const updateMenuPermissions = (menuId: number, permissionIds: number[]): Promise<void> => {
  return api.put(`/admin/menus/${menuId}/permissions`, permissionIds);
};

/** 获取角色菜单权限 */
export const getRoleMenus = (roleId: number): Promise<MenuResponse[]> => {
  return api.get<MenuResponse[]>(`/admin/menus/role/${roleId}`);
};

/** 获取当前用户菜单树 */
export const getUserMenus = (): Promise<MenuResponse[]> => {
  return api.get<MenuResponse[]>('/admin/menus/user/tree');
};
