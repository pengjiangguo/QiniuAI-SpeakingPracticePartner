package com.speakingpractice.partner.chat.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建对话会话请求
 */
@Data
public class CreateChatSessionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

}