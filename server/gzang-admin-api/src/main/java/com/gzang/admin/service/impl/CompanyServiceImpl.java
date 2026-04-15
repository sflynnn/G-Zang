package com.gzang.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.Company;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.CompanyMapper;
import com.gzang.admin.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.gzang.app.constant.ErrorCode.DATA_ALREADY_EXISTS;
import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;

/**
 * 公司服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    @Override
    public IPage<Company> getCompanyPage(Page<Company> page, String keyword, Integer status) {
        return getBaseMapper().selectPageWithKeyword(page, keyword, status);
    }

    @Override
    public Company getByCompanyCode(String companyCode) {
        return getBaseMapper().selectByCompanyCode(companyCode);
    }

    @Override
    public boolean createCompany(Company company) {
        // 检查公司编码唯一性
        Company existing = getByCompanyCode(company.getCompanyCode());
        if (existing != null) {
            log.warn("公司编码已存在: {}", company.getCompanyCode());
            throw new BusinessException(DATA_ALREADY_EXISTS, "公司编码已存在");
        }
        // 设置默认状态
        if (company.getStatus() == null) {
            company.setStatus(1);
        }
        boolean result = save(company);
        if (result) {
            log.info("公司创建成功: id={}, name={}", company.getId(), company.getCompanyName());
        }
        return result;
    }

    @Override
    public boolean updateCompany(Company company) {
        Company existing = getById(company.getId());
        if (existing == null) {
            log.warn("公司不存在: id={}", company.getId());
            throw new BusinessException(DATA_NOT_FOUND, "公司不存在");
        }
        // 如果修改了公司编码，检查新编码唯一性
        if (!existing.getCompanyCode().equals(company.getCompanyCode())) {
            Company byCode = getByCompanyCode(company.getCompanyCode());
            if (byCode != null) {
                throw new BusinessException(DATA_ALREADY_EXISTS, "公司编码已存在");
            }
        }
        boolean result = updateById(company);
        if (result) {
            log.info("公司更新成功: id={}", company.getId());
        }
        return result;
    }

    @Override
    public boolean deleteCompany(Long id) {
        Company company = getById(id);
        if (company == null) {
            throw new BusinessException(DATA_NOT_FOUND, "公司不存在");
        }
        boolean result = removeById(id);
        if (result) {
            log.info("公司删除成功: id={}", id);
        }
        return result;
    }
}
