package com.gzang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.OperationLog;
import com.gzang.app.mapper.OperationLogMapper;
import com.gzang.admin.service.OperationLogService;
import com.gzang.app.vo.OperationLogVO;
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
    public IPage<OperationLogVO> pageQuery(Page<OperationLogVO> page, String startTime, String endTime, String operator, String action) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OperationLog::getCreateTime);

        if (action != null && !action.isEmpty()) {
            wrapper.eq(OperationLog::getAction, action);
        }
        if (operator != null && !operator.isEmpty()) {
            wrapper.eq(OperationLog::getUsername, operator);
        }

        IPage<OperationLog> resultPage = baseMapper.selectPage(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        return resultPage.convert(this::convertToVO);
    }

    @Override
    public OperationLogVO getOperationLogById(Long id) {
        OperationLog log = getById(id);
        if (log == null) {
            return null;
        }
        return convertToVO(log);
    }

    private OperationLogVO convertToVO(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(log.getId());
        vo.setUserId(log.getUserId());
        vo.setUsername(log.getUsername());
        vo.setCompanyId(log.getCompanyId());
        vo.setAction(log.getAction());
        vo.setTargetType(log.getTargetType());
        vo.setTargetId(log.getTargetId());
        vo.setTargetName(log.getTargetName());
        vo.setDetail(log.getDetail());
        vo.setIpAddress(log.getIpAddress());
        vo.setUserAgent(log.getUserAgent());
        vo.setRequestUri(log.getRequestUri());
        vo.setRequestMethod(log.getRequestMethod());
        vo.setResponseStatus(log.getResponseStatus());
        vo.setErrorMessage(log.getErrorMessage());
        vo.setDurationMs(log.getDurationMs());
        vo.setCreateTime(log.getCreateTime());
        return vo;
    }
}
