import { Directive, DirectiveBinding } from 'vue';
import { hasPermission, hasRole, hasAnyPermission, hasAnyRole } from '@/utils/permission';

/**
 * 权限指令
 * 支持权限和角色验证
 *
 * 用法：
 * v-permission="'USER_MANAGE'" - 检查单个权限
 * v-permission="['USER_MANAGE', 'ROLE_MANAGE']" - 检查多个权限中的任意一个
 * v-role="'ADMIN'" - 检查单个角色
 * v-role="['ADMIN', 'SUPER_ADMIN']" - 检查多个角色中的任意一个
 */

export const vPermission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value } = binding;

    if (!value) {
      console.warn('v-permission: 权限值不能为空');
      return;
    }

    let hasAccess = false;

    if (Array.isArray(value)) {
      // 数组形式：检查多个权限中的任意一个
      hasAccess = hasAnyPermission(value);
    } else if (typeof value === 'string') {
      // 字符串形式：检查单个权限
      hasAccess = hasPermission(value);
    }

    if (!hasAccess) {
      // 移除元素
      el.parentNode?.removeChild(el);
    }
  }
};

export const vRole: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value } = binding;

    if (!value) {
      console.warn('v-role: 角色值不能为空');
      return;
    }

    let hasAccess = false;

    if (Array.isArray(value)) {
      // 数组形式：检查多个角色中的任意一个
      hasAccess = hasAnyRole(value);
    } else if (typeof value === 'string') {
      // 字符串形式：检查单个角色
      hasAccess = hasRole(value);
    }

    if (!hasAccess) {
      // 移除元素
      el.parentNode?.removeChild(el);
    }
  }
};
