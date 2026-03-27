package com.gzang.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.OperationLog;
import com.gzang.app.mapper.OperationLogMapper;
import com.gzang.app.service.OperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    @Async
    public void logOperation(OperationLog log) {
        save(log);
    }

    @Override
    public Object pageQuery(Integer current, Integer size, String action, String targetType, Long userId) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OperationLog::getCreateTime);

        if (action != null && !action.isEmpty()) {
            wrapper.eq(OperationLog::getAction, action);
        }
        if (targetType != null && !targetType.isEmpty()) {
            wrapper.eq(OperationLog::getTargetType, targetType);
        }
        if (userId != null) {
            wrapper.eq(OperationLog::getUserId, userId);
        }

        Page<OperationLog> page = new Page<>(current, size);
        Page<OperationLog> result = page(page, wrapper);
        return result;
    }
}
