import api from './index';

/** 公司查询参数 */
export interface CompanyQueryParams {
  keyword?: string;
  status?: number;
}

/** 公司响应 */
export interface CompanyResponse {
  id: number;
  companyName: string;
  companyCode: string;
  contactName?: string;
  contactPhone?: string;
  contactEmail?: string;
  address?: string;
  status: number;
  createTime: string;
  creditCode?: string;
  userCount?: number;
}

/** 创建公司请求 */
export interface CreateCompanyRequest {
  companyName: string;
  companyCode: string;
  contactName?: string;
  contactPhone?: string;
  contactEmail?: string;
  address?: string;
}

/** 更新公司请求 */
export interface UpdateCompanyRequest extends CreateCompanyRequest {
  status?: number;
}

/** 公司列表查询参数 */
export interface CompanyListParams {
  keyword?: string;
  status?: number;
  current?: number;
  size?: number;
}

/** 分页响应 */
export interface PageResponse<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

/** 统一 API 响应 */
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 获取公司列表
export const getCompanyList = (params?: CompanyListParams): Promise<ApiResponse<PageResponse<CompanyResponse>>> => {
  return api.get<ApiResponse<PageResponse<CompanyResponse>>>('/api/v1/companies', { params });
};

// 获取公司详情
export const getCompanyDetail = (id: number) => {
  return api.get<CompanyResponse>(`/api/v1/companies/${id}`);
};

// 创建公司
export const createCompany = (data: CreateCompanyRequest) => {
  return api.post('/api/v1/companies', data);
};

// 更新公司
export const updateCompany = (id: number, data: UpdateCompanyRequest) => {
  return api.put(`/api/v1/companies/${id}`, data);
};

// 删除公司
export const deleteCompany = (id: number) => {
  return api.delete(`/api/v1/companies/${id}`);
};
