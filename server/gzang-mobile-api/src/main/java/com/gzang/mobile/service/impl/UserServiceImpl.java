package com.gzang.mobile.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.dto.RegisterRequest;
import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.UserMapper;
import com.gzang.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static com.gzang.app.constant.ErrorCode.USERNAME_EXISTS;

/**
 * 用户服务实现类 - Mobile API 版本
 *
 * @author G-Zang Team
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final PasswordEncoder passwordEncoder;

    // 模拟验证码存储（生产环境应使用Redis）
    private static final Map<String, CodeInfo> VERIFICATION_CODES = new ConcurrentHashMap<>();

    private static class CodeInfo {
        String code;
        LocalDateTime expireTime;

        CodeInfo(String code, LocalDateTime expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByUsername(String username) {
        return getBaseMapper().selectByUsername(username);
    }

    @Override
    public boolean register(RegisterRequest request) {
        User existingUser = getUserByUsername(request.getUsername());
        if (existingUser != null) {
            log.warn("用户名已存在: {}", request.getUsername());
            throw new BusinessException(USERNAME_EXISTS, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(1);
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

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("密码错误: {}", username);
            return null;
        }

        log.info("用户登录成功: {}", username);
        return user;
    }

    @Override
    public IPage<User> getUsersByCompanyId(IPage<User> page, Long companyId) {
        return getBaseMapper().selectPageByCompanyId((Page<User>) page, companyId);
    }

    @Override
    public void sendResetCode(String phone) {
        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        // 5分钟有效期
        VERIFICATION_CODES.put("reset:" + phone, new CodeInfo(code, LocalDateTime.now().plusMinutes(5)));
        log.info("重置密码验证码已发送: phone={}, code={}", phone, code);
        // TODO: 实际发送短信验证码
    }

    @Override
    public boolean verifyResetCode(String phone, String code) {
        CodeInfo codeInfo = VERIFICATION_CODES.get("reset:" + phone);
        if (codeInfo == null) {
            log.warn("验证码不存在: phone={}", phone);
            return false;
        }
        if (LocalDateTime.now().isAfter(codeInfo.expireTime)) {
            log.warn("验证码已过期: phone={}", phone);
            VERIFICATION_CODES.remove("reset:" + phone);
            return false;
        }
        if (!codeInfo.code.equals(code)) {
            log.warn("验证码错误: phone={}, expected={}, actual={}", phone, codeInfo.code, code);
            return false;
        }
        return true;
    }

    @Override
    public void resetPassword(String phone, String code, String newPassword) {
        if (!verifyResetCode(phone, code)) {
            throw new BusinessException(400, "验证码错误或已过期");
        }
        // 根据手机号查询用户（需要手机号字段）
        User user = getBaseMapper().selectByUsername(phone);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        // 删除验证码
        VERIFICATION_CODES.remove("reset:" + phone);
        log.info("密码重置成功: phone={}", phone);
    }

    @Override
    public void sendBindCode(String phone) {
        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        // 5分钟有效期
        VERIFICATION_CODES.put("bind:" + phone, new CodeInfo(code, LocalDateTime.now().plusMinutes(5)));
        log.info("绑定手机验证码已发送: phone={}, code={}", phone, code);
        // TODO: 实际发送短信验证码
    }

    @Override
    public void bindPhone(Long userId, String phone, String code) {
        CodeInfo codeInfo = VERIFICATION_CODES.get("bind:" + phone);
        if (codeInfo == null) {
            throw new BusinessException(400, "验证码不存在");
        }
        if (LocalDateTime.now().isAfter(codeInfo.expireTime)) {
            VERIFICATION_CODES.remove("bind:" + phone);
            throw new BusinessException(400, "验证码已过期");
        }
        if (!codeInfo.code.equals(code)) {
            throw new BusinessException(400, "验证码错误");
        }
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setUsername(phone);
        updateById(user);
        VERIFICATION_CODES.remove("bind:" + phone);
        log.info("手机号绑定成功: userId={}, phone={}", userId, phone);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        log.info("密码修改成功: userId={}", userId);
    }

    @Override
    public User getUserInfo(Long userId) {
        return getById(userId);
    }

    @Override
    public void updateUserInfo(Long userId, String nickname, String avatar) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        updateById(user);
        log.info("用户信息更新成功: userId={}", userId);
    }
}
