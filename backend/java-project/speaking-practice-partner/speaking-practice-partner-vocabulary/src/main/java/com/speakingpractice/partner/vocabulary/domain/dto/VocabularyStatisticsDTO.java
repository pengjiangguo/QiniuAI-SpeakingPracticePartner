package com.speakingpractice.partner.vocabulary.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 词汇统计响应对象
 */
@Data
public class VocabularyStatisticsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总词汇数
     */
    private Integer totalVocabularies;

    /**
     * 未学习词汇数
     */
    private Integer unlearnedCount;

    /**
     * 学习中词汇数
     */
    private Integer learningCount;

    /**
     * 已掌握词汇数
     */
    private Integer masteredCount;

    /**
     * 需复习词汇数
     */
    private Integer needReviewCount;

    /**
     * 今日学习词汇数
     */
    private Integer todayLearnedCount;

    /**
     * 今日复习词汇数
     */
    private Integer todayReviewedCount;

    /**
     * 总学习次数
     */
    private Integer totalLearnCount;

    /**
     * 总复习次数
     */
    private Integer totalReviewCount;

    /**
     * 平均正确率
     */
    private Double averageCorrectRate;

    /**
     * 学习时长（分钟）
     */
    private Integer totalDurationMinutes;

}