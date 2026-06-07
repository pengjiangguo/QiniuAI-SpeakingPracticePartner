package com.speakingpractice.partner.statistics.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 统计查询请求
 */
@Data
public class StatisticsQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 场景ID（可选）
     */
    private String sceneId;

    /**
     * 页码（用于最近练习记录）
     */
    private Integer pageNum;

    /**
     * 页大小（用于最近练习记录）
     */
    private Integer pageSize;

}