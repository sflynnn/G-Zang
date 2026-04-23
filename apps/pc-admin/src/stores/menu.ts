import { defineStore } from 'pinia';
import { ref } from 'vue';
import {
  getMenuTree,
  getMenuDetail,
  createMenu,
  updateMenu,
  deleteMenu,
  getMenuTreeOptions,
  getMenuPermissions,
  updateMenuPermissions,
  type MenuResponse,
  type CreateMenuRequest,
  type UpdateMenuRequest
} from '@/api/menu';

export const useMenuStore = defineStore('menu', () => {
  const menus = ref<MenuResponse[]>([]);
  const menuTreeOptions = ref<MenuResponse[]>([]);
  const selectedMenu = ref<MenuResponse | null>(null);
  const loading = ref(false);

  /**
   * 获取菜单树
   */
  const fetchMenus = async () => {
    loading.value = true;
    try {
      const res: any = await getMenuTree();
      menus.value = res?.data || res || [];
    } catch (error) {
      console.error('获取菜单列表失败:', error);
    } finally {
      loading.value = false;
    }
  };

  /**
   * 获取菜单树选项（用于选择器）
   */
  const fetchMenuTreeOptions = async () => {
    try {
      const res: any = await getMenuTreeOptions();
      menuTreeOptions.value = res?.data || res || [];
    } catch (error) {
      console.error('获取菜单树选项失败:', error);
    }
  };

  /**
   * 获取菜单详情
   */
  const fetchMenuDetail = async (id: number) => {
    const res: any = await getMenuDetail(id);
    const menu = res?.data || res;
    selectedMenu.value = menu;
    return menu;
  };

  /**
   * 创建菜单
   */
  const addMenu = async (data: CreateMenuRequest) => {
    await createMenu(data);
    await fetchMenus();
  };

  /**
   * 更新菜单
   */
  const editMenu = async (id: number, data: UpdateMenuRequest) => {
    await updateMenu(id, data);
    await fetchMenus();
  };

  /**
   * 删除菜单
   */
  const removeMenu = async (id: number) => {
    await deleteMenu(id);
    await fetchMenus();
  };

  /**
   * 获取菜单权限列表
   */
  const fetchMenuPermissions = async (menuId: number) => {
    const res: any = await getMenuPermissions(menuId);
    return res?.data || res || [];
  };

  /**
   * 更新菜单权限
   */
  const saveMenuPermissions = async (menuId: number, permissionIds: number[]) => {
    await updateMenuPermissions(menuId, permissionIds);
  };

  /**
   * 将菜单树展平为数组
   */
  const flattenMenus = (menuList: MenuResponse[]): MenuResponse[] => {
    const result: MenuResponse[] = [];
    const flatten = (menus: MenuResponse[]) => {
      for (const menu of menus) {
        result.push(menu);
        if (menu.children && menu.children.length > 0) {
          flatten(menu.children);
        }
      }
    };
    flatten(menuList);
    return result;
  };

  /**
   * 根据 ID 查找菜单
   */
  const findMenuById = (id: number): MenuResponse | null => {
    const flatMenus = flattenMenus(menus.value);
    return flatMenus.find(m => m.id === id) || null;
  };

  /**
   * 递归查找菜单的所有子菜单ID
   */
  const getMenuAndChildrenIds = (menuId: number): number[] => {
    const result: number[] = [];
    const collect = (id: number) => {
      result.push(id);
      const menu = findMenuById(id);
      if (menu?.children && menu.children.length > 0) {
        menu.children.forEach(child => collect(child.id));
      }
    };
    collect(menuId);
    return result;
  };

  return {
    menus,
    menuTreeOptions,
    selectedMenu,
    loading,
    fetchMenus,
    fetchMenuTreeOptions,
    fetchMenuDetail,
    addMenu,
    editMenu,
    removeMenu,
    fetchMenuPermissions,
    saveMenuPermissions,
    flattenMenus,
    findMenuById,
    getMenuAndChildrenIds
  };
});
