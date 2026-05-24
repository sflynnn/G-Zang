package com.gzang.mobile.controller;

import com.gzang.app.entity.Tag;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.TagMapper;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 移动端标签控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "移动端标签管理", description = "用户标签管理相关接口")
public class MobileTagController {

    private static final Logger log = LoggerFactory.getLogger(MobileTagController.class);

    private final TagMapper tagMapper;

    public MobileTagController(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    /**
     * 获取用户标签列表
     */
    @GetMapping
    @Operation(summary = "获取标签列表", description = "获取用户的所有标签")
    public Result<List<Tag>> getTags() {
        Long userId = TenantContextHolder.getUserId();
        List<Tag> tags = tagMapper.selectTagsByUserId(userId);
        return Result.success(tags);
    }

    /**
     * 获取常用标签
     */
    @GetMapping("/frequent")
    @Operation(summary = "获取常用标签", description = "获取用户常用的标签")
    public Result<List<Tag>> getFrequentTags(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") int limit) {
        Long userId = TenantContextHolder.getUserId();
        List<Tag> tags = tagMapper.selectFrequentTags(userId, limit);
        return Result.success(tags);
    }

    /**
     * 创建标签
     */
    @PostMapping
    @Operation(summary = "创建标签", description = "新增一个标签")
    public Result<Tag> createTag(@RequestBody CreateTagDTO dto) {
        Long userId = TenantContextHolder.getUserId();
        
        log.info("创建标签请求: userId={}, tagName={}", userId, dto.getTagName());

        // 检查标签是否已存在
        if (tagMapper.countByName(userId, dto.getTagName()) > 0) {
            throw new BusinessException(400, "标签已存在");
        }

        Tag tag = new Tag();
        tag.setUserId(userId);
        tag.setTagName(dto.getTagName());
        tag.setTagColor(dto.getTagColor() != null ? dto.getTagColor() : "#0F4C5C");
        tag.setUsageCount(0);
        tag.setIsFrequent(dto.getIsFrequent() != null ? dto.getIsFrequent() : 0);
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());

        tagMapper.insert(tag);
        
        log.info("标签创建成功: id={}", tag.getId());
        return Result.success(tag);
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新标签", description = "更新指定标签信息")
    public Result<Void> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @RequestBody UpdateTagDTO dto) {
        Long userId = TenantContextHolder.getUserId();
        
        log.info("更新标签请求: id={}, userId={}", id, userId);

        Tag tag = tagMapper.selectById(id);
        if (tag == null || !tag.getUserId().equals(userId)) {
            throw new BusinessException(400, "标签不存在或无权修改");
        }

        if (dto.getTagName() != null) {
            tag.setTagName(dto.getTagName());
        }
        if (dto.getTagColor() != null) {
            tag.setTagColor(dto.getTagColor());
        }
        if (dto.getIsFrequent() != null) {
            tag.setIsFrequent(dto.getIsFrequent());
        }
        tag.setUpdateTime(LocalDateTime.now());

        tagMapper.updateById(tag);
        
        log.info("标签更新成功: id={}", id);
        return Result.success();
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签", description = "删除指定标签")
    public Result<Void> deleteTag(@Parameter(description = "标签ID") @PathVariable Long id) {
        Long userId = TenantContextHolder.getUserId();
        
        log.info("删除标签请求: id={}, userId={}", id, userId);

        Tag tag = tagMapper.selectById(id);
        if (tag == null || !tag.getUserId().equals(userId)) {
            throw new BusinessException(400, "标签不存在或无权删除");
        }

        tagMapper.deleteById(id);
        
        log.info("标签删除成功: id={}", id);
        return Result.success();
    }

    /**
     * 增加标签使用次数
     */
    @PostMapping("/{id}/use")
    @Operation(summary = "使用标签", description = "记录标签使用，增加使用次数")
    @Transactional
    public Result<Void> useTag(@Parameter(description = "标签ID") @PathVariable Long id) {
        Long userId = TenantContextHolder.getUserId();
        
        Tag tag = tagMapper.selectById(id);
        if (tag == null || !tag.getUserId().equals(userId)) {
            throw new BusinessException(400, "标签不存在");
        }

        tagMapper.incrementUsageCount(id);
        
        // 如果使用次数超过阈值，自动标记为常用
        if (tag.getUsageCount() != null && tag.getUsageCount() >= 3 && (tag.getIsFrequent() == null || tag.getIsFrequent() == 0)) {
            tag.setIsFrequent(1);
            tagMapper.updateById(tag);
        }

        return Result.success();
    }

    // DTOs
    public static class CreateTagDTO {
        private String tagName;
        private String tagColor;
        private Integer isFrequent;

        public String getTagName() { return tagName; }
        public void setTagName(String tagName) { this.tagName = tagName; }
        public String getTagColor() { return tagColor; }
        public void setTagColor(String tagColor) { this.tagColor = tagColor; }
        public Integer getIsFrequent() { return isFrequent; }
        public void setIsFrequent(Integer isFrequent) { this.isFrequent = isFrequent; }
    }

    public static class UpdateTagDTO {
        private String tagName;
        private String tagColor;
        private Integer isFrequent;

        public String getTagName() { return tagName; }
        public void setTagName(String tagName) { this.tagName = tagName; }
        public String getTagColor() { return tagColor; }
        public void setTagColor(String tagColor) { this.tagColor = tagColor; }
        public Integer getIsFrequent() { return isFrequent; }
        public void setIsFrequent(Integer isFrequent) { this.isFrequent = isFrequent; }
    }
}
