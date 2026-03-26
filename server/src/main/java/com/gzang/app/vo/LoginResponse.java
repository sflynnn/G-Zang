package com.gzang.app.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应VO
 *
 * @author G-Zang Team
 */
@Data
@NoArgsConstructor
public class LoginResponse {

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 公司ID（个人用户为null）
     */
    private Long companyId;

    // Manually add setter methods since Lombok isn't working properly in the IDE
    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
