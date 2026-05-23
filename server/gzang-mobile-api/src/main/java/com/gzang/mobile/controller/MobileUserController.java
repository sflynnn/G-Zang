package com.gzang.mobile.controller;

import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.UserService;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.vo.Result;
import com.gzang.mobile.vo.MobileUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 移动端用户控制器
 * 用户信息、绑定手机、修改密码等接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/user")
@Tag(name = "移动端用户管理", description = "用户信息、绑定手机、修改密码等接口")
public class MobileUserController {

    private static final Logger log = LoggerFactory.getLogger(MobileUserController.class);

    private final UserService userService;

    public MobileUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 发送绑定手机验证码
     * POST /api/mobile/user/send-bind-code
     */
    @PostMapping("/send-bind-code")
    @Operation(summary = "发送绑定手机验证码", description = "向指定手机号发送绑定验证码")
    public Result<Void> sendBindCode(@RequestParam String phone) {
        Long userId = TenantContextHolder.getUserId();
        log.info("发送绑定手机验证码请求: userId={}, phone={}", userId, phone);
        userService.sendBindCode(phone);
        return Result.successWithMessage("验证码已发送");
    }

    /**
     * 绑定手机号
     * POST /api/mobile/user/bind-phone
     */
    @PostMapping("/bind-phone")
    @Operation(summary = "绑定手机号", description = "使用验证码绑定手机号")
    public Result<Void> bindPhone(
            @RequestParam String phone,
            @RequestParam String code) {
        Long userId = TenantContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        log.info("绑定手机号请求: userId={}, phone={}", userId, phone);
        userService.bindPhone(userId, phone, code);
        return Result.successWithMessage("手机号绑定成功");
    }

    /**
     * 修改密码
     * POST /api/mobile/user/change-password
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    public Result<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        Long userId = TenantContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        log.info("修改密码请求: userId={}", userId);
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.successWithMessage("密码修改成功");
    }

    /**
     * 获取用户信息
     * GET /api/mobile/user/info
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的信息")
    public Result<MobileUserVO> getUserInfo() {
        Long userId = TenantContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        User user = userService.getUserInfo(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return Result.success(convertToMobileUserVO(user));
    }

    /**
     * 更新用户信息
     * PUT /api/mobile/user/info
     */
    @PutMapping("/info")
    @Operation(summary = "更新用户信息", description = "更新昵称、头像等信息")
    public Result<Void> updateUserInfo(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String avatar) {
        Long userId = TenantContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        log.info("更新用户信息请求: userId={}, nickname={}", userId, nickname);
        userService.updateUserInfo(userId, nickname, avatar);
        return Result.successWithMessage("用户信息更新成功");
    }

    /**
     * 转换用户信息
     */
    private MobileUserVO convertToMobileUserVO(User user) {
        MobileUserVO vo = new MobileUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setCompanyId(user.getCompanyId());
        vo.setStatus(user.getStatus());
        return vo;
    }
}
