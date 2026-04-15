package com.gzang.mobile.vo;

import lombok.Data;

/**
 * 移动端用户视图对象
 *
 * @author G-Zang Team
 */
@Data
public class MobileUserVO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 状态
     */
    private Integer status;
}
