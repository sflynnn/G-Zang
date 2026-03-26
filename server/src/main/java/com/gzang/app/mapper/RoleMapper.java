package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 *
 * @author G-Zang Team
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
