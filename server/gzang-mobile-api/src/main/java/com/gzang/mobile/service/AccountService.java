package com.gzang.mobile.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.entity.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户服务接口
 *
 * @author G-Zang Team
 */
public interface AccountService extends IService<Account> {

    /**
     * 创建账户
     *
     * @param account 账户信息
     * @return 是否成功
     */
    boolean createAccount(Account account);

    /**
     * 更新账户
     *
     * @param account 账户信息
     * @return 是否成功
     */
    boolean updateAccount(Account account);

    /**
     * 删除账户
     *
     * @param id 账户ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    boolean deleteAccount(Long id, Long userId);

    /**
     * 根据用户ID查询账户列表
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @return 账户列表
     */
    List<Account> getAccountsByUserId(Long userId, Long companyId);

    /**
     * 分页查询账户
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param accountType 账户类型
     * @return 分页结果
     */
    IPage<Account> getAccountPage(Page<Account> page, Long userId, Long companyId, Integer accountType);

    /**
     * 获取用户账户总余额
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @return 总余额
     */
    BigDecimal getTotalBalance(Long userId, Long companyId);

    /**
     * 调整账户余额（带数据归属验证）
     *
     * @param accountId 账户ID
     * @param amount 调整金额（正数增加，负数减少）
     * @param userId 当前用户ID
     * @param companyId 当前公司ID（可为null表示超级管理员）
     * @return 是否成功
     */
    boolean adjustBalance(Long accountId, BigDecimal amount, Long userId, Long companyId);
}
