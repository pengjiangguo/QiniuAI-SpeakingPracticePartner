package com.speakingpractice.partner.auth.controller;

import com.speakingpractice.partner.auth.domain.dto.LoginDTO;
import com.speakingpractice.partner.auth.domain.dto.RegisterDTO;
import com.speakingpractice.partner.auth.domain.vo.LoginVO;
import com.speakingpractice.partner.auth.domain.vo.UserInfoVO;
import com.speakingpractice.partner.auth.service.AuthService;
import com.speakingpractice.partner.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "认证接口", description = "用户认证相关接口")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT令牌")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求，用户名: {}", loginDTO.getUsername());
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success(loginVO);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 登录响应
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口，返回JWT令牌")
    public Result<LoginVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("用户注册请求，用户名: {}", registerDTO.getUsername());
        LoginVO loginVO = authService.register(registerDTO);
        return Result.success(loginVO);
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public Result<LoginVO> refreshToken(
            @Parameter(description = "刷新令牌") @RequestParam String refreshToken) {
        log.info("刷新令牌请求");
        LoginVO loginVO = authService.refreshToken(refreshToken);
        return Result.success(loginVO);
    }

    /**
     * 获取用户信息
     *
     * @param token 访问令牌
     * @return 用户信息
     */
    @GetMapping("/user/info")
    @Operation(summary = "获取用户信息", description = "根据访问令牌获取用户信息")
    public Result<UserInfoVO> getUserInfo(
            @Parameter(description = "访问令牌") @RequestParam String token) {
        log.info("获取用户信息请求");
        UserInfoVO userInfoVO = authService.getUserInfo(token);
        return Result.success(userInfoVO);
    }

    /**
     * 验证令牌
     *
     * @param token 访问令牌
     * @return 是否有效
     */
    @GetMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证访问令牌是否有效")
    public Result<Boolean> validateToken(
            @Parameter(description = "访问令牌") @RequestParam String token) {
        log.info("验证令牌请求");
        boolean isValid = authService.validateToken(token);
        return Result.success(isValid);
    }

}