package com.speakingpractice.partner.auth.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 验证码响应VO
 */
@Data
public class CaptchaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 验证码key
     */
    private String captchaKey;

    /**
     * 验证码图片（Base64）
     */
    private String captchaImage;

}