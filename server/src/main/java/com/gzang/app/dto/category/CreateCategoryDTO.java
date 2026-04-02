package com.gzang.app.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * 创建分类请求DTO
 *
 * @author G-Zang Team
 */
@Schema(description = "创建分类请求")
public class CreateCategoryDTO {

    @Schema(description = "分类名称", example = "餐饮")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50")
    private String categoryName;

    @Schema(description = "分类类型：1=收入, 2=支出", example = "2")
    @NotNull(message = "分类类型不能为空")
    @Min(value = 1, message = "分类类型值无效")
    @Max(value = 2, message = "分类类型值无效")
    private Integer type;

    @Schema(description = "父分类ID（顶级分类为null）", example = "1")
    private Long parentId;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
