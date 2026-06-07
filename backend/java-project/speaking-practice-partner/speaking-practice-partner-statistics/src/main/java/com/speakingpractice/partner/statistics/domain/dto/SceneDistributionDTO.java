package com.speakingpractice.partner.statistics.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 场景分布DTO
 */
@Data
public class SceneDistributionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 对话次数
     */
    private Integer dialogueCount;

    /**
     * 占比（百分比）
     */
    private Double percentage;

    /**
     * 平均评分
     */
    private Double averageScore;

    /**
     * 总练习时长（分钟）
     */
    private Integer totalDuration;

}