import httpClient from './http';
import { FinanceSummary, Result } from '../types';

// 报表相关API
export const reportApi = {
  // 获取收支汇总
  getSummary: (params?: { startDate?: string; endDate?: string }): Promise<Result<FinanceSummary>> => {
    return httpClient.get('/report/summary', { params });
  },

  // 获取分类统计
  getCategoryStats: (params?: { startDate?: string; endDate?: string; type?: number }): Promise<Result<any[]>> => {
    return httpClient.get('/report/category-stats', { params });
  },

  // 获取趋势分析
  getTrendAnalysis: (params?: { startDate?: string; endDate?: string; period?: string }): Promise<Result<any[]>> => {
    return httpClient.get('/report/trend', { params });
  },

  // 获取年度财务报告
  getAnnualReport: (year: number): Promise<Result<any>> => {
    return httpClient.get(`/report/annual/${year}`);
  }
};
