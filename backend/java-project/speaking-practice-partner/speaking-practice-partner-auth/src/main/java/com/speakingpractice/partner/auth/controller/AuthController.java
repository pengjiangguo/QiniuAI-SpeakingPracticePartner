package com.speakingpractice.partner.auth.controller;

import com.speakingpractice.partner.auth.domain.dto.LoginDTO;
import com.speakingpractice.partner.auth.domain.dto.RegisterDTO;
import com.speakingpractice.partner.auth.domain.vo.CaptchaVO;
import com.speakingpractice.partner.auth.domain.vo.LoginVO;
import com.speakingpractice.partner.auth.domain.vo.UserInfoVO;
import com.speakingpractice.partner.auth.service.AuthService;
import com.speakingpractice.partner.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器（使用 Sa-Token）
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "认证接口", description = "用户认证相关接口（Sa-Token）")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 生成验证码
     *
     * @return 验证码信息
     */
    @GetMapping("/captcha")
    @Operation(summary = "生成验证码", description = "生成验证码图片和key")
    public Result<CaptchaVO> generateCaptcha() {
        log.info("生成验证码请求");
        CaptchaVO captchaVO = authService.generateCaptcha();
        return Result.success(captchaVO);
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回Sa-Token")
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
    @Operation(summary = "用户注册", description = "用户注册接口，返回Sa-Token")
    public Result<LoginVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("用户注册请求，用户名: {}", registerDTO.getUsername());
        LoginVO loginVO = authService.register(registerDTO);
        return Result.success(loginVO);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/user/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户信息")
    public Result<UserInfoVO> getUserInfo() {
        log.info("获取用户信息请求");
        UserInfoVO userInfoVO = authService.getUserInfo();
        return Result.success(userInfoVO);
    }

    /**
     * 退出登录
     *
     * @return 成功响应
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "退出登录")
    public Result<Void> logout() {
        log.info("退出登录请求");
        authService.logout();
        return Result.success();
    }

    /**
     * 检查是否登录
     *
     * @return 是否登录
     */
    @GetMapping("/is-login")
    @Operation(summary = "检查是否登录", description = "检查当前用户是否登录")
    public Result<Boolean> isLogin() {
        log.info("检查是否登录请求");
        boolean isLogin = authService.isLogin();
        return Result.success(isLogin);
    }

    /**
     * 获取 Token 信息
     *
     * @return Token 信息
     */
    @GetMapping("/token/info")
    @Operation(summary = "获取Token信息", description = "获取当前登录用户的Token信息")
    public Result<LoginVO> getTokenInfo() {
        log.info("获取Token信息请求");
        LoginVO loginVO = authService.getTokenInfo();
        return Result.success(loginVO);
    }

}