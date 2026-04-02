package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.entity.Company;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 公司Mapper接口
 *
 * @author G-Zang Team
 */
@Mapper
public interface CompanyMapper extends BaseMapper<Company> {

    /**
     * 按公司编码查询（唯一性校验）
     */
    Company selectByCompanyCode(@Param("companyCode") String companyCode);

    /**
     * 分页查询公司列表（支持关键词过滤）
     */
    IPage<Company> selectPageWithKeyword(Page<Company> page,
                                       @Param("keyword") String keyword,
                                       @Param("status") Integer status);
}
