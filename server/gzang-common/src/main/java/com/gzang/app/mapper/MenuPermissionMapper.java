package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.MenuPermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单权限关联Mapper接口
 *
 * @author G-Zang Team
 */
@Mapper
public interface MenuPermissionMapper extends BaseMapper<MenuPermission> {

    /**
     * 根据菜单ID查询权限ID列表
     */
    List<Long> selectPermissionIdsByMenuId(@Param("menuId") Long menuId);

    /**
     * 根据菜单ID删除所有权限关联
     */
    @Delete("DELETE FROM t_menu_permission WHERE menu_id = #{menuId}")
    int deletePermissionsByMenuId(@Param("menuId") Long menuId);

    /**
     * 批量插入菜单权限关联
     */
    @Insert("<script>" +
            "INSERT INTO t_menu_permission (menu_id, permission_id) VALUES " +
            "<foreach collection='permissionIds' item='pid' separator=','>" +
            "(#{menuId}, #{pid})" +
            "</foreach>" +
            "</script>")
    int batchInsertPermissions(@Param("menuId") Long menuId, @Param("permissionIds") Long[] permissionIds);
}
