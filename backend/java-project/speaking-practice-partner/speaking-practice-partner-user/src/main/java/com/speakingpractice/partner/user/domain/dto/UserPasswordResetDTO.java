package com.speakingpractice.partner.user.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户密码重置DTO
 */
@Data
public class UserPasswordResetDTO implements Serializable {

    /**
     * 原密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}
