package com.gzang.mobile.controller;

import com.gzang.app.dto.BookRequest;
import com.gzang.app.service.BookService;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.vo.BookStatisticsVO;
import com.gzang.app.vo.BookVO;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 移动端账本控制器
 */
@RestController
@RequestMapping("/api/mobile/books")
@Tag(name = "移动端账本", description = "移动端账本管理接口")
public class MobileBookController {

    private final BookService bookService;

    public MobileBookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * 获取账本列表
     */
    @GetMapping
    @Operation(summary = "获取账本列表", description = "获取当前用户的账本列表")
    public Result<List<BookVO>> getBooks() {
        Long userId = TenantContextHolder.getUserId();
        List<BookVO> books = bookService.getBooksByUserId(userId);
        return Result.success(books);
    }

    /**
     * 获取账本详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取账本详情", description = "获取指定账本的详情")
    public Result<BookVO> getBook(@PathVariable Long id) {
        Long userId = TenantContextHolder.getUserId();
        BookVO book = bookService.getBookById(id, userId);
        return Result.success(book);
    }

    /**
     * 创建账本
     */
    @PostMapping
    @Operation(summary = "创建账本", description = "创建新的账本")
    public Result<BookVO> createBook(@Valid @RequestBody BookRequest request) {
        Long userId = TenantContextHolder.getUserId();
        BookVO book = bookService.createBook(request, userId);
        return Result.success(book);
    }

    /**
     * 更新账本
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新账本", description = "更新指定账本")
    public Result<BookVO> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        Long userId = TenantContextHolder.getUserId();
        BookVO book = bookService.updateBook(id, request, userId);
        return Result.success(book);
    }

    /**
     * 删除账本
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除账本", description = "删除指定账本")
    public Result<Void> deleteBook(@PathVariable Long id) {
        Long userId = TenantContextHolder.getUserId();
        bookService.deleteBook(id, userId);
        return Result.success();
    }

    /**
     * 设置默认账本
     */
    @PutMapping("/{id}/default")
    @Operation(summary = "设置默认账本", description = "将指定账本设为默认")
    public Result<Void> setDefaultBook(@PathVariable Long id) {
        Long userId = TenantContextHolder.getUserId();
        bookService.setDefaultBook(id, userId);
        return Result.success();
    }

    /**
     * 获取账本统计
     */
    @GetMapping("/{id}/statistics")
    @Operation(summary = "获取账本统计", description = "获取指定账本的统计信息")
    public Result<BookStatisticsVO> getBookStatistics(
            @PathVariable Long id,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Long userId = TenantContextHolder.getUserId();
        BookStatisticsVO stats = bookService.getBookStatistics(id, userId);
        return Result.success(stats);
    }
}
