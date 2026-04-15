package com.gzang.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    IPage<OperationLogVO> pageQuery(Page<OperationLogVO> page, String startTime, String endTime, String operator, String action);

    /**
     * 根据ID获取操作日志详情
     */
    OperationLogVO getOperationLogById(Long id);
}
