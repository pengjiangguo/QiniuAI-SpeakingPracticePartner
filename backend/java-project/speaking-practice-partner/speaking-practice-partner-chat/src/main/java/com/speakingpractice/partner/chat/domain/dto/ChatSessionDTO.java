package com.speakingpractice.partner.chat.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话会话响应对象
 */
@Data
public class ChatSessionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 综合评分
     */
    private Integer overallScore;

    /**
     * 发音评分
     */
    private Integer pronunciationScore;

    /**
     * 语法评分
     */
    private Integer grammarScore;

    /**
     * 词汇评分
     */
    private Integer vocabularyScore;

    /**
     * 流利度评分
     */
    private Integer fluencyScore;

    /**
     * 交互评分
     */
    private Integer interactionScore;

    /**
     * 对话时长（秒）
     */
    private Integer durationSeconds;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 状态 (ACTIVE/PAUSED/ENDED)
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

}