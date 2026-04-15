package com.gzang.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.entity.Company;

/**
 * 公司服务接口
 *
 * @author G-Zang Team
 */
public interface CompanyService extends IService<Company> {

    /**
     * 分页查询公司列表
     *
     * @param page    分页对象
     * @param keyword 关键词（公司名/编码/联系人）
     * @param status 状态
     * @return 分页结果
     */
    IPage<Company> getCompanyPage(Page<Company> page, String keyword, Integer status);

    /**
     * 根据公司编码查询
     *
     * @param companyCode 公司编码
     * @return 公司信息
     */
    Company getByCompanyCode(String companyCode);

    /**
     * 创建公司
     *
     * @param company 公司信息
     * @return 是否成功
     */
    boolean createCompany(Company company);

    /**
     * 更新公司信息
     *
     * @param company 公司信息
     * @return 是否成功
     */
    boolean updateCompany(Company company);

    /**
     * 删除公司
     *
     * @param id 公司ID
     * @return 是否成功
     */
    boolean deleteCompany(Long id);
}
