package com.speakingpractice.partner.common.util;

import cn.hutool.core.util.IdUtil;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 */
@Slf4j
@Component
public class CaptchaUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 验证码前缀
     */
    private static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 验证码有效期（单位：秒）
     */
    private static final long CAPTCHA_EXPIRATION = 300; // 5分钟

    /**
     * 生成验证码
     *
     * @return 验证码信息
     */
    public CaptchaInfo generateCaptcha() {
        // 生成验证码key
        String captchaKey = IdUtil.simpleUUID();

        // 生成验证码（3位数字+字母）
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        captcha.setCharType(Captcha.TYPE_DEFAULT);

        // 获取验证码文本
        String captchaText = captcha.text().toLowerCase();

        // 存储验证码到Redis
        String redisKey = CAPTCHA_PREFIX + captchaKey;
        stringRedisTemplate.opsForValue().set(redisKey, captchaText, CAPTCHA_EXPIRATION, TimeUnit.SECONDS);

        log.info("生成验证码成功，key: {}, text: {}", captchaKey, captchaText);

        // 返回验证码信息
        CaptchaInfo captchaInfo = new CaptchaInfo();
        captchaInfo.setCaptchaKey(captchaKey);
        captchaInfo.setCaptchaImage(captcha.toBase64());

        return captchaInfo;
    }

    /**
     * 验证验证码
     *
     * @param captchaKey 验证码key
     * @param captchaCode 验证码
     * @return 是否验证成功
     */
    public boolean validateCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            log.warn("验证码参数为空");
            return false;
        }

        // 从Redis获取验证码
        String redisKey = CAPTCHA_PREFIX + captchaKey;
        String storedCaptcha = stringRedisTemplate.opsForValue().get(redisKey);

        if (storedCaptcha == null) {
            log.warn("验证码不存在或已过期，key: {}", captchaKey);
            return false;
        }

        // 验证码比对（不区分大小写）
        boolean isValid = storedCaptcha.equalsIgnoreCase(captchaCode);

        if (isValid) {
            // 验证成功，删除验证码
            stringRedisTemplate.delete(redisKey);
            log.info("验证码验证成功，key: {}", captchaKey);
        } else {
            log.warn("验证码验证失败，key: {}, expected: {}, actual: {}", captchaKey, storedCaptcha, captchaCode);
        }

        return isValid;
    }

    /**
     * 验证码信息
     */
    public static class CaptchaInfo {
        /**
         * 验证码key
         */
        private String captchaKey;

        /**
         * 验证码图片（Base64）
         */
        private String captchaImage;

        public String getCaptchaKey() {
            return captchaKey;
        }

        public void setCaptchaKey(String captchaKey) {
            this.captchaKey = captchaKey;
        }

        public String getCaptchaImage() {
            return captchaImage;
        }

        public void setCaptchaImage(String captchaImage) {
            this.captchaImage = captchaImage;
        }
    }

}