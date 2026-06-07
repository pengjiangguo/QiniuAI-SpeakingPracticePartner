package com.speakingpractice.partner.chat.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询对话会话请求
 */
@Data
public class ChatSessionQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 状态 (ACTIVE/PAUSED/ENDED)
     */
    private String status;

    /**
     * 开始时间范围（开始）
     */
    private String startTimeFrom;

    /**
     * 开始时间范围（结束）
     */
    private String startTimeTo;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 20;

}