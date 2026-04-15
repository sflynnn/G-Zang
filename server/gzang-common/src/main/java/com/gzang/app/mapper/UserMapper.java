package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 *
 * @author G-Zang Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM t_user WHERE username = #{username} AND status = 1")
    User selectByUsername(@Param("username") String username);

    /**
     * 按公司ID分页查询用户（仅返回本公司用户，支持超级管理员查看全部）
     *
     * @param page      分页对象
     * @param companyId 公司ID（超级管理员可传null查看全部）
     * @return 分页用户列表
     */
    IPage<User> selectPageByCompanyId(Page<User> page, @Param("companyId") Long companyId);
}
