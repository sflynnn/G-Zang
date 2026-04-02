package com.gzang.app.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * 更新分类请求DTO
 *
 * @author G-Zang Team
 */
@Schema(description = "更新分类请求")
public class UpdateCategoryDTO {

    @Schema(description = "分类名称", example = "餐饮")
    @Size(max = 50, message = "分类名称长度不能超过50")
    private String categoryName;

    @Schema(description = "父分类ID（顶级分类为null）", example = "1")
    private Long parentId;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
