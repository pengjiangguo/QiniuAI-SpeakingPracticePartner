package com.speakingpractice.partner.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.speakingpractice.partner.auth.domain.dto.LoginDTO;
import com.speakingpractice.partner.auth.domain.dto.RegisterDTO;
import com.speakingpractice.partner.auth.domain.vo.CaptchaVO;
import com.speakingpractice.partner.auth.domain.vo.LoginVO;
import com.speakingpractice.partner.auth.domain.vo.UserInfoVO;
import com.speakingpractice.partner.common.enums.ResultCode;
import com.speakingpractice.partner.common.exception.BusinessException;
import com.speakingpractice.partner.common.util.CaptchaUtil;
import com.speakingpractice.partner.user.domain.entity.User;
import com.speakingpractice.partner.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现（使用 Sa-Token）
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CaptchaUtil captchaUtil;

    /**
     * 生成验证码
     *
     * @return 验证码信息
     */
    public CaptchaVO generateCaptcha() {
        log.info("生成验证码开始");

        CaptchaUtil.CaptchaInfo captchaInfo = captchaUtil.generateCaptcha();

        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaKey(captchaInfo.getCaptchaKey());
        captchaVO.setCaptchaImage(captchaInfo.getCaptchaImage());

        log.info("生成验证码成功，key: {}", captchaInfo.getCaptchaKey());
        return captchaVO;
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录响应
     */
    public LoginVO login(LoginDTO loginDTO) {
        log.info("用户登录开始，用户名: {}", loginDTO.getUsername());

        // 1. 验证验证码
        boolean captchaValid = captchaUtil.validateCaptcha(loginDTO.getCaptchaKey(), loginDTO.getCaptchaCode());
        if (!captchaValid) {
            log.warn("验证码验证失败，用户名: {}", loginDTO.getUsername());
            throw new BusinessException("验证码错误或已过期");
        }

        // 2. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        queryWrapper.eq(User::getDeleted, 0);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("用户不存在: {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.USER_NOT_EXISTS);
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            log.warn("用户已禁用: {}", loginDTO.getUsername());
            throw new BusinessException("用户已禁用，请联系管理员");
        }

        // 4. 验证密码（使用 Hutool 的 BCrypt）
        boolean passwordMatches = BCrypt.checkpw(loginDTO.getPassword(), user.getPassword());
        if (!passwordMatches) {
            log.warn("密码错误，用户名: {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 5. 使用 Sa-Token 登录
        StpUtil.login(user.getId());

        // 6. 返回登录信息
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setTokenName(StpUtil.getTokenName());
        loginVO.setTokenValue(StpUtil.getTokenValue());

        log.info("用户登录成功，用户ID: {}, token: {}", user.getId(), StpUtil.getTokenValue());
        return loginVO;
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 登录响应
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterDTO registerDTO) {
        log.info("用户注册开始，用户名: {}", registerDTO.getUsername());

        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerDTO.getUsername());
        queryWrapper.eq(User::getDeleted, 0);
        User existUser = userMapper.selectOne(queryWrapper);

        if (existUser != null) {
            log.warn("用户名已存在: {}", registerDTO.getUsername());
            throw new BusinessException(ResultCode.USER_EXISTS);
        }

        // 2. 创建用户实体
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);

        // 3. 加密密码（使用 Hutool 的 BCrypt）
        String encodedPassword = BCrypt.hashpw(registerDTO.getPassword());
        user.setPassword(encodedPassword);

        // 4. 设置默认值
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(registerDTO.getUsername());
        }
        if (user.getNativeLanguage() == null || user.getNativeLanguage().isEmpty()) {
            user.setNativeLanguage("zh-CN");
        }
        if (user.getEnglishLevel() == null || user.getEnglishLevel().isEmpty()) {
            user.setEnglishLevel("B1");
        }
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 5. 保存用户
        int result = userMapper.insert(user);
        if (result <= 0) {
            log.error("用户注册失败，用户名: {}", registerDTO.getUsername());
            throw new BusinessException("用户注册失败");
        }

        // 6. 使用 Sa-Token 登录
        StpUtil.login(user.getId());

        // 7. 返回登录信息
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setTokenName(StpUtil.getTokenName());
        loginVO.setTokenValue(StpUtil.getTokenValue());

        log.info("用户注册成功，用户ID: {}, token: {}", user.getId(), StpUtil.getTokenValue());
        return loginVO;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public UserInfoVO getUserInfo() {
        log.info("获取用户信息开始");

        // 1. 从 Sa-Token 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();

        // 2. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        queryWrapper.eq(User::getDeleted, 0);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("用户不存在，用户ID: {}", userId);
            throw new BusinessException(ResultCode.USER_NOT_EXISTS);
        }

        // 3. 返回用户信息
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);

        log.info("获取用户信息成功，用户ID: {}", user.getId());
        return userInfoVO;
    }

    /**
     * 退出登录
     */
    public void logout() {
        log.info("退出登录开始");

        // 从 Sa-Token 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();

        // Sa-Token 退出登录
        StpUtil.logout();

        log.info("退出登录成功，用户ID: {}", userId);
    }

    /**
     * 检查是否登录
     *
     * @return 是否登录
     */
    public boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 获取 Token 信息
     *
     * @return Token 信息
     */
    public LoginVO getTokenInfo() {
        log.info("获取Token信息开始");

        if (!StpUtil.isLogin()) {
            log.warn("用户未登录");
            throw new BusinessException("用户未登录");
        }

        // 从 Sa-Token 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();

        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        queryWrapper.eq(User::getDeleted, 0);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("用户不存在，用户ID: {}", userId);
            throw new BusinessException(ResultCode.USER_NOT_EXISTS);
        }

        // 返回 Token 信息
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setTokenName(StpUtil.getTokenName());
        loginVO.setTokenValue(StpUtil.getTokenValue());

        log.info("获取Token信息成功，用户ID: {}", user.getId());
        return loginVO;
    }

}