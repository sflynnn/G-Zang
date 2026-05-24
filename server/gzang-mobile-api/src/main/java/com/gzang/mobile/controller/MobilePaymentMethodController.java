package com.gzang.mobile.controller;

import com.gzang.app.entity.PaymentMethod;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.PaymentMethodMapper;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 移动端支付方式控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/payment-methods")
@io.swagger.v3.oas.annotations.tags.Tag(name = "移动端支付方式管理", description = "支付方式管理相关接口")
public class MobilePaymentMethodController {

    private static final Logger log = LoggerFactory.getLogger(MobilePaymentMethodController.class);

    private final PaymentMethodMapper paymentMethodMapper;

    public MobilePaymentMethodController(PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodMapper = paymentMethodMapper;
    }

    /**
     * 获取用户支付方式列表
     */
    @GetMapping
    @Operation(summary = "获取支付方式列表", description = "获取用户的所有支付方式")
    public Result<List<PaymentMethod>> getPaymentMethods() {
        Long userId = TenantContextHolder.getUserId();
        List<PaymentMethod> methods = paymentMethodMapper.selectPaymentMethodsByUserId(userId);
        return Result.success(methods);
    }

    /**
     * 创建支付方式
     */
    @PostMapping
    @Operation(summary = "创建支付方式", description = "新增一个支付方式")
    public Result<PaymentMethod> createPaymentMethod(@RequestBody CreatePaymentMethodDTO dto) {
        Long userId = TenantContextHolder.getUserId();
        
        log.info("创建支付方式请求: userId={}, methodCode={}", userId, dto.getMethodCode());

        // 检查代码是否已存在
        if (paymentMethodMapper.countByCode(userId, dto.getMethodCode(), 0L) > 0) {
            throw new BusinessException(400, "支付方式代码已存在");
        }

        PaymentMethod method = new PaymentMethod();
        method.setUserId(userId);
        method.setMethodCode(dto.getMethodCode());
        method.setMethodName(dto.getMethodName());
        method.setIcon(dto.getIcon());
        method.setColor(dto.getColor() != null ? dto.getColor() : "#0F4C5C");
        method.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        method.setIsEnabled(1);
        method.setCreateTime(LocalDateTime.now());
        method.setUpdateTime(LocalDateTime.now());

        paymentMethodMapper.insert(method);
        
        log.info("支付方式创建成功: id={}", method.getId());
        return Result.success(method);
    }

    /**
     * 更新支付方式
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新支付方式", description = "更新指定支付方式信息")
    public Result<Void> updatePaymentMethod(
            @Parameter(description = "支付方式ID") @PathVariable Long id,
            @RequestBody UpdatePaymentMethodDTO dto) {
        Long userId = TenantContextHolder.getUserId();
        
        log.info("更新支付方式请求: id={}, userId={}", id, userId);

        PaymentMethod method = paymentMethodMapper.selectById(id);
        if (method == null || !method.getUserId().equals(userId)) {
            throw new BusinessException(400, "支付方式不存在或无权修改");
        }

        if (dto.getMethodName() != null) {
            method.setMethodName(dto.getMethodName());
        }
        if (dto.getIcon() != null) {
            method.setIcon(dto.getIcon());
        }
        if (dto.getColor() != null) {
            method.setColor(dto.getColor());
        }
        if (dto.getSortOrder() != null) {
            method.setSortOrder(dto.getSortOrder());
        }
        if (dto.getIsEnabled() != null) {
            method.setIsEnabled(dto.getIsEnabled());
        }
        method.setUpdateTime(LocalDateTime.now());

        paymentMethodMapper.updateById(method);
        
        log.info("支付方式更新成功: id={}", id);
        return Result.success();
    }

    /**
     * 删除支付方式
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除支付方式", description = "删除指定支付方式")
    public Result<Void> deletePaymentMethod(@Parameter(description = "支付方式ID") @PathVariable Long id) {
        Long userId = TenantContextHolder.getUserId();
        
        log.info("删除支付方式请求: id={}, userId={}", id, userId);

        PaymentMethod method = paymentMethodMapper.selectById(id);
        if (method == null || !method.getUserId().equals(userId)) {
            throw new BusinessException(400, "支付方式不存在或无权删除");
        }

        paymentMethodMapper.deleteById(id);
        
        log.info("支付方式删除成功: id={}", id);
        return Result.success();
    }

    // DTOs
    public static class CreatePaymentMethodDTO {
        private String methodCode;
        private String methodName;
        private String icon;
        private String color;
        private Integer sortOrder;

        public String getMethodCode() { return methodCode; }
        public void setMethodCode(String methodCode) { this.methodCode = methodCode; }
        public String getMethodName() { return methodName; }
        public void setMethodName(String methodName) { this.methodName = methodName; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    }

    public static class UpdatePaymentMethodDTO {
        private String methodName;
        private String icon;
        private String color;
        private Integer sortOrder;
        private Integer isEnabled;

        public String getMethodName() { return methodName; }
        public void setMethodName(String methodName) { this.methodName = methodName; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
        public Integer getIsEnabled() { return isEnabled; }
        public void setIsEnabled(Integer isEnabled) { this.isEnabled = isEnabled; }
    }
}
