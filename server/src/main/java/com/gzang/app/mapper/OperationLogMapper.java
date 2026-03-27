package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper接口
 *
 * @author G-Zang Team
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
