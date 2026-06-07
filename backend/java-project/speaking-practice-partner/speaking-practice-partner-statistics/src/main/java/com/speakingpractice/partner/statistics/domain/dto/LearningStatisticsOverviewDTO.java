package com.speakingpractice.partner.statistics.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 学习统计概览DTO
 */
@Data
public class LearningStatisticsOverviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总对话次数
     */
    private Integer totalDialogues;

    /**
     * 总练习时长（分钟）
     */
    private Integer totalMinutes;

    /**
     * 平均评分
     */
    private Double averageScore;

    /**
     * 新增词汇数
     */
    private Integer newWords;

    /**
     * 已掌握词汇数
     */
    private Integer masteredWords;

    /**
     * 学习中词汇数
     */
    private Integer learningWords;

    /**
     * 需复习词汇数
     */
    private Integer needReviewWords;

    /**
     * 连续学习天数
     */
    private Integer consecutiveDays;

    /**
     * 本周对话次数
     */
    private Integer weekDialogues;

    /**
     * 本月对话次数
     */
    private Integer monthDialogues;

}