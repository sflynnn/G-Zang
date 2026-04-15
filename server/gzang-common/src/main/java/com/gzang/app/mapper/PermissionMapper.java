package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 *
 * @author G-Zang Team
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("SELECT p.* FROM t_permission p INNER JOIN t_role_permission rp ON p.id = rp.permission_id WHERE rp.role_id = #{roleId}")
    List<Permission> selectByRoleId(@Param("roleId") Long roleId);
}
