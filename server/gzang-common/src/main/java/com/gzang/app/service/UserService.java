package com.gzang.app.service;

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

    /**
     * 发送重置密码验证码
     *
     * @param phone 手机号
     */
    void sendResetCode(String phone);

    /**
     * 验证重置密码验证码
     *
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证通过
     */
    boolean verifyResetCode(String phone, String code);

    /**
     * 重置密码
     *
     * @param phone        手机号
     * @param code         验证码
     * @param newPassword 新密码
     */
    void resetPassword(String phone, String code, String newPassword);

    /**
     * 发送绑定手机验证码
     *
     * @param phone 手机号
     */
    void sendBindCode(String phone);

    /**
     * 绑定手机号
     *
     * @param userId 用户ID
     * @param phone  手机号
     * @param code   验证码
     */
    void bindPhone(Long userId, String phone, String code);

    /**
     * 修改密码
     *
     * @param userId        用户ID
     * @param oldPassword  旧密码
     * @param newPassword  新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId   用户ID
     * @param nickname 昵称
     * @param avatar   头像
     */
    void updateUserInfo(Long userId, String nickname, String avatar);
}
