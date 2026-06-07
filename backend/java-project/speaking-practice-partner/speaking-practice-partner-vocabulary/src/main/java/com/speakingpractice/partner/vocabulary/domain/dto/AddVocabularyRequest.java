package com.speakingpractice.partner.vocabulary.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加词汇请求
 */
@Data
public class AddVocabularyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单词
     */
    @NotBlank(message = "单词不能为空")
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
    @NotBlank(message = "中文释义不能为空")
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
     * 来源对话ID
     */
    private String chatSessionId;

    /**
     * 难度等级 (A1-C2)
     */
    private String difficulty;

    /**
     * 备注
     */
    private String notes;

}