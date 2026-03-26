package com.gzang.app.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 *
 * @author G-Zang Team
 */
@Data
public class LoginRequest {

    /**
     * 用户名（手机号/邮箱）
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    // Explicit getters to ensure Lombok generates them properly
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
