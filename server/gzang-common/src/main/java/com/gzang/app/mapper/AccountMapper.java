package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.entity.Account;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 账户Mapper接口
 *
 * @author G-Zang Team
 */
public interface AccountMapper extends BaseMapper<Account> {

    /**
     * 根据用户ID查询账户列表
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @return 账户列表
     */
    @Select("SELECT * FROM t_account WHERE user_id = #{userId} " +
            "#{companyId != null ? 'AND (company_id IS NULL OR company_id = #{companyId})' : ''} " +
            "ORDER BY create_time DESC")
    List<Account> selectAccountsByUserId(@Param("userId") Long userId, @Param("companyId") Long companyId);

    /**
     * 分页查询账户
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param accountType 账户类型
     * @return 分页结果
     */
    IPage<Account> selectAccountPage(
            Page<Account> page,
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("accountType") Integer accountType);

    /**
     * 统计用户账户总余额
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @return 总余额
     */
    @Select("SELECT COALESCE(SUM(balance), 0) FROM t_account WHERE user_id = #{userId} " +
            "#{companyId != null ? 'AND (company_id IS NULL OR company_id = #{companyId})' : ''}")
    java.math.BigDecimal selectTotalBalance(@Param("userId") Long userId, @Param("companyId") Long companyId);
}
