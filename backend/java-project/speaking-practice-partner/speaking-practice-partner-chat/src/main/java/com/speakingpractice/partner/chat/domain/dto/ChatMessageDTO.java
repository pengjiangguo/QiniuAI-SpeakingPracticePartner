package com.speakingpractice.partner.chat.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话消息响应对象
 */
@Data
public class ChatMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String id;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 角色 (USER/ASSISTANT/SYSTEM)
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 音频路径
     */
    private String audioPath;

    /**
     * ASR识别置信度
     */
    private Double asrConfidence;

    /**
     * 发音评分
     */
    private Integer pronunciationScore;

    /**
     * 语法错误数量
     */
    private Integer grammarErrorCount;

    /**
     * 是否已纠错 (0-否 1-是)
     */
    private Integer corrected;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

}