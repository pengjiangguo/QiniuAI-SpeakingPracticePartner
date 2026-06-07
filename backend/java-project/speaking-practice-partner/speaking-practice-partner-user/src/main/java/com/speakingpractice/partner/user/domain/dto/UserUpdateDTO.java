package com.speakingpractice.partner.user.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息更新DTO
 */
@Data
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
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
    @Size(max = 200, message = "学习目标长度不能超过200个字符")
    private String learningGoal;

}
