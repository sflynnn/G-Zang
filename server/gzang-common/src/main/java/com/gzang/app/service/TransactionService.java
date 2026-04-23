package com.gzang.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.entity.Transaction;
import com.gzang.app.mapper.TransactionMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易记录服务接口
 *
 * @author G-Zang Team
 */
public interface TransactionService extends IService<Transaction> {

    /**
     * 创建交易记录
     *
     * @param transaction 交易记录
     * @return 是否成功
     */
    boolean createTransaction(Transaction transaction);

    /**
     * 更新交易记录
     *
     * @param transaction 交易记录
     * @return 是否成功
     */
    boolean updateTransaction(Transaction transaction);

    /**
     * 删除交易记录
     *
     * @param id 交易记录ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    boolean deleteTransaction(Long id, Long userId);

    /**
     * 分页查询交易记录
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param type 交易类型
     * @param categoryId 分类ID
     * @param bookId 账本ID
     * @return 分页结果
     */
    IPage<Transaction> getTransactionPage(
            Page<Transaction> page,
            Long userId,
            Long companyId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer type,
            Long categoryId,
            Long bookId);

    /**
     * 获取交易汇总信息
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param bookId 账本ID
     * @return 汇总信息
     */
    TransactionMapper.TransactionSummary getTransactionSummary(
            Long userId,
            Long companyId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long bookId);

    /**
     * 按分类统计交易金额
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param type 交易类型
     * @param bookId 账本ID
     * @return 分类统计列表
     */
    List<TransactionMapper.CategorySummary> getCategorySummary(
            Long userId,
            Long companyId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer type,
            Long bookId);
}
