package com.gzang.mobile.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.dto.account.CreateAccountDTO;
import com.gzang.app.dto.account.UpdateAccountDTO;
import com.gzang.app.entity.Account;
import com.gzang.app.exception.BusinessException;
import com.gzang.mobile.service.AccountService;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 移动端账户控制器
 * 移动端 + PC个人记账 共用的账户管理接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/accounts")
@Tag(name = "移动端账户管理", description = "账户管理相关接口")
public class MobileAccountController {

    private static final Logger log = LoggerFactory.getLogger(MobileAccountController.class);

    private final AccountService accountService;

    public MobileAccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 获取账户列表
     */
    @GetMapping
    @Operation(summary = "获取账户列表", description = "获取当前用户的账户列表")
    public Result<List<Account>> getAccountList() {
        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();
        List<Account> accounts = accountService.getAccountsByUserId(userId, companyId);
        return Result.success(accounts);
    }

    /**
     * 获取账户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取账户详情", description = "根据ID获取账户详情")
    public Result<Account> getAccountById(
            @Parameter(description = "账户ID") @PathVariable Long id) {
        Account account = accountService.getById(id);
        if (account == null) {
            throw new BusinessException(404, "账户不存在");
        }
        return Result.success(account);
    }

    /**
     * 创建账户
     */
    @PostMapping
    @Operation(summary = "创建账户", description = "新增一个账户")
    public Result<Void> createAccount(@Validated @RequestBody CreateAccountDTO dto) {
        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();
        
        log.info("创建账户请求: userId={}, accountName={}", userId, dto.getAccountName());

        Account account = new Account();
        account.setAccountName(dto.getAccountName());
        account.setAccountType(dto.getAccountType());
        account.setBalance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO);
        account.setUserId(userId);
        account.setCompanyId(companyId);

        boolean success = accountService.createAccount(account);
        if (!success) {
            throw new BusinessException(400, "创建账户失败，可能账户名称已存在");
        }

        log.info("账户创建成功: id={}", account.getId());
        return Result.success();
    }

    /**
     * 更新账户
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新账户", description = "更新指定账户信息")
    public Result<Void> updateAccount(
            @Parameter(description = "账户ID") @PathVariable Long id,
            @Validated @RequestBody UpdateAccountDTO dto) {

        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();
        
        log.info("更新账户请求: id={}, userId={}", id, userId);

        Account account = new Account();
        account.setId(id);
        account.setUserId(userId);
        account.setCompanyId(companyId);
        if (dto.getAccountName() != null) {
            account.setAccountName(dto.getAccountName());
        }
        if (dto.getAccountType() != null) {
            account.setAccountType(dto.getAccountType());
        }

        boolean success = accountService.updateAccount(account);
        if (!success) {
            throw new BusinessException(400, "更新账户失败");
        }

        log.info("账户更新成功: id={}", id);
        return Result.success();
    }

    /**
     * 删除账户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除账户", description = "删除指定账户")
    public Result<Void> deleteAccount(
            @Parameter(description = "账户ID") @PathVariable Long id) {

        Long userId = TenantContextHolder.getUserId();
        log.info("删除账户请求: id={}, userId={}", id, userId);

        boolean success = accountService.deleteAccount(id, userId);
        if (!success) {
            throw new BusinessException(400, "删除账户失败");
        }

        log.info("账户删除成功: id={}", id);
        return Result.success();
    }

    /**
     * 获取账户总余额
     */
    @GetMapping("/total-balance")
    @Operation(summary = "获取总余额", description = "获取用户所有账户的总余额")
    public Result<BigDecimal> getTotalBalance() {
        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();
        BigDecimal totalBalance = accountService.getTotalBalance(userId, companyId);
        return Result.success(totalBalance);
    }

    /**
     * 分页查询账户
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询账户", description = "分页获取账户列表")
    public Result<IPage<Account>> getAccountPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        Page<Account> page = new Page<>(current, size);
        IPage<Account> result = accountService.getAccountPage(page, userId, companyId, null);

        return Result.success(result);
    }
}
