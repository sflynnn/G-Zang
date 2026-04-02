import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getAllPermissions, type GroupedPermissions } from '@/api/permission';

export const usePermissionStore = defineStore('permission', () => {
  const groupedPermissions = ref<GroupedPermissions>({});
  const loading = ref(false);

  const fetchPermissions = async () => {
    loading.value = true;
    try {
      const res: any = await getAllPermissions();
      // 后端返回 Result<Map<String, List<PermissionVO>>>，结构为 { code, message, data: {...} }
      groupedPermissions.value = res?.data || res || {};
    } catch (error) {
      console.error('获取权限列表失败:', error);
    } finally {
      loading.value = false;
    }
  };

  // 将分组权限转换为扁平数组（用于表格展示）
  const flatPermissions = ref<any[]>([]);

  const buildFlatList = () => {
    const list: any[] = [];
    for (const [group, perms] of Object.entries(groupedPermissions.value)) {
      for (const p of perms) {
        list.push({ ...p, groupLabel: group });
      }
    }
    flatPermissions.value = list;
  };

  return { groupedPermissions, flatPermissions, loading, fetchPermissions, buildFlatList };
});
