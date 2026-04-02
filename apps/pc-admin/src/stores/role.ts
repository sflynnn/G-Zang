import { defineStore } from 'pinia';
import { ref } from 'vue';
import {
  getRoleList,
  getRoleDetail,
  createRole,
  updateRole,
  deleteRole,
  getRolePermissions,
  updateRolePermissions,
  type RoleResponse,
  type CreateRoleRequest,
  type UpdateRoleRequest
} from '@/api/role';

export const useRoleStore = defineStore('role', () => {
  const roles = ref<RoleResponse[]>([]);
  const loading = ref(false);

  const fetchRoles = async () => {
    loading.value = true;
    try {
      const res: any = await getRoleList();
      // 后端返回 Result<List<RoleVO>>，结构为 { code, message, data: [...] }
      roles.value = res?.data || res || [];
    } catch (error) {
      console.error('获取角色列表失败:', error);
    } finally {
      loading.value = false;
    }
  };

  const fetchRoleDetail = async (id: number) => {
    const res: any = await getRoleDetail(id);
    // 后端返回 Result<RoleVO>，结构为 { code, message, data: {...} }
    return res?.data || res;
  };

  const addRole = async (data: CreateRoleRequest) => {
    await createRole(data);
    await fetchRoles();
  };

  const editRole = async (id: number, data: UpdateRoleRequest) => {
    await updateRole(id, data);
    await fetchRoles();
  };

  const removeRole = async (id: number) => {
    await deleteRole(id);
    await fetchRoles();
  };

  const fetchRolePermissions = async (id: number) => {
    const res: any = await getRolePermissions(id);
    // 后端返回 Result<List<Long>>，结构为 { code, message, data: [...] }
    return res?.data || res || [];
  };

  const saveRolePermissions = async (id: number, permissionIds: number[]) => {
    await updateRolePermissions(id, permissionIds);
  };

  return { roles, loading, fetchRoles, fetchRoleDetail, addRole, editRole, removeRole, fetchRolePermissions, saveRolePermissions };
});
