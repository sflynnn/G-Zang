package com.gzang.app.service;

import com.gzang.app.dto.BookRequest;
import com.gzang.app.entity.Book;
import com.gzang.app.vo.BookStatisticsVO;
import com.gzang.app.vo.BookVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 账本服务接口
 */
public interface BookService extends IService<Book> {

    /**
     * 获取用户的账本列表
     */
    List<BookVO> getBooksByUserId(Long userId);

    /**
     * 获取账本详情
     */
    BookVO getBookById(Long bookId, Long userId);

    /**
     * 创建账本
     */
    BookVO createBook(BookRequest request, Long userId);

    /**
     * 更新账本
     */
    BookVO updateBook(Long bookId, BookRequest request, Long userId);

    /**
     * 删除账本
     */
    void deleteBook(Long bookId, Long userId);

    /**
     * 设置默认账本
     */
    void setDefaultBook(Long bookId, Long userId);

    /**
     * 获取账本统计
     */
    BookStatisticsVO getBookStatistics(Long bookId, Long userId);
}
