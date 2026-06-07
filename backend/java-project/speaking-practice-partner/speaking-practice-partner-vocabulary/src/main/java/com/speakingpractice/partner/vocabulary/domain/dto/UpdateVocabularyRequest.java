package com.speakingpractice.partner.vocabulary.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新词汇请求
 */
@Data
public class UpdateVocabularyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 难度等级 (A1-C2)
     */
    private String difficulty;

    /**
     * 掌握程度 (0-未学习 1-学习中 2-已掌握 3-需复习)
     */
    private Integer masteryLevel;

    /**
     * 备注
     */
    private String notes;

}