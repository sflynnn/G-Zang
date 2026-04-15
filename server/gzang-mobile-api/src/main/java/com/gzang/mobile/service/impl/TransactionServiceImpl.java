package com.gzang.mobile.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.Account;
import com.gzang.app.entity.Transaction;
import com.gzang.app.mapper.TransactionMapper;
import com.gzang.mobile.service.AccountService;
import com.gzang.mobile.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易记录服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final AccountService accountService;

    public TransactionServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    @Transactional
    public boolean createTransaction(Transaction transaction) {
        log.info("创建交易记录: userId={}, amount={}, type={}",
                transaction.getUserId(), transaction.getAmount(), transaction.getType());

        // 验证账户是否存在且属于当前用户
        Account account = accountService.getById(transaction.getAccountId());
        if (account == null) {
            log.warn("账户不存在: accountId={}", transaction.getAccountId());
            return false;
        }

        if (!account.getUserId().equals(transaction.getUserId()) &&
            !account.getCompanyId().equals(transaction.getCompanyId())) {
            log.warn("用户无权访问此账户: userId={}, accountId={}",
                    transaction.getUserId(), transaction.getAccountId());
            return false;
        }

        // 保存交易记录
        boolean result = save(transaction);

        if (result) {
            // 更新账户余额
            updateAccountBalance(transaction.getAccountId(), transaction.getAmount(), transaction.getType());
            log.info("交易记录创建成功: id={}", transaction.getId());
        }

        return result;
    }

    @Override
    @Transactional
    public boolean updateTransaction(Transaction transaction) {
        log.info("更新交易记录: id={}, userId={}", transaction.getId(), transaction.getUserId());

        // 获取原交易记录
        Transaction oldTransaction = getById(transaction.getId());
        if (oldTransaction == null) {
            log.warn("交易记录不存在: id={}", transaction.getId());
            return false;
        }

        // 验证权限
        if (!oldTransaction.getUserId().equals(transaction.getUserId())) {
            log.warn("用户无权修改此交易记录: userId={}, transactionId={}",
                    transaction.getUserId(), transaction.getId());
            return false;
        }

        // 撤销原交易对账户余额的影响
        updateAccountBalance(oldTransaction.getAccountId(), oldTransaction.getAmount().negate(), oldTransaction.getType());

        // 如果账户发生变化，需要验证新账户权限
        if (!oldTransaction.getAccountId().equals(transaction.getAccountId())) {
            Account newAccount = accountService.getById(transaction.getAccountId());
            if (newAccount == null ||
                (!newAccount.getUserId().equals(transaction.getUserId()) &&
                 !newAccount.getCompanyId().equals(transaction.getCompanyId()))) {
                log.warn("用户无权访问新账户: userId={}, accountId={}",
                        transaction.getUserId(), transaction.getAccountId());
                // 恢复原账户余额
                updateAccountBalance(oldTransaction.getAccountId(), oldTransaction.getAmount(), oldTransaction.getType());
                return false;
            }
        }

        // 更新交易记录
        boolean result = updateById(transaction);

        if (result) {
            // 应用新交易对账户余额的影响
            updateAccountBalance(transaction.getAccountId(), transaction.getAmount(), transaction.getType());
            log.info("交易记录更新成功: id={}", transaction.getId());
        } else {
            // 恢复原账户余额
            updateAccountBalance(oldTransaction.getAccountId(), oldTransaction.getAmount(), oldTransaction.getType());
        }

        return result;
    }

    @Override
    @Transactional
    public boolean deleteTransaction(Long id, Long userId) {
        log.info("删除交易记录: id={}, userId={}", id, userId);

        // 获取交易记录
        Transaction transaction = getById(id);
        if (transaction == null) {
            log.warn("交易记录不存在: id={}", id);
            return false;
        }

        // 验证权限
        if (!transaction.getUserId().equals(userId)) {
            log.warn("用户无权删除此交易记录: userId={}, transactionId={}", userId, id);
            return false;
        }

        // 删除交易记录
        boolean result = removeById(id);

        if (result) {
            // 撤销对账户余额的影响
            updateAccountBalance(transaction.getAccountId(), transaction.getAmount().negate(), transaction.getType());
            log.info("交易记录删除成功: id={}", id);
        }

        return result;
    }

    @Override
    public IPage<Transaction> getTransactionPage(Page<Transaction> page, Long userId, Long companyId,
                                                LocalDateTime startTime, LocalDateTime endTime,
                                                Integer type, Long categoryId) {
        return getBaseMapper().selectTransactionPage(page, userId, companyId, startTime, endTime, type, categoryId);
    }

    @Override
    public TransactionMapper.TransactionSummary getTransactionSummary(Long userId, Long companyId,
                                                                     LocalDateTime startTime, LocalDateTime endTime) {
        return getBaseMapper().selectTransactionSummary(userId, companyId, startTime, endTime);
    }

    @Override
    public List<TransactionMapper.CategorySummary> getCategorySummary(Long userId, Long companyId,
                                                                     LocalDateTime startTime, LocalDateTime endTime,
                                                                     Integer type) {
        return getBaseMapper().selectCategorySummary(userId, companyId, startTime, endTime, type);
    }

    /**
     * 更新账户余额
     *
     * @param accountId 账户ID
     * @param amount 金额
     * @param type 交易类型 (1:收入, 2:支出)
     */
    private void updateAccountBalance(Long accountId, BigDecimal amount, Integer type) {
        Account account = accountService.getById(accountId);
        if (account != null) {
            BigDecimal oldBalance = account.getBalance();
            BigDecimal newBalance = oldBalance;
            if (type == 1) { // 收入
                newBalance = oldBalance.add(amount);
            } else if (type == 2) { // 支出
                newBalance = oldBalance.subtract(amount);
            }

            account.setBalance(newBalance);
            accountService.updateById(account);

            log.debug("账户余额更新: accountId={}, oldBalance={}, newBalance={}",
                    accountId, oldBalance, newBalance);
        }
    }
}
