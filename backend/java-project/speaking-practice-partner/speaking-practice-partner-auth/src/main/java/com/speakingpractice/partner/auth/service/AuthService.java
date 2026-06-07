package com.speakingpractice.partner.auth.service;

import com.speakingpractice.partner.auth.domain.dto.LoginDTO;
import com.speakingpractice.partner.auth.domain.dto.RegisterDTO;
import com.speakingpractice.partner.auth.domain.vo.LoginVO;
import com.speakingpractice.partner.auth.domain.vo.UserInfoVO;
import com.speakingpractice.partner.auth.util.JwtUtil;
import com.speakingpractice.partner.common.enums.ResultCode;
import com.speakingpractice.partner.common.exception.BusinessException;
import com.speakingpractice.partner.user.domain.entity.User;
import com.speakingpractice.partner.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录响应
     */
    public LoginVO login(LoginDTO loginDTO) {
        log.info("用户登录开始，用户名: {}", loginDTO.getUsername());

        // 1. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        queryWrapper.eq(User::getDeleted, 0);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("用户不存在: {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.USER_NOT_EXISTS);
        }

        // 2. 检查用户状态
        if (user.getStatus() == 0) {
            log.warn("用户已禁用: {}", loginDTO.getUsername());
            throw new BusinessException("用户已禁用，请联系管理员");
        }

        // 3. 验证密码
        boolean passwordMatches = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        if (!passwordMatches) {
            log.warn("密码错误，用户名: {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 4. 生成JWT令牌
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        LocalDateTime expiresAt = jwtUtil.getExpirationTime(accessToken);

        // 5. 返回登录信息
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresAt(expiresAt);

        log.info("用户登录成功，用户ID: {}", user.getId());
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

        // 3. 加密密码
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
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

        // 6. 生成JWT令牌
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        LocalDateTime expiresAt = jwtUtil.getExpirationTime(accessToken);

        // 7. 返回登录信息
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresAt(expiresAt);

        log.info("用户注册成功，用户ID: {}", user.getId());
        return loginVO;
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    public LoginVO refreshToken(String refreshToken) {
        log.info("刷新令牌开始");

        // 1. 验证刷新令牌
        if (!jwtUtil.validateToken(refreshToken)) {
            log.warn("刷新令牌无效");
            throw new BusinessException("刷新令牌无效");
        }

        // 2. 从令牌中获取用户ID
        String userId = jwtUtil.getUserIdFromToken(refreshToken);

        // 3. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        queryWrapper.eq(User::getDeleted, 0);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("用户不存在，用户ID: {}", userId);
            throw new BusinessException(ResultCode.USER_NOT_EXISTS);
        }

        // 4. 检查用户状态
        if (user.getStatus() == 0) {
            log.warn("用户已禁用，用户ID: {}", userId);
            throw new BusinessException("用户已禁用，请联系管理员");
        }

        // 5. 生成新的访问令牌
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername());
        LocalDateTime expiresAt = jwtUtil.getExpirationTime(accessToken);

        // 6. 返回新的令牌信息
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken); // 刷新令牌保持不变
        loginVO.setExpiresAt(expiresAt);

        log.info("刷新令牌成功，用户ID: {}", user.getId());
        return loginVO;
    }

    /**
     * 获取用户信息
     *
     * @param token 访问令牌
     * @return 用户信息
     */
    public UserInfoVO getUserInfo(String token) {
        log.info("获取用户信息开始");

        // 1. 验证令牌
        if (!jwtUtil.validateToken(token)) {
            log.warn("访问令牌无效");
            throw new BusinessException("访问令牌无效");
        }

        // 2. 从令牌中获取用户ID
        String userId = jwtUtil.getUserIdFromToken(token);

        // 3. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        queryWrapper.eq(User::getDeleted, 0);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("用户不存在，用户ID: {}", userId);
            throw new BusinessException(ResultCode.USER_NOT_EXISTS);
        }

        // 4. 返回用户信息
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);

        log.info("获取用户信息成功，用户ID: {}", user.getId());
        return userInfoVO;
    }

    /**
     * 验证令牌
     *
     * @param token 访问令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

}