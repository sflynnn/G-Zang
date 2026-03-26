package com.gzang.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
     * @param user 用户信息
     * @return 是否注册成功
     */
    boolean register(User user);

    /**
     * 用户登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户信息，验证失败返回null
     */
    User login(String username, String password);
}
