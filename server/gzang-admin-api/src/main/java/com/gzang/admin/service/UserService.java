package com.gzang.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.dto.RegisterRequest;
import com.gzang.app.entity.User;

/**
 * 用户服务接口
 *
 * @author G-Zang Team
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 用户注册
     *
     * @param request 注册请求（不含敏感字段）
     * @return 是否注册成功
     */
    boolean register(RegisterRequest request);

    /**
     * 用户登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户信息，验证失败返回null
     */
    User login(String username, String password);

    /**
     * 按公司ID分页查询用户（仅返回本公司用户）
     *
     * @param page      分页对象
     * @param companyId 公司ID（超级管理员传null可查看全部）
     * @return 分页用户列表
     */
    IPage<User> getUsersByCompanyId(IPage<User> page, Long companyId);
}
