package com.gzang.mobile.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.Account;
import com.gzang.app.mapper.AccountMapper;
import com.gzang.mobile.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Override
    public boolean createAccount(Account account) {
        log.info("创建账户: userId={}, accountName={}", account.getUserId(), account.getAccountName());

        // 检查账户名称是否重复
        List<Account> existingAccounts = getBaseMapper().selectAccountsByUserId(account.getUserId(), account.getCompanyId());
        boolean nameExists = existingAccounts.stream()
                .anyMatch(acc -> acc.getAccountName().equals(account.getAccountName()));

        if (nameExists) {
            log.warn("账户名称已存在: userId={}, accountName={}", account.getUserId(), account.getAccountName());
            return false;
        }

        // 设置默认余额
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        boolean result = save(account);
        if (result) {
            log.info("账户创建成功: id={}", account.getId());
        }

        return result;
    }

    @Override
    public boolean updateAccount(Account account) {
        log.info("更新账户: id={}, userId={}", account.getId(), account.getUserId());

        // 获取原账户信息
        Account oldAccount = getById(account.getId());
        if (oldAccount == null) {
            log.warn("账户不存在: id={}", account.getId());
            return false;
        }

        // 验证权限
        if (!oldAccount.getUserId().equals(account.getUserId()) &&
            !oldAccount.getCompanyId().equals(account.getCompanyId())) {
            log.warn("用户无权修改此账户: userId={}, accountId={}", account.getUserId(), account.getId());
            return false;
        }

        // 检查账户名称是否重复（排除自己）
        List<Account> existingAccounts = getBaseMapper().selectAccountsByUserId(account.getUserId(), account.getCompanyId());
        boolean nameExists = existingAccounts.stream()
                .anyMatch(acc -> !acc.getId().equals(account.getId()) &&
                               acc.getAccountName().equals(account.getAccountName()));

        if (nameExists) {
            log.warn("账户名称已存在: userId={}, accountName={}", account.getUserId(), account.getAccountName());
            return false;
        }

        boolean result = updateById(account);
        if (result) {
            log.info("账户更新成功: id={}", account.getId());
        }

        return result;
    }

    @Override
    public boolean deleteAccount(Long id, Long userId) {
        log.info("删除账户: id={}, userId={}", id, userId);

        Account account = getById(id);
        if (account == null) {
            log.warn("账户不存在: id={}", id);
            return false;
        }

        // 验证权限：账户所有者本人
        if (!account.getUserId().equals(userId)) {
            log.warn("用户无权删除此账户: userId={}, accountId={}", userId, id);
            return false;
        }

        // 检查账户是否有余额
        if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.warn("账户还有余额，不能删除: id={}, balance={}", id, account.getBalance());
            return false;
        }

        boolean result = removeById(id);
        if (result) {
            log.info("账户删除成功: id={}", id);
        }

        return result;
    }

    @Override
    public List<Account> getAccountsByUserId(Long userId, Long companyId) {
        return getBaseMapper().selectAccountsByUserId(userId, companyId);
    }

    @Override
    public IPage<Account> getAccountPage(Page<Account> page, Long userId, Long companyId, Integer accountType) {
        return getBaseMapper().selectAccountPage(page, userId, companyId, accountType);
    }

    @Override
    public BigDecimal getTotalBalance(Long userId, Long companyId) {
        BigDecimal totalBalance = getBaseMapper().selectTotalBalance(userId, companyId);
        return totalBalance != null ? totalBalance : BigDecimal.ZERO;
    }

    @Override
    public boolean adjustBalance(Long accountId, BigDecimal amount, Long userId, Long companyId) {
        log.info("调整账户余额: accountId={}, amount={}, operatorUserId={}", accountId, amount, userId);

        Account account = getById(accountId);
        if (account == null) {
            log.warn("账户不存在: accountId={}", accountId);
            return false;
        }

        // 数据归属验证：账户必须属于当前用户或当前公司
        boolean isOwner = account.getUserId() != null && account.getUserId().equals(userId);
        boolean isInCompany = companyId != null
                && account.getCompanyId() != null
                && account.getCompanyId().equals(companyId);

        if (!isOwner && !isInCompany) {
            log.warn("无权调整此账户余额: operatorUserId={}, companyId={}, accountOwnerUserId={}, accountCompanyId={}",
                    userId, companyId, account.getUserId(), account.getCompanyId());
            return false;
        }

        // 防止余额变为负数（仅在减少时检查）
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal newBalance = account.getBalance().add(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                log.warn("余额不足，无法减少: accountId={}, currentBalance={}, reduceAmount={}",
                        accountId, account.getBalance(), amount.abs());
                return false;
            }
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        boolean result = updateById(account);
        if (result) {
            log.info("账户余额调整成功: accountId={}, oldBalance={}, newBalance={}",
                    accountId, account.getBalance().subtract(amount), newBalance);
        }

        return result;
    }
}
