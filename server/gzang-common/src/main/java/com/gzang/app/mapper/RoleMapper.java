package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 角色Mapper接口
 *
 * @author G-Zang Team
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 通过角色ID查询角色（显式SQL，绕过MP自动列检测）
     */
    @Select("SELECT id, role_name AS roleName, role_code AS roleCode, description, role_type AS roleType, " +
            "data_scope AS dataScope, company_id AS companyId, is_default AS isDefault, " +
            "create_time AS createTime, update_time AS updateTime FROM t_role WHERE id = #{id}")
    Role selectRoleById(Long id);
}
