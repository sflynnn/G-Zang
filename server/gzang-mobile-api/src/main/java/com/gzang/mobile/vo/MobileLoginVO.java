package com.gzang.mobile.vo;

import lombok.Data;

/**
 * 移动端登录响应视图对象
 *
 * @author G-Zang Team
 */
@Data
public class MobileLoginVO {
    /**
     * JWT Token
     */
    private String token;

    /**
     * 过期时间（毫秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private MobileUserVO user;
}
