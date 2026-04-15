package com.gzang.admin.vo;

import com.gzang.app.vo.AdminUserVO;
import lombok.Data;

import java.util.List;

/**
 * 管理端登录响应视图对象
 *
 * @author G-Zang Team
 */
@Data
public class AdminLoginVO {
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
    private AdminUserVO user;

    /**
     * 权限列表
     */
    private List<String> permissions;
}
