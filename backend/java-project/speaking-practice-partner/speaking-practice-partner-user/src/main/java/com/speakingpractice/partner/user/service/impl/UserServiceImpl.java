package com.speakingpractice.partner.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.speakingpractice.partner.common.enums.ResultCode;
import com.speakingpractice.partner.common.exception.BusinessException;
import com.speakingpractice.partner.user.domain.dto.UserLoginDTO;
import com.speakingpractice.partner.user.domain.dto.UserRegisterDTO;
import com.speakingpractice.partner.user.domain.entity.User;
import com.speakingpractice.partner.user.domain.vo.UserVO;
import com.speakingpractice.partner.user.mapper.UserMapper;
import com.speakingpractice.partner.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserVO register(UserRegisterDTO registerDTO) {
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

        log.info("用户注册成功，用户ID: {}", user.getId());

        // 6. 返回用户信息
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 用户信息
     */
    @Override
    public UserVO login(UserLoginDTO loginDTO) {
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
        boolean passwordMatches = BCrypt.checkpw(loginDTO.getPassword(), user.getPassword());
        if (!passwordMatches) {
            log.warn("密码错误，用户名: {}", loginDTO.getUsername());
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        log.info("用户登录成功，用户ID: {}", user.getId());

        // 4. 返回用户信息
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public UserVO getUserById(String userId) {
        log.info("查询用户信息，用户ID: {}", userId);

        // 1. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        queryWrapper.eq(User::getDeleted, 0);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("用户不存在，用户ID: {}", userId);
            throw new BusinessException(ResultCode.USER_NOT_EXISTS);
        }

        // 2. 返回用户信息
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public UserVO getUserByUsername(String username) {
        log.info("查询用户信息，用户名: {}", username);

        // 1. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        queryWrapper.eq(User::getDeleted, 0);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            log.warn("用户不存在，用户名: {}", username);
            throw new BusinessException(ResultCode.USER_NOT_EXISTS);
        }

        // 2. 返回用户信息
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

}