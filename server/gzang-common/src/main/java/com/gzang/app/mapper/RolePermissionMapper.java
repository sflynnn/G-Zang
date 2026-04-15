package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色权限关联Mapper接口
 *
 * @author G-Zang Team
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 批量插入角色权限关联
     */
    @Insert("<script>" +
            "INSERT INTO t_role_permission (role_id, permission_id) VALUES " +
            "<foreach collection='permissionIds' item='pid' separator=','>" +
            "(#{roleId}, #{pid})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") Long[] permissionIds);

    /**
     * 根据角色ID删除所有权限关联
     */
    @Delete("DELETE FROM t_role_permission WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);
}
