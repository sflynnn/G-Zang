package com.gzang.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.dto.account.CreateAccountDTO;
import com.gzang.app.dto.account.UpdateAccountDTO;
import com.gzang.app.entity.Account;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.AccountService;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;

/**
 * 账户管理控制器
 * 提供账户管理的相关接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "账户管理", description = "账户管理相关接口")
public class AccountController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    public AccountController(AccountService accountService, JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 创建账户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ACCOUNT_MANAGE')")
    @Operation(summary = "创建账户", description = "新增一个账户")
    public Result<Void> createAccount(@Validated @RequestBody CreateAccountDTO dto, Principal principal) {
        log.info("创建账户请求: user={}, accountName={}", principal.getName(), dto.getAccountName());

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

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
    @PreAuthorize("hasAuthority('ACCOUNT_MANAGE')")
    @Operation(summary = "更新账户", description = "更新指定账户信息")
    public Result<Void> updateAccount(
            @Parameter(description = "账户ID") @PathVariable Long id,
            @Validated @RequestBody UpdateAccountDTO dto,
            Principal principal) {

        log.info("更新账户请求: id={}, user={}", id, principal.getName());

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

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
            throw new BusinessException(400, "更新账户失败，可能账户名称已存在或无权限");
        }

        log.info("账户更新成功: id={}", id);
        return Result.success();
    }

    /**
     * 删除账户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ACCOUNT_MANAGE')")
    @Operation(summary = "删除账户", description = "删除指定账户")
    public Result<Void> deleteAccount(
            @Parameter(description = "账户ID") @PathVariable Long id,
            Principal principal) {

        log.info("删除账户请求: id={}, user={}", id, principal.getName());

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());

        boolean success = accountService.deleteAccount(id, userId);
        if (!success) {
            throw new BusinessException(400, "删除账户失败，可能账户不存在、无权限或还有余额");
        }

        log.info("账户删除成功: id={}", id);
        return Result.success();
    }

    /**
     * 获取账户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACCOUNT_MANAGE')")
    @Operation(summary = "获取账户详情", description = "根据ID获取账户详情")
    public Result<Account> getAccountById(@Parameter(description = "账户ID") @PathVariable Long id) {
        Account account = accountService.getById(id);
        if (account == null) {
            throw new BusinessException(DATA_NOT_FOUND, "账户不存在");
        }
        return Result.success(account);
    }

    /**
     * 获取账户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ACCOUNT_MANAGE')")
    @Operation(summary = "获取账户列表", description = "获取用户的账户列表")
    public Result<List<Account>> getAccountList(Principal principal) {
        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        List<Account> accounts = accountService.getAccountsByUserId(userId, companyId);
        return Result.success(accounts);
    }

    /**
     * 分页查询账户
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('ACCOUNT_MANAGE')")
    @Operation(summary = "分页查询账户", description = "分页获取账户列表")
    public Result<IPage<Account>> getAccountPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "账户类型 (1:现金, 2:银行卡, 3:电子支付)") @RequestParam(required = false) Integer accountType,
            Principal principal) {

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        Page<Account> page = new Page<>(current, size);
        IPage<Account> result = accountService.getAccountPage(page, userId, companyId, accountType);

        return Result.success(result);
    }

    /**
     * 获取账户总余额
     */
    @GetMapping("/total-balance")
    @PreAuthorize("hasAuthority('ACCOUNT_MANAGE')")
    @Operation(summary = "获取总余额", description = "获取用户所有账户的总余额")
    public Result<BigDecimal> getTotalBalance(Principal principal) {
        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        BigDecimal totalBalance = accountService.getTotalBalance(userId, companyId);
        return Result.success(totalBalance);
    }

    /**
     * 调整账户余额
     */
    @PostMapping("/{id}/adjust-balance")
    @PreAuthorize("hasAuthority('ACCOUNT_MANAGE')")
    @Operation(summary = "调整账户余额", description = "手动调整账户余额（仅限自有账户）")
    public Result<Void> adjustBalance(
            @Parameter(description = "账户ID") @PathVariable Long id,
            @Parameter(description = "调整金额") @RequestParam BigDecimal amount,
            Principal principal) {

        log.info("调整账户余额请求: accountId={}, amount={}, user={}", id, amount, principal.getName());

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        boolean success = accountService.adjustBalance(id, amount, userId, companyId);
        if (!success) {
            throw new BusinessException(400, "调整账户余额失败，可能账户不存在或无权操作");
        }

        log.info("账户余额调整成功: accountId={}, amount={}", id, amount);
        return Result.success();
    }
}
