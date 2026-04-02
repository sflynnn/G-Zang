package com.gzang.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户注册请求DTO
 * <p>
 * 仅包含注册所需的字段，与 User 实体完全隔离，避免字段污染和安全风险。
 * </p>
 *
 * @author G-Zang Team
 */
@Schema(description = "用户注册请求")
public class RegisterRequest {

    @Schema(description = "用户名（手机号或邮箱）", example = "john@example.com")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 64, message = "用户名长度必须在3-64位之间")
    private String username;

    @Schema(description = "密码", example = "Aa123456!")
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 128, message = "密码长度必须在8-128位之间")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_.#^])[A-Za-z\\d@$!%*?&_.#^]{8,}$",
            message = "密码必须包含大小写字母、数字和特殊字符（@$!%*?&_.#^）"
    )
    private String password;

    @Schema(description = "用户昵称", example = "约翰")
    @Size(max = 64, message = "昵称长度不能超过64位")
    private String nickname;

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
