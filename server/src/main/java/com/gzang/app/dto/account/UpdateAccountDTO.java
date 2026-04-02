package com.gzang.app.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 更新账户请求DTO
 *
 * @author G-Zang Team
 */
@Schema(description = "更新账户请求")
public class UpdateAccountDTO {

    @Schema(description = "账户名称", example = "我的银行卡")
    @Size(max = 100, message = "账户名称长度不能超过100")
    private String accountName;

    @Schema(description = "账户类型：1=现金, 2=银行卡, 3=电子支付", example = "1")
    @Min(value = 1, message = "账户类型值无效")
    @Max(value = 3, message = "账户类型值无效")
    private Integer accountType;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }
}
