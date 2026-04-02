package com.gzang.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.annotation.OperationLog;
import com.gzang.app.annotation.RequirePermission;
import com.gzang.app.constant.BusinessType;
import com.gzang.app.constant.PermissionCode;
import com.gzang.app.entity.Company;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.CompanyService;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;

/**
 * 公司管理控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "公司管理", description = "公司/组织管理相关接口")
public class CompanyController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CompanyController.class);

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * 获取公司列表（分页）
     */
    @GetMapping
    @RequirePermission(PermissionCode.COMPANY_MANAGE)
    @Operation(summary = "获取公司列表", description = "分页获取公司列表，支持关键词和状态过滤")
    public Result<IPage<Company>> getCompanyList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词（公司名/编码/联系人）") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态：1=正常，0=禁用") @RequestParam(required = false) Integer status) {

        Page<Company> page = new Page<>(current, size);
        IPage<Company> result = companyService.getCompanyPage(page, keyword, status);
        return Result.success(result);
    }

    /**
     * 获取公司详情
     */
    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.COMPANY_MANAGE)
    @Operation(summary = "获取公司详情", description = "根据ID获取公司详情")
    public Result<Company> getCompanyById(@Parameter(description = "公司ID") @PathVariable Long id) {
        Company company = companyService.getById(id);
        if (company == null) {
            throw new BusinessException(DATA_NOT_FOUND, "公司不存在");
        }
        return Result.success(company);
    }

    /**
     * 创建公司
     */
    @PostMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @OperationLog(module = "COMPANY", action = BusinessType.CREATE, describe = "创建公司：#{#company.companyName}")
    @Operation(summary = "创建公司", description = "创建新公司")
    public Result<Void> createCompany(@Validated @RequestBody Company company) {
        log.info("创建公司请求: companyName={}", company.getCompanyName());
        companyService.createCompany(company);
        return Result.success();
    }

    /**
     * 更新公司
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @OperationLog(module = "COMPANY", action = BusinessType.UPDATE, describe = "更新公司ID：#{#id}")
    @Operation(summary = "更新公司", description = "更新公司信息")
    public Result<Void> updateCompany(
            @Parameter(description = "公司ID") @PathVariable Long id,
            @Validated @RequestBody Company company) {
        company.setId(id);
        companyService.updateCompany(company);
        return Result.success();
    }

    /**
     * 删除公司
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @OperationLog(module = "COMPANY", action = BusinessType.DELETE, describe = "删除公司ID：#{#id}")
    @Operation(summary = "删除公司", description = "删除指定公司")
    public Result<Void> deleteCompany(@Parameter(description = "公司ID") @PathVariable Long id) {
        log.info("删除公司请求: id={}", id);
        companyService.deleteCompany(id);
        return Result.success();
    }
}
