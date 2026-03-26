import { useAppStore } from '@/stores/app';

/**
 * 权限验证工具类
 * 提供权限检查和角色验证功能
 */

export interface UserPermission {
  id: number;
  permissionName: string;
  permissionCode: string;
}

/**
 * 系统权限编码常量
 */
export const PERMISSIONS = {
  // 用户管理
  USER_MANAGE: 'USER_MANAGE',
  USER_ADD: 'USER_ADD',
  USER_EDIT: 'USER_EDIT',
  USER_DELETE: 'USER_DELETE',
  USER_VIEW: 'USER_VIEW',

  // 角色管理
  ROLE_MANAGE: 'ROLE_MANAGE',
  ROLE_ADD: 'ROLE_ADD',
  ROLE_EDIT: 'ROLE_EDIT',
  ROLE_DELETE: 'ROLE_DELETE',
  ROLE_VIEW: 'ROLE_VIEW',

  // 权限管理
  PERMISSION_MANAGE: 'PERMISSION_MANAGE',
  PERMISSION_ADD: 'PERMISSION_ADD',
  PERMISSION_EDIT: 'PERMISSION_EDIT',
  PERMISSION_DELETE: 'PERMISSION_DELETE',
  PERMISSION_VIEW: 'PERMISSION_VIEW',

  // 公司管理
  COMPANY_MANAGE: 'COMPANY_MANAGE',
  COMPANY_ADD: 'COMPANY_ADD',
  COMPANY_EDIT: 'COMPANY_EDIT',
  COMPANY_DELETE: 'COMPANY_DELETE',
  COMPANY_VIEW: 'COMPANY_VIEW',

  // 系统设置
  SYSTEM_CONFIG: 'SYSTEM_CONFIG',
  LOG_VIEW: 'LOG_VIEW',

  // 财务相关
  TRANSACTION_ADD: 'TRANSACTION_ADD',
  TRANSACTION_EDIT: 'TRANSACTION_EDIT',
  TRANSACTION_DELETE: 'TRANSACTION_DELETE',
  TRANSACTION_VIEW: 'TRANSACTION_VIEW',
  ACCOUNT_MANAGE: 'ACCOUNT_MANAGE',
  CATEGORY_MANAGE: 'CATEGORY_MANAGE',
  REPORT_VIEW: 'REPORT_VIEW'
} as const;

/**
 * 系统角色编码常量
 */
export const ROLES = {
  SUPER_ADMIN: 'SUPER_ADMIN',
  ADMIN: 'ADMIN',
  FINANCE: 'FINANCE',
  USER: 'USER'
} as const;

/**
 * 检查用户是否有指定权限
 * @param permission 权限编码，如 'USER_MANAGE'
 * @returns boolean
 */
export function hasPermission(permission: string): boolean {
  const appStore = useAppStore();
  const user = appStore.user;

  if (!user || !user.permissions) {
    return false;
  }

  // 超级管理员拥有所有权限
  if (user.roleCode === ROLES.SUPER_ADMIN) {
    return true;
  }

  return user.permissions.includes(permission);
}

/**
 * 检查用户是否有任意一个权限
 * @param permissions 权限编码数组
 * @returns boolean
 */
export function hasAnyPermission(permissions: string[]): boolean {
  return permissions.some(permission => hasPermission(permission));
}

/**
 * 检查用户是否有指定角色
 * @param role 角色编码，如 'ADMIN'
 * @returns boolean
 */
export function hasRole(role: string): boolean {
  const appStore = useAppStore();
  const user = appStore.user;

  if (!user || !user.roleCode) {
    return false;
  }

  // 超级管理员拥有所有角色权限
  if (user.roleCode === 'SUPER_ADMIN') {
    return true;
  }

  return user.roleCode === role;
}

/**
 * 检查用户是否有任意一个角色
 * @param roles 角色编码数组
 * @returns boolean
 */
export function hasAnyRole(roles: string[]): boolean {
  return roles.some(role => hasRole(role));
}

/**
 * 获取用户的权限列表
 * @returns string[]
 */
export function getUserPermissions(): string[] {
  const appStore = useAppStore();
  const user = appStore.user;

  return user?.permissions || [];
}

/**
 * 获取用户的角色编码
 * @returns string | null
 */
export function getUserRole(): string | null {
  const appStore = useAppStore();
  const user = appStore.user;

  return user?.roleCode || null;
}

/**
 * 权限验证结果类型
 */
export interface PermissionCheckResult {
  hasPermission: boolean;
  requiredPermission?: string;
  message?: string;
}

/**
 * 验证权限并返回详细信息
 * @param permission 权限编码
 * @returns PermissionCheckResult
 */
export function checkPermission(permission: string): PermissionCheckResult {
  const hasPerm = hasPermission(permission);

  return {
    hasPermission: hasPerm,
    requiredPermission: permission,
    message: hasPerm ? undefined : `需要 ${permission} 权限`
  };
}

/**
 * 检查多个权限中的任意一个
 * @param permissions 权限编码数组
 * @returns PermissionCheckResult
 */
export function checkAnyPermission(permissions: string[]): PermissionCheckResult {
  const hasPerm = hasAnyPermission(permissions);

  return {
    hasPermission: hasPerm,
    requiredPermission: permissions.join(' 或 '),
    message: hasPerm ? undefined : `需要以下权限之一: ${permissions.join(', ')}`
  };
}
