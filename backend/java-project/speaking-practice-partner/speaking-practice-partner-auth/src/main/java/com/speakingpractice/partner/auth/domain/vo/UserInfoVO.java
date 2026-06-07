package com.speakingpractice.partner.auth.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息VO
 */
@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 母语
     */
    private String nativeLanguage;

    /**
     * 英语水平
     */
    private String englishLevel;

    /**
     * 学习目标
     */
    private String learningGoal;

}