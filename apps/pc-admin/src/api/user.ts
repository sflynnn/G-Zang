import api from './index';

// 获取用户列表
export const getUsers = (params?: any) => {
  return api.get('/admin/users', { params });
};

// 获取单个用户信息
export const getUser = (id: number) => {
  return api.get(`/admin/users/${id}`);
};

// 创建用户
export const createUser = (data: any) => {
  return api.post('/admin/users', data);
};

// 更新用户
export const updateUser = (id: number, data: any) => {
  return api.put(`/admin/users/${id}`, data);
};

// 删除用户
export const deleteUser = (id: number) => {
  return api.delete(`/admin/users/${id}`);
};

// 获取当前用户信息
export const getLoginUser = () => {
  return api.get('/admin/users/me');
};
