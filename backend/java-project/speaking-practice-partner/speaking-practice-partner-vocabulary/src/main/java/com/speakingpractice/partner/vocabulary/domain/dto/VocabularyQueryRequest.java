package com.speakingpractice.partner.vocabulary.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询词汇请求
 */
@Data
public class VocabularyQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键词（单词或释义）
     */
    private String keyword;

    /**
     * 掌握程度 (0-未学习 1-学习中 2-已掌握 3-需复习)
     */
    private Integer masteryLevel;

    /**
     * 难度等级 (A1-C2)
     */
    private String difficulty;

    /**
     * 来源场景ID
     */
    private String sceneId;

    /**
     * 是否需要复习
     */
    private Boolean needReview;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 20;

}