package com.gzang.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.dto.BookRequest;
import com.gzang.app.entity.Book;
import com.gzang.app.entity.Transaction;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.BookMapper;
import com.gzang.app.mapper.TransactionMapper;
import com.gzang.app.service.BookService;
import com.gzang.app.vo.BookStatisticsVO;
import com.gzang.app.vo.BookVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账本服务实现
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    private final TransactionMapper transactionMapper;

    public BookServiceImpl(TransactionMapper transactionMapper) {
        this.transactionMapper = transactionMapper;
    }

    @Override
    public List<BookVO> getBooksByUserId(Long userId) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getUserId, userId);
        wrapper.orderByDesc(Book::getIsDefault)
               .orderByDesc(Book::getCreateTime);
        
        List<Book> books = list(wrapper);
        return books.stream().map(this::convertToVO).toList();
    }

    @Override
    public BookVO getBookById(Long bookId, Long userId) {
        Book book = getBookByUserIdAndId(bookId, userId);
        return convertToVO(book);
    }

    @Override
    @Transactional
    public BookVO createBook(BookRequest request, Long userId) {
        // 如果设置为默认账本，先取消其他默认
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            cancelDefaultBooks(userId);
        }
        
        Book book = new Book();
        BeanUtils.copyProperties(request, book);
        book.setUserId(userId);
        
        // 设置默认值
        if (book.getIcon() == null) {
            book.setIcon("📒");
        }
        if (book.getColor() == null) {
            book.setColor("#0F4C5C");
        }
        if (book.getCurrency() == null) {
            book.setCurrency("CNY");
        }
        if (book.getCurrencySymbol() == null) {
            book.setCurrencySymbol("¥");
        }
        if (book.getType() == null) {
            book.setType("PERSONAL");
        }
        if (book.getIsDefault() == null) {
            book.setIsDefault(false);
        }
        
        save(book);
        return convertToVO(book);
    }

    @Override
    @Transactional
    public BookVO updateBook(Long bookId, BookRequest request, Long userId) {
        Book book = getBookByUserIdAndId(bookId, userId);
        
        // 如果设置为默认账本，先取消其他默认
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            cancelDefaultBooks(userId);
        }
        
        BeanUtils.copyProperties(request, book, "id", "userId", "createTime");
        updateById(book);
        
        return convertToVO(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId, Long userId) {
        Book book = getBookByUserIdAndId(bookId, userId);
        
        // 如果删除的是默认账本，需要设置一个新的默认账本
        if (Boolean.TRUE.equals(book.getIsDefault())) {
            LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Book::getUserId, userId)
                   .ne(Book::getId, bookId)
                   .orderByDesc(Book::getCreateTime)
                   .last("LIMIT 1");
            
            Book newDefault = getOne(wrapper);
            if (newDefault != null) {
                newDefault.setIsDefault(true);
                updateById(newDefault);
            }
        }
        
        removeById(bookId);
    }

    @Override
    @Transactional
    public void setDefaultBook(Long bookId, Long userId) {
        Book book = getBookByUserIdAndId(bookId, userId);
        
        // 取消其他默认账本
        cancelDefaultBooks(userId);
        
        // 设置新的默认账本
        book.setIsDefault(true);
        updateById(book);
    }

    @Override
    public BookStatisticsVO getBookStatistics(Long bookId, Long userId) {
        // 验证权限
        getBookByUserIdAndId(bookId, userId);
        
        // 统计收入
        LambdaQueryWrapper<Transaction> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.eq(Transaction::getBookId, bookId)
                     .eq(Transaction::getType, 1); // 收入
        
        BigDecimal totalIncome = transactionMapper.selectList(incomeWrapper)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 统计支出
        LambdaQueryWrapper<Transaction> expenseWrapper = new LambdaQueryWrapper<>();
        expenseWrapper.eq(Transaction::getBookId, bookId)
                      .eq(Transaction::getType, 2); // 支出
        
        BigDecimal totalExpense = transactionMapper.selectList(expenseWrapper)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 统计交易数量
        LambdaQueryWrapper<Transaction> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Transaction::getBookId, bookId);
        long transactionCount = transactionMapper.selectCount(countWrapper);
        
        BookStatisticsVO stats = new BookStatisticsVO();
        stats.setBookId(bookId);
        stats.setTotalIncome(totalIncome);
        stats.setTotalExpense(totalExpense);
        stats.setBalance(totalIncome.subtract(totalExpense));
        stats.setTransactionCount((int) transactionCount);
        
        return stats;
    }

    private Book getBookByUserIdAndId(Long bookId, Long userId) {
        Book book = getById(bookId);
        if (book == null) {
            throw new BusinessException(404, "账本不存在");
        }
        if (!userId.equals(book.getUserId())) {
            throw new BusinessException(403, "无权访问该账本");
        }
        return book;
    }

    private void cancelDefaultBooks(Long userId) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getUserId, userId)
               .eq(Book::getIsDefault, true);
        
        List<Book> defaultBooks = list(wrapper);
        for (Book book : defaultBooks) {
            book.setIsDefault(false);
        }
        updateBatchById(defaultBooks);
    }

    private BookVO convertToVO(Book book) {
        if (book == null) {
            return null;
        }
        
        BookVO vo = new BookVO();
        BeanUtils.copyProperties(book, vo);
        return vo;
    }
}
