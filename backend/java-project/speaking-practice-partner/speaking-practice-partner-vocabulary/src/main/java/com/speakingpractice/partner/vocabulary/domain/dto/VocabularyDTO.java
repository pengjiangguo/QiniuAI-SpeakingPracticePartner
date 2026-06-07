package com.speakingpractice.partner.vocabulary.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 词汇响应对象
 */
@Data
public class VocabularyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 词汇ID
     */
    private String id;

    /**
     * 单词
     */
    private String word;

    /**
     * 音标（美式）
     */
    private String phoneticUs;

    /**
     * 音标（英式）
     */
    private String phoneticUk;

    /**
     * 词性
     */
    private String partOfSpeech;

    /**
     * 中文释义
     */
    private String meaningCn;

    /**
     * 英文释义
     */
    private String meaningEn;

    /**
     * 例句（JSON数组）
     */
    private String examples;

    /**
     * 来源场景ID
     */
    private String sceneId;

    /**
     * 来源场景名称
     */
    private String sceneName;

    /**
     * 难度等级 (A1-C2)
     */
    private String difficulty;

    /**
     * 掌握程度 (0-未学习 1-学习中 2-已掌握 3-需复习)
     */
    private Integer masteryLevel;

    /**
     * 学习次数
     */
    private Integer learnCount;

    /**
     * 复习次数
     */
    private Integer reviewCount;

    /**
     * 正确次数
     */
    private Integer correctCount;

    /**
     * 错误次数
     */
    private Integer wrongCount;

    /**
     * 正确率
     */
    private Double correctRate;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastLearnAt;

    /**
     * 下次复习时间
     */
    private LocalDateTime nextReviewAt;

    /**
     * 备注
     */
    private String notes;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

}