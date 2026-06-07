package com.speakingpractice.partner.user.controller;

import com.speakingpractice.partner.common.result.Result;
import com.speakingpractice.partner.user.domain.dto.UserLoginDTO;
import com.speakingpractice.partner.user.domain.dto.UserRegisterDTO;
import com.speakingpractice.partner.user.domain.dto.UserUpdateDTO;
import com.speakingpractice.partner.user.domain.vo.UserVO;
import com.speakingpractice.partner.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户接口",description = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 用户信息
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        log.info("用户注册请求，用户名: {}", registerDTO.getUsername());

        try {
            UserVO userVO = userService.register(registerDTO);
            return Result.success("注册成功", userVO);
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 用户信息
     */
    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        log.info("用户登录请求，用户名: {}", loginDTO.getUsername());

        try {
            UserVO userVO = userService.login(loginDTO);
            return Result.success("登录成功", userVO);
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public Result<UserVO> getUserById(@PathVariable String userId) {
        log.info("查询用户信息请求，用户ID: {}", userId);

        try {
            UserVO userVO = userService.getUserById(userId);
            return Result.success(userVO);
        } catch (Exception e) {
            log.error("查询用户信息失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/username/{username}")
    public Result<UserVO> getUserByUsername(@PathVariable String username) {
        log.info("查询用户信息请求，用户名: {}", username);

        try {
            UserVO userVO = userService.getUserByUsername(username);
            return Result.success(userVO);
        } catch (Exception e) {
            log.error("查询用户信息失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     *
     * @param updateDTO 更新信息
     * @return 用户信息
     */
    @PutMapping("/info")
    public Result<UserVO> updateUser(@Valid @RequestBody UserUpdateDTO updateDTO) {
        try {
            UserVO userVO = userService.updateUser(updateDTO);
            return Result.success("更新成功", userVO);
        } catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

}