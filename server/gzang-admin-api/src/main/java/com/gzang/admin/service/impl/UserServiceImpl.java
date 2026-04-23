package com.gzang.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.dto.RegisterRequest;
import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.UserMapper;
import com.gzang.admin.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.gzang.app.constant.ErrorCode.USERNAME_EXISTS;

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
    public boolean register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existingUser = getUserByUsername(request.getUsername());
        if (existingUser != null) {
            log.warn("用户名已存在: {}", request.getUsername());
            throw new BusinessException(USERNAME_EXISTS, "用户名已存在");
        }

        // 构建用户实体（仅从 DTO 复制允许的字段）
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        // 设置默认状态
        user.setStatus(1);
        // 默认设置为普通用户角色（角色ID为3，可配置）
        user.setRoleId(3L);

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

    @Override
    public IPage<User> getUsersByCompanyId(IPage<User> page, Long companyId) {
        log.info("UserServiceImpl.getUsersByCompanyId called, companyId={}", companyId);
        try {
            IPage<User> result = getBaseMapper().selectPageByCompanyId((Page<User>) page, companyId);
            log.info("selectPageByCompanyId returned, total={}", result.getTotal());
            return result;
        } catch (Exception e) {
            log.error("Error in selectPageByCompanyId", e);
            throw e;
        }
    }
}
