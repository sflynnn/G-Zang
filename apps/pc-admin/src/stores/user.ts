import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getUsers, createUser, updateUser, deleteUser } from '@/api/user';

export const useUserStore = defineStore('user', () => {
  // 用户列表
  const users = ref([]);
  const total = ref(0);
  const loading = ref(false);

  // 获取用户列表
  const fetchUsers = async (params: any = {}) => {
    loading.value = true;
    try {
      const response = await getUsers(params);
      users.value = (response as any).data;
      total.value = (response as any).total || 0;
    } catch (error) {
      console.error('获取用户列表失败:', error);
    } finally {
      loading.value = false;
    }
  };

  // 创建用户
  const addUser = async (userData: any) => {
    try {
      await createUser(userData);
      await fetchUsers(); // 重新获取列表
      return true;
    } catch (error) {
      console.error('创建用户失败:', error);
      return false;
    }
  };

  // 更新用户
  const editUser = async (id: number, userData: any) => {
    try {
      await updateUser(id, userData);
      await fetchUsers(); // 重新获取列表
      return true;
    } catch (error) {
      console.error('更新用户失败:', error);
      return false;
    }
  };

  // 删除用户
  const removeUser = async (id: number) => {
    try {
      await deleteUser(id);
      await fetchUsers(); // 重新获取列表
      return true;
    } catch (error) {
      console.error('删除用户失败:', error);
      return false;
    }
  };

  return {
    // 状态
    users,
    total,
    loading,

    // 方法
    fetchUsers,
    addUser,
    editUser,
    removeUser
  };
});
