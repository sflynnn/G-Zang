import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getUsers, createUser, updateUser, deleteUser } from '@/api/user';

export const useUserStore = defineStore('user', () => {
  // 用户列表
  const users = ref([]);
  const total = ref(0);
  const loading = ref(false);

  // 分页状态
  const page = ref(1);
  const pageSize = ref(10);

  // 获取用户列表
  const fetchUsers = async () => {
    loading.value = true;
    try {
      const data: any = await getUsers({
        current: page.value,
        size: pageSize.value
      });
      // 后端返回 IPage<User> 结构：{ records, total, size, current }
      users.value = data?.records || [];
      total.value = data?.total || 0;
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
      await fetchUsers();
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
      await fetchUsers();
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
      await fetchUsers();
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
    page,
    pageSize,

    // 方法
    fetchUsers,
    addUser,
    editUser,
    removeUser
  };
});
