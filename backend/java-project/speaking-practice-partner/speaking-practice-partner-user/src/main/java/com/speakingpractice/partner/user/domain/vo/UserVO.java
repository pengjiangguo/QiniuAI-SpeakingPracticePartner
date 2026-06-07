package com.speakingpractice.partner.user.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String id;

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

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

}