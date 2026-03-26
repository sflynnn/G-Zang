package com.gzang.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.User;
import com.gzang.app.mapper.UserMapper;
import com.gzang.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByUsername(String username) {
        return getBaseMapper().selectByUsername(username);
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existingUser = getUserByUsername(user.getUsername());
        if (existingUser != null) {
            log.warn("用户名已存在: {}", user.getUsername());
            return false;
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置默认状态
        user.setStatus(1);

        // 如果没有指定角色，默认设置为普通用户（假设角色ID为3）
        if (user.getRoleId() == null) {
            user.setRoleId(3L); // 普通用户角色ID
        }

        return save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            return null;
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("密码错误: {}", username);
            return null;
        }

        log.info("用户登录成功: {}", username);
        return user;
    }
}
