package com.speakingpractice.partner.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户业务对象（BO）
 * 用于业务逻辑处理，包含业务相关的字段和方法
 */
@Data
public class UserBO implements Serializable {

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
     * 密码（加密后）
     */
    private String password;

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
     * 英语水平 (A1-C2)
     */
    private String englishLevel;

    /**
     * 学习目标
     */
    private String learningGoal;

    /**
     * 状态 (0-禁用 1-正常)
     */
    private Integer status;

    /**
     * 是否是新用户
     */
    private Boolean isNewUser;

    /**
     * 是否需要水平测试
     */
    private Boolean needLevelTest;

}