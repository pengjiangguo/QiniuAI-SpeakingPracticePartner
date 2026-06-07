package com.speakingpractice.partner.chat.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新对话会话评分请求
 */
@Data
public class UpdateChatSessionScoreRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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

}