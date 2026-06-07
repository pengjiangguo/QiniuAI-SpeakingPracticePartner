package com.speakingpractice.partner.statistics.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 最近练习记录DTO
 */
@Data
public class RecentPracticeRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 日期
     */
    private LocalDateTime date;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 时长（分钟）
     */
    private Integer durationMinutes;

    /**
     * 评分
     */
    private Integer score;

    /**
     * 词汇数
     */
    private Integer wordsCount;

    /**
     * 消息数
     */
    private Integer messageCount;

}