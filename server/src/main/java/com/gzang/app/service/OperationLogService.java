package com.gzang.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.entity.OperationLog;
import com.gzang.app.vo.OperationLogVO;

/**
 * 操作日志服务接口
 *
 * @author G-Zang Team
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 记录操作日志
     */
    void logOperation(OperationLog log);

    /**
     * 分页查询操作日志
     */
    Object pageQuery(Integer current, Integer size, String action, String targetType, Long userId);
}
