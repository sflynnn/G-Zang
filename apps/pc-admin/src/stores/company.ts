import { defineStore } from 'pinia';
import { ref } from 'vue';
import {
  getCompanyList,
  getCompanyDetail,
  createCompany,
  updateCompany,
  deleteCompany,
  type CompanyResponse,
  type CreateCompanyRequest,
  type UpdateCompanyRequest,
  type CompanyListParams
} from '@/api/company';

export const useCompanyStore = defineStore('company', () => {
  const companies = ref<CompanyResponse[]>([]);
  const total = ref(0);
  const loading = ref(false);
  const current = ref(1);
  const size = ref(10);

  const fetchCompanies = async (params?: CompanyListParams) => {
    loading.value = true;
    try {
      const res = await getCompanyList(params);
      companies.value = res.data.records;
      total.value = res.data.total;
      if (params?.current !== undefined) current.value = params.current;
      if (params?.size !== undefined) size.value = params.size;
    } catch (error) {
      console.error('获取公司列表失败:', error);
    } finally {
      loading.value = false;
    }
  };

  const fetchCompanyDetail = async (id: number) => {
    return await getCompanyDetail(id);
  };

  const addCompany = async (data: CreateCompanyRequest) => {
    await createCompany(data);
    await fetchCompanies();
  };

  const editCompany = async (id: number, data: UpdateCompanyRequest) => {
    await updateCompany(id, data);
    await fetchCompanies();
  };

  const removeCompany = async (id: number) => {
    await deleteCompany(id);
    await fetchCompanies();
  };

  return { companies, total, current, size, loading, fetchCompanies, fetchCompanyDetail, addCompany, editCompany, removeCompany };
});
