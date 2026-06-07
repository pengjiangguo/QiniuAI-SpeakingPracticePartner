package com.speakingpractice.partner.statistics.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 练习趋势DTO
 */
@Data
public class PracticeTrendDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 对话次数
     */
    private Integer dialogueCount;

    /**
     * 练习时长（分钟）
     */
    private Integer durationMinutes;

    /**
     * 平均评分
     */
    private Double averageScore;

    /**
     * 新增词汇数
     */
    private Integer newWordsCount;

}