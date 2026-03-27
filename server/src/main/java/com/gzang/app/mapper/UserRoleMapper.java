package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 *
 * @author G-Zang Team
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
