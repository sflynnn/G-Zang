package com.gzang.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.entity.Company;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.CompanyService;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;

/**
 * 管理端公司管理控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/admin/companies")
@Tag(name = "管理端公司管理", description = "公司管理相关接口")
public class AdminCompanyController {

    private final CompanyService companyService;
    private final JwtUtil jwtUtil;

    public AdminCompanyController(CompanyService companyService, JwtUtil jwtUtil) {
        this.companyService = companyService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取公司列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('COMPANY_MANAGE')")
    @Operation(summary = "获取公司列表", description = "分页获取公司列表")
    public Result<IPage<Company>> getCompanyList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Page<Company> page = new Page<>(current, size);
        IPage<Company> companyPage = companyService.getCompanyPage(page, keyword, status);
        return Result.success(companyPage);
    }

    /**
     * 获取公司详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_MANAGE')")
    @Operation(summary = "获取公司详情", description = "根据ID获取公司详情")
    public Result<Company> getCompanyById(
            @Parameter(description = "公司ID") @PathVariable Long id) {
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
    @PreAuthorize("hasAuthority('COMPANY_MANAGE')")
    @Operation(summary = "创建公司", description = "创建新公司")
    public Result<Void> createCompany(@RequestBody Company company) {
        boolean success = companyService.createCompany(company);
        if (!success) {
            throw new BusinessException(400, "创建公司失败");
        }
        return Result.success();
    }

    /**
     * 更新公司
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_MANAGE')")
    @Operation(summary = "更新公司", description = "更新公司信息")
    public Result<Void> updateCompany(
            @Parameter(description = "公司ID") @PathVariable Long id,
            @RequestBody Company company) {
        company.setId(id);
        boolean success = companyService.updateCompany(company);
        if (!success) {
            throw new BusinessException(400, "更新公司失败");
        }
        return Result.success();
    }

    /**
     * 删除公司
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_MANAGE')")
    @Operation(summary = "删除公司", description = "删��公司")
    public Result<Void> deleteCompany(
            @Parameter(description = "公司ID") @PathVariable Long id) {
        boolean success = companyService.deleteCompany(id);
        if (!success) {
            throw new BusinessException(400, "删除公司失败");
        }
        return Result.success();
    }
}
