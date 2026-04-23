package com.gzang.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 账本请求DTO
 */
@Data
public class BookRequest {

    @NotBlank(message = "账本名称不能为空")
    @Size(max = 50, message = "账本名称不能超过50个字符")
    private String name;

    private String icon;

    private String color;

    private String currency;

    private String currencySymbol;

    private String type;

    private Boolean isDefault;

    private String remark;
}
