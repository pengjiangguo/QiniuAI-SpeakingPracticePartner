package com.speakingpractice.partner.user.service;

import com.speakingpractice.partner.user.domain.dto.UserLoginDTO;
import com.speakingpractice.partner.user.domain.dto.UserRegisterDTO;
import com.speakingpractice.partner.user.domain.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 用户信息
     */
    UserVO register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 用户信息
     */
    UserVO login(UserLoginDTO loginDTO);

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(String userId);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserVO getUserByUsername(String username);

}