package com.gzang.app.controller;

import com.gzang.app.dto.LoginRequest;
import com.gzang.app.dto.RegisterRequest;
import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.UserService;
import com.gzang.app.service.RoleService;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.vo.AdminUserVO;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;
import static com.gzang.app.constant.ErrorCode.LOGIN_FAILED;

/**
 * 认证控制器
 * 处理用户登录、注册等认证相关操作
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证管理", description = "用户登录、注册等认证接口")
public class AuthController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final RoleService roleService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, RoleService roleService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.roleService = roleService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录（返回完整用户信息，含角色和权限）
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT Token及完整用户信息")
    public Result<AdminUserVO> login(@Validated @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求: {}", loginRequest.getUsername());

        // 验证用户登录
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            throw new BusinessException(LOGIN_FAILED, "用户名或密码错误");
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleId(), user.getCompanyId());

        // 构建完整用户信息
        AdminUserVO userVO = buildAdminUserVO(user, token);

        log.info("用户登录成功: {}", user.getUsername());
        return Result.success("登录成功", userVO);
    }

    /**
     * 获取当前用户完整信息（含角色和权限）
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的完整信息，包含角色编码和权限码列表")
    public Result<AdminUserVO> getCurrentUser(Principal principal) {
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        User user = userService.getById(userId);

        if (user == null) {
            throw new BusinessException(DATA_NOT_FOUND, "用户不存在");
        }

        AdminUserVO userVO = buildAdminUserVO(user, null);
        return Result.success(userVO);
    }

    /**
     * 构建管理端用户完整信息
     */
    private AdminUserVO buildAdminUserVO(User user, String token) {
        AdminUserVO vo = new AdminUserVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRoleId(user.getRoleId());
        vo.setCompanyId(user.getCompanyId());
        vo.setStatus(user.getStatus());
        if (token != null) {
            vo.setToken(token);
        }

        log.info("[buildAdminUserVO] userId={}, roleId={}", user.getId(), user.getRoleId());

        // 填充角色信息
        if (user.getRoleId() != null) {
            try {
                var role = roleService.findRoleById(user.getRoleId());
                log.info("[buildAdminUserVO] role lookup result: {}", role);
                if (role != null) {
                    vo.setRoleCode(role.getRoleCode());
                    vo.setRoleName(role.getRoleName());
                    log.info("[buildAdminUserVO] roleCode={}, roleName={}", role.getRoleCode(), role.getRoleName());
                }
                // 填充权限码列表
                List<String> permissionCodes = roleService.getRolePermissionCodes(user.getRoleId());
                log.info("[buildAdminUserVO] permissionCodes count={}: {}", permissionCodes.size(), permissionCodes);
                vo.setPermissions(permissionCodes);
            } catch (Exception e) {
                log.error("[buildAdminUserVO] 获取角色信息失败, userId={}, roleId={}: {}", user.getId(), user.getRoleId(), e.getMessage(), e);
                vo.setPermissions(Collections.emptyList());
            }
        } else {
            vo.setPermissions(Collections.emptyList());
        }

        return vo;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result<Void> register(@Validated @RequestBody RegisterRequest request) {
        log.info("用户注册请求: {}", request.getUsername());
        // BusinessException（用户名已存在）由 GlobalExceptionHandler 统一处理
        userService.register(request);
        log.info("用户注册成功: {}", request.getUsername());
        return Result.success();
    }
}
