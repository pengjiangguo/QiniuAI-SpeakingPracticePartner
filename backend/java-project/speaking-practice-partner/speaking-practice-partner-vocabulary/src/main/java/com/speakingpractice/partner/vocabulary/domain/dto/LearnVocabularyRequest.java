package com.speakingpractice.partner.vocabulary.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 学习词汇请求
 */
@Data
public class LearnVocabularyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 词汇ID
     */
    @NotBlank(message = "词汇ID不能为空")
    private String vocabularyId;

    /**
     * 学习类型 (LEARN-学习 REVIEW-复习 TEST-测试)
     */
    @NotBlank(message = "学习类型不能为空")
    private String learnType;

    /**
     * 学习方式 (READ-阅读 LISTEN-听力 SPEAK-口语 WRITE-拼写)
     */
    @NotBlank(message = "学习方式不能为空")
    private String learnMethod;

    /**
     * 是否正确 (true-正确 false-错误)
     */
    @NotNull(message = "是否正确不能为空")
    private Boolean isCorrect;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 学习时长（秒）
     */
    private Integer duration;

}