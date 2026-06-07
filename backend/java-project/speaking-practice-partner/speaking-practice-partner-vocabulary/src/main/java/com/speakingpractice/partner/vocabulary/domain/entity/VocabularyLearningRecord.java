package com.speakingpractice.partner.vocabulary.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 词汇学习记录实体
 */
@Data
@TableName("vocabulary_learning_records")
public class VocabularyLearningRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 词汇ID
     */
    private String vocabularyId;

    /**
     * 学习类型 (LEARN-学习 REVIEW-复习 TEST-测试)
     */
    private String learnType;

    /**
     * 学习方式 (READ-阅读 LISTEN-听力 SPEAK-口语 WRITE-拼写)
     */
    private String learnMethod;

    /**
     * 是否正确 (true-正确 false-错误)
     */
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

    /**
     * 学习时间
     */
    private LocalDateTime learnAt;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 是否删除 (0-否 1-是)
     */
    @TableLogic
    private Integer deleted;

}