package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.RoleDataScope;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色数据范围Mapper接口（行级权限预留）
 *
 * @author G-Zang Team
 */
@Mapper
public interface RoleDataScopeMapper extends BaseMapper<RoleDataScope> {
}
